package com.re.paas.internal.fusion;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.Session;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.utils.Utils;

class SessionImpl implements Session<String, Object> {

	private final String id;

	private final RoutingContext ctx;
	private final Cache<String, Object> cache;

	private static final String CACHE_BUCKET = "web_sessions";
	private static final String SESSION_COOKIE = "SID";

	/**
	 * This function is called on each HTTP request, to instantiate a session object
	 * for the routing context
	 */
	public SessionImpl(RoutingContext ctx) {

		String sessionId = CookieHelper.getCookie(ctx.request().getCookies(), SESSION_COOKIE);

		if (sessionId == null) {

			// Generate new session id
			sessionId = Utils.newRandom();

			// add cookie
			ctx.response().addCookie(new CookieImpl(SESSION_COOKIE, sessionId).setMaxAge(-1));
		}

		this.cache = CacheAdapter.getDelegate().getCacheFactory().get(CACHE_BUCKET);

		this.id = sessionId;
		this.ctx = ctx;
		
		this.updateExpiry(3600);
	}
	
	private void updateExpiry(long secs) {
		this.cache.expireInSecs(this.id, secs);
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
	public CompletableFuture<Object> get(String k) {
		return this.cache.hget(id, k);
	}

	@Override
	public CompletableFuture<Object> put(String k, Object v) {
		return this.cache.hset(id, k, v);
	}

	@Override
	public CompletableFuture<Integer> delete(String k) {
		return this.cache.hdel(id, k);
	}

	@Override
	public CompletableFuture<Map<String, Object>> data() {
		return this.cache.hgetall(id);
	}
}
