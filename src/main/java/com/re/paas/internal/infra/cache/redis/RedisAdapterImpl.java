package com.re.paas.internal.infra.cache.redis;

import static com.re.paas.api.forms.input.InputType.NUMBER;
import static com.re.paas.api.forms.input.InputType.TEXT;

import java.util.Map;

import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class RedisAdapterImpl implements CacheAdapter {

	@Override
	public String name() {
		return "redis";
	}

	@Override
	public String title() {
		return "Redis";
	}

	@Override
	public String iconUrl() {
		return "https://d1q6f0aelx0por.cloudfront.net/product-logos/89e5782a-76ea-4b94-a561-39e331c281a5-redis.png";
	}

	@Override
	public Form initForm() {

		Section section = new Section().setTitle("redis_configuration")

				.withField(new SimpleField("host", TEXT, "host")).withField(new SimpleField("port", NUMBER, "port"))
				.withField(new SimpleField("database", TEXT, "database"))
				.withField(new SimpleField("username", TEXT, "username"))
				.withField(new SimpleField("password", TEXT, "password"))
				.withField(new SimpleField("maxConnections", NUMBER, "maxConnections").setDefaultValue("65000"));

		return new Form().addSection(section);
	}

	@Override
	public CacheFactory<String, Object> cacheFactory(Map<String, String> fields) {

		String host = fields.get("host");
		Integer port = Integer.parseInt(fields.get("port"));
		String database = fields.get("database");
		String username = fields.get("username");
		String password = fields.get("password");
		Long maxConnections = Long.parseLong(fields.get("maxConnections"));

		RedisConfig config = new RedisConfig().setHost(host).setPort(port).setDatabase(database).setUsername(username)
				.setPassword(password).setMaxConnections(maxConnections);

		return new RedisCacheFactory("default", config);
	}

}
