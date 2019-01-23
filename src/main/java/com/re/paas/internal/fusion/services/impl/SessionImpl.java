package com.re.paas.internal.fusion.services.impl;

import java.util.Date;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.server.Session;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.PersistenceType;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;

public class SessionImpl implements Session<String, Object> {

	private final String id;
	private final String cacheKey;
	private final RoutingContext ctx;

	private static final PersistenceType SESSION_PERSISTENCE = PersistenceType.SHORT_LIVED;
	private static final String SESSION_CACHE_PREFIX = "web_session_";
	private static final String SESSION_COOKIE = "SID";

	private static final String DATE_CREATED_HASH_FIELD = "__DC";
	private static final String DATE_UPDATED_HASH_FIELD = "__DU";

	/**
	 * This function is called on each HTTP request, to instantiate a session object
	 * for the routing context
	 */
	public SessionImpl(RoutingContext ctx) {

		Cookie c = ctx.request().cookies().get(SESSION_COOKIE);

		String sessionId = null;
		String cacheKey = null;

		if (c == null) {

			// Generate new session id
			sessionId = Utils.newRandom();

			cacheKey = getCacheKey(sessionId);

			// Create hash in cache, for session data
			Object now = Dates.currentDate();
			Cache.get().mset(SESSION_PERSISTENCE, cacheKey,
					FluentHashMap
					.of(DATE_CREATED_HASH_FIELD, now)
					.with(DATE_UPDATED_HASH_FIELD, now));

			// add cookie
			ctx.response().addCookie(new Cookie(SESSION_COOKIE, sessionId).setMaxAge(-1));

		} else {

			sessionId = c.getValue();

			cacheKey = getCacheKey(sessionId);

			// update expiry
			Cache.get().expire(cacheKey, SESSION_PERSISTENCE);
		}

		this.id = sessionId;
		this.cacheKey = cacheKey;
		this.ctx = ctx;
	}
	
	private static final String getCacheKey(String sessionId) {
		return SESSION_CACHE_PREFIX + sessionId;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public RoutingContext context() {
		return ctx;
	}

	@Override
	public Object get(String k) {
		return Cache.get().hget(cacheKey, k).join();
	}

	@Override
	public void put(String k, Object v) {
		Cache<String, Object> cache = Cache.get();
		cache.hset(cacheKey, DATE_UPDATED_HASH_FIELD, Dates.now().toString());
		cache.hset(cacheKey, k, v);
	}

	@Override
	public void delete(String k) {
		Cache<String, Object> cache = Cache.get();
		cache.hset(cacheKey, DATE_UPDATED_HASH_FIELD, Dates.now().toString());
		cache.hdel(cacheKey, k);
	}

	@Override
	public Map<String, Object> data() {
		return Cache.get().hgetall(cacheKey).join();
	}

	@Override
	public Date dateCreated() {
		String dateString = (String) Cache.get().hget(cacheKey, DATE_CREATED_HASH_FIELD).join();
		return Dates.toDate(dateString);
	}

	@Override
	public Date dateUpdated() {
		String dateString = (String) Cache.get().hget(cacheKey, DATE_UPDATED_HASH_FIELD).join();
		return Dates.toDate(dateString);
	}
}
