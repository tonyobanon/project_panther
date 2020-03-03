package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.Date;
import java.util.Map;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.TimeUnit;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.integrated.realms.AdminRealm;
import com.re.paas.integrated.tables.defs.payments.CurrencyRatesTable;
import com.re.paas.integrated.tables.spec.payments.CurrencyRatesTableSpec;
import com.re.paas.integrated.tables.spec.users.UserRoleTableSpec;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.classes.spec.TokenCredentials;
import com.re.paas.internal.core.keys.ConfigKeys;

public class CurrencyModel extends BaseModel {

	private static TokenCredentials credentials = null;

	@Override
	public String path() {
		return "ext/services/currency";
	}

	@Override
	public void preInstall() {

		// Create configuration fields

		String sectionId = FormModel.newSection(ClientRBRef.get("currency_layer_api_settings"), null,
				FormSectionType.SYSTEM_CONFIGURATION, new AdminRealm());

		ConfigModel.putString(ConfigKeys.CURRENCY_LAYER_FORM_SECTION_ID, sectionId);

		String urlField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("url")));

		String accessTokenField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("access_token")));

		String refreshIntervalField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("refresh_interval")));

		ConfigModel
				.putAll(new FluentHashMap<String, String>().with(ConfigKeys.CURRENCY_LAYER_API_URL_FIELD_ID, urlField)
						.with(ConfigKeys.CURRENCY_LAYER_API_ACCESS_TOKEN_FIELD_ID, accessTokenField)
						.with(ConfigKeys.CURRENCY_LAYER_REFRESH_INTERVAL_FIELD_ID, refreshIntervalField));

		ConfigModel.putAll(new FluentHashMap<String, String>()
				.with(urlField, "http://www.apilayer.net/api/live")
				.with(accessTokenField, "5f8ea7f3810655cc37e9ba486a217559").with(refreshIntervalField, "30"));

		start();
	}

	@Override
	public void start() {
		
		String defaultCurrency = defaultCurrency();
		
		// Set the forex rates for all supported countries
		RBModel.availableCountries().forEach(c -> {
			if (!c.equals(defaultCurrency)) {
				getCurrencyRate(defaultCurrency, c);
			}
		});

	}

	protected static Double getCurrencyRate(String currency) {
		return getCurrencyRate(ConfigModel.get(ConfigKeys.DEFAULT_CURRENCY).toString(), currency);
	}

	public static Double getCurrencyRate(String base, String currency) {

		if (base.equals(currency)) {
			return 1.0;
		}

		String pair = base + currency;
		Integer refreshInterval = getRefreshInterval();

		Date lastUpdated = getLastUpdated(pair);
		
		if(lastUpdated != null) {
			boolean minutesElapsed = Math.abs(Utils.getTimeOffset(TimeUnit.MINUTES, lastUpdated)) > refreshInterval;
			if(!minutesElapsed) {
				return getRate(pair);
			}
		}

		Double rate = fetchCurrencyRate(base, currency);
		updateRate(pair, rate);

		return rate;
	}
	
	private static Date getLastUpdated(String pair) {
		Item item = get(pair, CurrencyRatesTableSpec.LAST_UPDATED);
		return item != null ? item.getDate(CurrencyRatesTableSpec.LAST_UPDATED) : null;
	}
	
	private static Double getRate(String pair) {
		return get(pair, CurrencyRatesTableSpec.RATE).getDouble(CurrencyRatesTableSpec.RATE);
	}
	
	private static void updateRate(String pair, Double rate) {
		Table t = Database.get().getTable(CurrencyRatesTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(S(CurrencyRatesTableSpec.PAIR).eq(pair))
				.addUpdate(N(CurrencyRatesTableSpec.RATE).set(rate))
				.addUpdate(D(UserRoleTableSpec.DATE_UPDATED).set(Dates.now()))
				.buildForUpdate();

		t.updateItem(spec);
	}
	
	private static Item get(String pair, String... projections) {

		Table t = Database.get().getTable(CurrencyRatesTable.class);
		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(projections)
				.withCondition(S(CurrencyRatesTableSpec.PAIR).eq(pair));

		GetItemSpec spec = expr.buildForGetItem();
		return t.getItem(spec);
	}

	private static Integer getRefreshInterval() {
		Map<String, String> keys = ConfigModel.getAll(ConfigKeys.CURRENCY_LAYER_REFRESH_INTERVAL_FIELD_ID);

		Map<String, String> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String refreshInterval = values.get(keys.get(ConfigKeys.CURRENCY_LAYER_REFRESH_INTERVAL_FIELD_ID));
		return Integer.parseInt(refreshInterval);
	}

	private static TokenCredentials _getCredentials() {

		Map<String, String> keys = ConfigModel.getAll(ConfigKeys.CURRENCY_LAYER_API_URL_FIELD_ID,
				ConfigKeys.CURRENCY_LAYER_API_ACCESS_TOKEN_FIELD_ID);

		Map<String, String> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String url = values.get(keys.get(ConfigKeys.CURRENCY_LAYER_API_URL_FIELD_ID));
		String token = values.get(keys.get(ConfigKeys.CURRENCY_LAYER_API_ACCESS_TOKEN_FIELD_ID));

		return CurrencyModel.credentials = new TokenCredentials().setToken(token).setUrl(url);
	}

	private static TokenCredentials getCredentials() {
		return credentials != null ? credentials : _getCredentials();
	}

	private static Double fetchCurrencyRate(String base, String currency) {

		TokenCredentials credentials = getCredentials();

		try {

			Content content = Request.Get(credentials.getUrl() + "?access_key=" + credentials.getToken() + "&source="
					+ base + "&currencies=" + currency).execute().returnContent();

			JsonObject response = new JsonObject(content.asString());

			return response.getJsonObject("quotes").getDouble(base + currency);

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	protected static String defaultCurrency() {
		return ConfigModel.get(ConfigKeys.DEFAULT_CURRENCY);
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
