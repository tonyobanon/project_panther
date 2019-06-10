package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.tables.defs.base.MetricTable;

@Model(version = "0.0.3-rc.1+build.1")
public class MetricsModel extends BaseModel {

	@Override
	public String path() {
		return "core/metrics";
	}

	@Override
	public void update() {

	}

	@Override
	public void preInstall() {

	}

	@Override
	public void install(InstallOptions options) {

	}

	@Override
	@Todo
	public void start() {

		// Start a socket server, that gets written to when metric is updated

		if (!CloudEnvironment.get().isProduction()) {

			// Start a cron job that updates metrics database with test data
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			}, 0, 1, TimeUnit.MINUTES);
		}
	}

	private static void doIncrement(String key, String parentKey) {
		put(key, parentKey, get(key) + 1);
	}

	public static void increment(String key) {

		Calendar currentTime = Calendar.getInstance();

		int yy = currentTime.get(Calendar.YEAR);
		int mm = currentTime.get(Calendar.MONTH);
		int dd = currentTime.get(Calendar.DAY_OF_MONTH);
		int hh = currentTime.get(Calendar.HOUR_OF_DAY);
		int mn = currentTime.get(Calendar.MINUTE);
		int ss = currentTime.get(Calendar.SECOND);

		doIncrement(key + "_" + yy, key);
		doIncrement(key + "_" + yy + "_" + mm, key);
		doIncrement(key + "_" + yy + "_" + mm + "_" + dd, key);
		doIncrement(key + "_" + yy + "_" + mm + "_" + dd + "_" + hh, key);
		doIncrement(key + "_" + yy + "_" + mm + "_" + dd + "_" + hh + "_" + mn, key);
		doIncrement(key + "_" + yy + "_" + mm + "_" + dd + "_" + hh + "_" + mn + "_" + ss, key);

	}

	private static Integer get(String key) {
		MetricTable e = ((MetricTable) ofy().load().type(MetricTable.class).id(key).now());
		return e != null ? e.getValue() : 0;
	}

	private static void put(String key, String parentKey, Integer value) {
		ofy().save().entity(new MetricTable().setKey(key).setParentKey(parentKey).setValue(value)).now();
	}

	public static void clear(String parentKey) {

		List<String> keys = new FluentArrayList<>();

		ofy().load().type(MetricTable.class).filter("parentKey = ", parentKey).forEach((e) -> {
			keys.add(e.getKey());
		});

		delete(keys.toArray(new String[keys.size()]));
	}

	private static void delete(String... keys) {
		List<Key<MetricTable>> keysO = new FluentArrayList<>();

		for (String k : keys) {
			keysO.add(Key.create(MetricTable.class, k));
		}

		ofy().delete().keys(keysO).now();
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
