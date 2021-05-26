package com.re.paas.internal.fusion;

import java.util.Date;
import java.util.Map;

import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.Session;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;

public class SessionImpl implements Session<String, Object> {

	private final String id;
	private final String cacheKey;
	private final RoutingContext ctx;

	//private static final PersistenceType SESSION_PERSISTENCE = PersistenceType.SHORT_LIVED;
	
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
			
			// Todo: set createdAt and updatedAt

			// add cookie
			ctx.response().addCookie(new CookieImpl(SESSION_COOKIE, sessionId).setMaxAge(-1));

		} else {

			sessionId = c.getValue();

			cacheKey = getCacheKey(sessionId);

			// Todo: Set expiry on <cacheKey>
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
		return null;
	}

	@Override
	public void put(String k, Object v) {
	}

	@Override
	public void delete(String k) {
	}

	@Override
	public Map<String, Object> data() {
		return null;
	}

	@Override
	public Date dateCreated() {
		return null;
	}

	@Override
	public Date dateUpdated() {
		return null;
	}
}
