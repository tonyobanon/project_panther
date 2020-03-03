package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.attribute_not_exists;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.ConditionalCheckFailedException;
import com.re.paas.api.infra.database.model.ReturnValue;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.tables.defs.base.ConfigTable;
import com.re.paas.integrated.tables.spec.base.ConfigTableSpec;
import com.re.paas.internal.core.keys.ConfigKeys;

@Todo("Add functionality, that allow incremental updates to config parameters from the frontend, "
		+ "i.e uploading a config file")
@BlockerTodo("Use Ibm Icu as the default locale provider")
public class ConfigModel extends BaseModel {

	@Override
	public String path() {
		return "core/config";
	}

	@Override
	public void preInstall() {
	}

	@Override
	public void install(InstallOptions options) {

		ConfigModel.putString(ConfigKeys.ORGANIZATION_NAME, options.getCompanyName());
		ConfigModel.putString(ConfigKeys.ORGANIZATION_LOGO_URL, options.getCompanyLogoUrl());
		ConfigModel.putString(ConfigKeys.ORGANIZATION_COUNTRY, options.getCountry());
		ConfigModel.putString(ConfigKeys.ORGANIZATION_AUDIENCE, options.getAudience());

		ConfigModel.putString(ConfigKeys.DEFAULT_CURRENCY, options.getCurrency());
		ConfigModel.putString(ConfigKeys.DEFAULT_TIMEZONE, options.getTimezone());

	}

	public static Map<String, String> getAll(String... keys) {
		return getAll(Arrays.asList(keys));
	}

	public static Map<String, String> getAll(List<String> keys) {
		Map<String, String> result = new HashMap<String, String>(keys.size());

		BatchGetItemRequest request = new BatchGetItemRequest().addRequestItem(ConfigTable.class,
				GetItemsSpec.forKeys(
						keys.stream().map(key -> new PrimaryKey(ConfigTableSpec.KEY, key)).collect(Collectors.toList()),
						ConfigTableSpec.VALUE));

		Database.get().batchGetItem(request).getResponses(ConfigTable.class).forEach(i -> {
			String k = i.getString(ConfigTableSpec.KEY);
			String v = i.getString(ConfigTableSpec.VALUE);

			result.put(k, v);
		});

		keys.forEach(key -> {
			if (!result.containsKey(key)) {
				result.put(key, null);
			}
		});

		return result;
	}

	public static String get(String key) {
		Item item = Database.get().getTable(ConfigTable.class)
				.getItem(GetItemSpec.forKey(ConfigTableSpec.KEY, key, ConfigTableSpec.VALUE));
		return item != null ? item.getString(ConfigTableSpec.VALUE) : null;
	}

	public static Number getInt(String key) {
		Item item = Database.get().getTable(ConfigTable.class)
				.getItem(GetItemSpec.forKey(ConfigTableSpec.KEY, key, ConfigTableSpec.INT_VALUE));
		return item != null ? item.getNumber(ConfigTableSpec.INT_VALUE) : null;
	}

	public static Boolean putString(String key, String value) {
		return putString(key, value, false);
	}

	public static Boolean putString(String key, String value, boolean conditionally) {
		return put(key, ConfigTableSpec.VALUE, value, conditionally);
	}

	public static Boolean putInt(String key, Number value) {
		return putInt(key, value, false);
	}

	public static Boolean putInt(String key, Number value, boolean conditionally) {
		return put(key, ConfigTableSpec.INT_VALUE, value, conditionally);
	}

	public static Number incr(String key) {

		Date now = Dates.now();

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder()
				.addUpdate(N(ConfigTableSpec.INT_VALUE).set(N(ConfigTableSpec.INT_VALUE).plus(1)))
				.addUpdate(D(ConfigTableSpec.DATE_UPDATED).set(now));

		UpdateItemSpec spec = expr.buildForUpdate().withReturnValues(ReturnValue.ALL_NEW);

		return Database.get().getTable(ConfigTable.class).updateItem(spec).getItem()
				.getNumber(ConfigTableSpec.INT_VALUE);
	}

	public static Number decr(String key) {

		Date now = Dates.now();

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder()
				.addUpdate(N(ConfigTableSpec.INT_VALUE).set(N(ConfigTableSpec.INT_VALUE).minus(1)))
				.addUpdate(D(ConfigTableSpec.DATE_UPDATED).set(now));

		UpdateItemSpec spec = expr.buildForUpdate().withReturnValues(ReturnValue.ALL_NEW);

		return Database.get().getTable(ConfigTable.class).updateItem(spec).getItem()
				.getNumber(ConfigTableSpec.INT_VALUE);
	}

	public static boolean put(String key, String valueAttr, Object value, boolean conditionally) {

		Date now = Dates.now();
		ExpressionSpecBuilder expr = new ExpressionSpecBuilder();

		if (conditionally) {
			expr.withCondition(attribute_not_exists(ConfigTableSpec.KEY));
		}

		Item item = new Item().withString(ConfigTableSpec.KEY, key).with(valueAttr, value)
				.withDate(ConfigTableSpec.DATE_CREATED, now).withDate(ConfigTableSpec.DATE_UPDATED, now);

		try {
			Database.get().getTable(ConfigTable.class).putItem(expr.buildForPut().withItem(item));
			return true;
		} catch (ConditionalCheckFailedException e) {
			return false;
		}
	}

	public static void putAll(Map<String, String> values) {

		Date now = Dates.now();
		BatchWriteItemRequest req = new BatchWriteItemRequest();

		values.forEach((key, value) -> {
			Item item = new Item().withString(ConfigTableSpec.KEY, key).withString(ConfigTableSpec.VALUE, value)
					.withDate(ConfigTableSpec.DATE_CREATED, now).withDate(ConfigTableSpec.DATE_UPDATED, now);
			req.addRequestItem(ConfigTable.class, new WriteRequest(item));
		});

		Database.get().batchWriteItem(req);
	}

	public static void delete(String key) {
		Database.get().getTable(ConfigTable.class).deleteItem(DeleteItemSpec.forKey(ConfigTableSpec.KEY, key));
	}

	@Override
	public void start() {
	}

	@Override
	public void update() {
	}

	@Override
	public void unInstall() {
	}

}
