package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.googlecode.objectify.cmd.QueryKeys;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.classes.spec.PublicHolidaySpec;
import com.re.paas.internal.classes.spec.TokenCredentials;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.models.helpers.CacheHelper;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.tables.locations.PublicHolidayTable;
import com.re.paas.internal.realms.AdminRealm;
import com.re.paas.internal.utils.BackendObjectMarshaller;

@Model(dependencies = { RBModel.class })
public class HolidayModel extends BaseModel {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static TokenCredentials credentials = null;

	@Override
	public String path() {
		return "ext/services/holidays";
	}

	@Override
	public void preInstall() {

		String sectionId = FormModel.newSection(ClientRBRef.get("holiday_api_settings"), null,
				FormSectionType.SYSTEM_CONFIGURATION, new AdminRealm());

		ConfigModel.put(ConfigKeys.HOLIDAY_API_FORM_SECTION_ID, sectionId);

		String urlField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("url")));

		String accessKeyField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("access_key")));

		ConfigModel.putAll(new FluentHashMap<String, Object>().with(ConfigKeys.HOLIDAY_API_URL_FIELD_ID, urlField)
				.with(ConfigKeys.HOLIDAY_API_KEY_FIELD_ID, accessKeyField));

		ConfigModel.putAll(new FluentHashMap<String, Object>().with(urlField, "https://holidayapi.com/v1/holidays")
				.with(accessKeyField, "1b1f2382-b4f0-4358-98e7-0149808f86e8"));
 
		
		if(CloudEnvironment.get().isProduction()) {

			// Fetch holidays data for the available countries
			RBModel.availableCountries().forEach(c -> {
				fetchHolidays(c);
			});
			
		}
	}

	@Override
	public void start() {

		// Fetch holidays data for available countries that do not yet have one

		RBModel.availableCountries().forEach(c -> {
			Boolean isDataAvailable = BackendObjectMarshaller.unmarshalBool(
					ConfigModel.get(ConfigKeys.IS_HOLIDAY_DATA_AVAILABLE_$COUNTRY.replace("$COUNTRY", c)));
			if (!isDataAvailable) {
				fetchHolidays(c);
			}
		});

		// Get all holiday data, and add to cache

	}

	private static TokenCredentials _getCredentials() {

		Map<String, Object> keys = ConfigModel.getAll(ConfigKeys.HOLIDAY_API_URL_FIELD_ID,
				ConfigKeys.HOLIDAY_API_KEY_FIELD_ID);

		Map<String, Object> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String url = (String) values.get(keys.get(ConfigKeys.HOLIDAY_API_URL_FIELD_ID));
		String accessKey = (String) values.get(keys.get(ConfigKeys.HOLIDAY_API_KEY_FIELD_ID));

		return HolidayModel.credentials = new TokenCredentials().setToken(accessKey).setUrl(url);
	}

	private static TokenCredentials getCredentials() {
		return credentials != null ? credentials : _getCredentials();
	}

	protected static PublicHolidaySpec getHoliday(String country, Date date) {

		String key = getCacheKey(country, date);

		Object value = CacheAdapter.get(CacheType.PERSISTENT, key);

		if (value != null) {
			String[] arr = value.toString().split(getDelim());

			String name = arr[0];
			boolean isPublic = BackendObjectMarshaller.unmarshalBool(arr[1]);

			return new PublicHolidaySpec()
					.setName(name)
					.setPublic(isPublic)
					.setCountry(country)
					.setDate(date);
		}

		return null;
	}

	private static String getDelim() {
		return "__";
	}

	private static String getCacheKey(String country, Date date) {
		return "holiday" + getDelim() + country + getDelim() + dateFormat.format(date);
	}

	private static String getCacheValue(String name, boolean isPublic) {
		return name + getDelim() + BackendObjectMarshaller.marshal(isPublic);
	}

	protected static void addHolidaysToCache() {

		QueryKeys<PublicHolidayTable> keys = ofy().load().type(PublicHolidayTable.class).keys();
		ofy().load().type(PublicHolidayTable.class).ids(keys).forEach((k, v) -> {

			CacheHelper.addToList(CacheType.PERSISTENT, getCacheKey(v.getCountry(), v.getDate()),
					getCacheValue(v.getName(), v.isPublic()));
		});
	}

	protected static void fetchHolidays(String country) {

		List<PublicHolidaySpec> holidays = getHolidays(country);

		List<PublicHolidayTable> entities = new ArrayList<>(holidays.size());
		holidays.forEach(h -> {
			entities.add(EntityHelper.fromObjectModel(h));
		});

		ofy().save().entities(entities).now();

		ConfigModel.put(ConfigKeys.IS_HOLIDAY_DATA_AVAILABLE_$COUNTRY.replace("$COUNTRY", country),
				BackendObjectMarshaller.marshal(true));
	}

	@BlockerBlockerTodo("Handle possible failure scenarios, see https://holidayapi.com/")
	protected static List<PublicHolidaySpec> getHolidays(String country) {

		Integer year = Dates.getCalendar().get(Calendar.YEAR) - 1;
		TokenCredentials credentials = getCredentials();

		List<PublicHolidaySpec> result = new ArrayList<PublicHolidaySpec>();

		try {

			Content content = Request.Get(
					credentials.getUrl() + "?key=" + credentials.getToken() + "&country=" + country + "&year=" + year)
					.execute().returnContent();

			JsonObject response = new JsonObject(content.asString());

			JsonObject holidays = response.getJsonObject("holidays");

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			holidays.stream().forEachOrdered(e -> {
				JsonArray v = (JsonArray) e.getValue();
				v.forEach(h -> {

					JsonObject holidayJson = (JsonObject) h;

					PublicHolidaySpec spec = new PublicHolidaySpec();
					spec.setName(holidayJson.getString("name"));
					spec.setCountry(country);
					spec.setPublic(holidayJson.getBoolean("public"));
					try {
						spec.setDate(format.parse(holidayJson.getString("date")));
					} catch (ParseException ex) {
						Exceptions.throwRuntime(ex);
					}
					spec.setDateCreated(Dates.now());

					result.add(spec);
				});
			});

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
			return null;
		}

		return result;
	}

	@Override
	public void install(InstallOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
