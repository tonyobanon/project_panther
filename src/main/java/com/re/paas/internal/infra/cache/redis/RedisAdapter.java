package com.re.paas.internal.infra.cache.redis;

import static com.re.paas.api.forms.input.InputType.NUMBER;
import static com.re.paas.api.forms.input.InputType.TEXT;

import java.util.Map;

import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class RedisAdapter implements CacheAdapter {

	private static final Integer defaultDb = 0;

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
				
				.withField(new SimpleField("database", NUMBER, "database")
						.setDefaultValue(defaultDb.toString()))

				.withField(new SimpleField("username", TEXT, "username"))
				.withField(new SimpleField("password", TEXT, "password"));

		return new Form().addSection(section);
	}

	@Override
	public CacheFactory<String, Object> getResource(Map<String, String> fields) {

		String host = fields.get("host");
		Integer port = Integer.parseInt(fields.get("port"));
		Integer database = fields.containsKey("database") ? Integer.parseInt(fields.get("database")) : defaultDb;
		String username = fields.get("username");
		String password = fields.get("password");

		RedisConfig config = new RedisConfig().setHost(host).setPort(port).setDatabase(database).setUsername(username)
				.setPassword(password);

		return new RedisCacheFactory(this, config);
	}
}
