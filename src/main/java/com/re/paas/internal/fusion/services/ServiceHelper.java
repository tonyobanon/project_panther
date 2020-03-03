package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.HttpServerRequest;

public class ServiceHelper {

	public static Long getUserId(HttpServerRequest req) {
		String userId = req.getParam(ServiceDelegate.USER_ID_PARAM_NAME);
		return userId != null ? Long.parseLong(userId) : null;
	}

	public static void setUserId(HttpServerRequest req, Long userId) {
		req.params().add(ServiceDelegate.USER_ID_PARAM_NAME, userId.toString());
	
	}

}
