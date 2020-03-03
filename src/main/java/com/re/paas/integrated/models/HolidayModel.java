package com.re.paas.integrated.models;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.re.paas.api.Platform;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.models.helpers.CacheHelper;
import com.re.paas.integrated.realms.AdminRealm;
import com.re.paas.integrated.tables.defs.locations.PublicHolidayTable;
import com.re.paas.integrated.tables.spec.locations.PublicHolidayTableSpec;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.BackendObjectMarshaller;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.classes.spec.PublicHolidaySpec;
import com.re.paas.internal.classes.spec.TokenCredentials;
import com.re.paas.internal.core.keys.ConfigKeys;

@Model(dependencies = { RBModel.class })
public class HolidayModel extends BaseModel {

	private static final String DELIM = "__";

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

		ConfigModel.putString(ConfigKeys.HOLIDAY_API_FORM_SECTION_ID, sectionId);

		String urlField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("url")));

		String accessKeyField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("access_key")));

		ConfigModel.putAll(new FluentHashMap<String, String>().with(ConfigKeys.HOLIDAY_API_URL_FIELD_ID, urlField)
				.with(ConfigKeys.HOLIDAY_API_KEY_FIELD_ID, accessKeyField));

		ConfigModel.putAll(new FluentHashMap<String, String>().with(urlField, "https://holidayapi.com/v1/holidays")
				.with(accessKeyField, "1b1f2382-b4f0-4358-98e7-0149808f86e8"));

		if (Platform.isProduction()) {

			// Fetch holidays data for all available countries
			RBModel.availableCountries().forEach(c -> {
				fetchHolidays(c, false);
			});

		}
	}

	@Override
	public void start() {

		// Fetch holidays data for available countries that do not yet have one

		RBModel.availableCountries().forEach(c -> {
			fetchHolidays(c, true);
		});
	}

	private static TokenCredentials _getCredentials() {

		Map<String, String> keys = ConfigModel.getAll(ConfigKeys.HOLIDAY_API_URL_FIELD_ID,
				ConfigKeys.HOLIDAY_API_KEY_FIELD_ID);

		Map<String, String> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String url = values.get(keys.get(ConfigKeys.HOLIDAY_API_URL_FIELD_ID));
		String accessKey = values.get(keys.get(ConfigKeys.HOLIDAY_API_KEY_FIELD_ID));

		return HolidayModel.credentials = new TokenCredentials().setToken(accessKey).setUrl(url);
	}

	private static TokenCredentials getCredentials() {
		return credentials != null ? credentials : _getCredentials();
	}

	protected static PublicHolidaySpec getHoliday(String country, Calendar calendar) {

		String key = getCacheKey(country, calendar);

		Object value = CacheAdapter.get(CacheType.PERSISTENT, key);

		if (value != null) {
			String[] arr = value.toString().split(DELIM);

			String name = arr[0];
			Date date = BackendObjectMarshaller.unmarshalDate(arr[1]);
			Boolean isPublic = BackendObjectMarshaller.unmarshalBool(arr[2]);

			return new PublicHolidaySpec().setName(name).setPublic(isPublic).setCountry(country).setDate(date);
		}

		return null;
	}

	private static String getCacheKey(String country, Calendar date) {
		return "holiday" + DELIM + country + DELIM + dateFormat.format(date.getTime());
	}

	private static String getCacheValue(String name, Date date, Boolean isPublic) {
		return name + DELIM + BackendObjectMarshaller.marshal(date) + DELIM + BackendObjectMarshaller.marshal(isPublic);
	}

	private static void fetchHolidays(String country, boolean checkCache) {

		Calendar date = getDate();

		if (checkCache) {
			String cacheKey = getCacheKey(country, date);
			if (CacheAdapter.containsKey(CacheType.PERSISTENT, cacheKey)) {
				return;
			}
		}

		fetchHolidaysFromDbOrService(country, date);
	}

	private static void fetchHolidaysFromService(String country, Calendar date) {

		Integer year = date.get(Calendar.YEAR) - 1;
		TokenCredentials credentials = getCredentials();

		List<PublicHolidaySpec> result = new ArrayList<PublicHolidaySpec>();

		Content content = null;
		try {
			content = Request.Get(
					credentials.getUrl() + "?key=" + credentials.getToken() + "&country=" + country + "&year=" + year)
					.execute().returnContent();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

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

				result.add(spec);
			});
		});

		// Add result to the database

		Date now = Dates.now();

		List<WriteRequest> items = result.stream().map(spec ->

		new WriteRequest(new Item().withString(PublicHolidayTableSpec.NAME, spec.getName())
				.withString(PublicHolidayTableSpec.COUNTRY, spec.getCountry())
				.withBoolean(PublicHolidayTableSpec.IS_PUBLIC, spec.isPublic())
				.withDate(PublicHolidayTableSpec.DATE, spec.getDate())
				.withDate(PublicHolidayTableSpec.DATE_CREATED, now))

		).collect(Collectors.toList());

		Database.get().batchWriteItem(new BatchWriteItemRequest().addRequestItems(PublicHolidayTable.class, items));

		// Add result to cache
		result.forEach(spec -> addHolidayToCache(country, date, spec));
	}

	private static void fetchHolidaysFromDbOrService(String country, Calendar date) {

		List<Long> ids = new ArrayList<>();

		Database.get().getTable(PublicHolidayTable.class).getIndex(PublicHolidayTableSpec.COUNTRY_INDEX)
				.all(QuerySpec.get(PublicHolidayTableSpec.COUNTRY, country, PublicHolidayTableSpec.DATE,
						new Date[] { date.getTime() }, PublicHolidayTableSpec.ID))
				.forEach(i -> {
					ids.add(i.getLong(PublicHolidayTableSpec.ID));
				});

		Collection<PrimaryKey> keys = ids.stream().map(id -> new PrimaryKey(PublicHolidayTableSpec.ID, id))
				.collect(Collectors.toList());

		if (keys.isEmpty()) {

			// This does not exist in DB, fetch from service, update DB and cache as well
			fetchHolidaysFromService(country, date);
			return;
		}

		// Fetch from DB, and add to cache

		Database.get().batchGetItem(

				new BatchGetItemRequest().addRequestItem(PublicHolidayTable.class,
						GetItemsSpec.forKeys(keys, PublicHolidayTableSpec.NAME, PublicHolidayTableSpec.DATE,
								PublicHolidayTableSpec.IS_PUBLIC)))

				.getResponses(PublicHolidayTable.class).forEach(i -> {
					PublicHolidaySpec spec = getHolidaySpec(i);
					addHolidayToCache(country, date, spec);
				});
	}

	private static void addHolidayToCache(String country, Calendar date, PublicHolidaySpec spec) {
		CacheHelper.addToList(CacheType.PERSISTENT, getCacheKey(country, date),
				getCacheValue(spec.getName(), spec.getDate(), spec.isPublic()));
	}

	private static PublicHolidaySpec getHolidaySpec(Item i) {
		return new PublicHolidaySpec().setId(i.getLong(PublicHolidayTableSpec.ID))
				.setName(i.getString(PublicHolidayTableSpec.NAME))
				.setCountry(i.getString(PublicHolidayTableSpec.COUNTRY))
				.setPublic(i.getBoolean(PublicHolidayTableSpec.IS_PUBLIC))
				.setDate(i.getDate(PublicHolidayTableSpec.DATE))
				.setDateCreated(i.getDate(PublicHolidayTableSpec.DATE_CREATED));
	}

	private static Calendar getDate() {
		Calendar c = Dates.getCalendar();
		Integer year = c.get(Calendar.YEAR) - 1;
		c.set(year, 0, 1, 0, 0, 0);
		return c;
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
