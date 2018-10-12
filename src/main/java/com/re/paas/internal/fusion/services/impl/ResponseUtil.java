package com.re.paas.internal.fusion.services.impl;

import javax.servlet.http.HttpServletResponse;

import com.re.paas.api.fusion.server.JsonObject;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ResponseUtil {
	
	static String toResponse(int status) {
		
		HttpResponseStatus rStatus = HttpResponseStatus.valueOf(status);
		
		return new JsonObject()
				.put("code", rStatus.code())
				.put("class", rStatus.codeClass().name())
				.put("status", rStatus.reasonPhrase()).encode();
	}
	
	static String toResponse(Throwable T) {
		
		HttpResponseStatus rStatus = HttpResponseStatus.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		return new JsonObject()
				.put("code", rStatus.code())
				.put("class", rStatus.codeClass().name())
				.put("message", T.getMessage()).encode();
	}
	
	static String toResponse(int status, String message) {
		
		HttpResponseStatus rStatus = HttpResponseStatus.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		return new JsonObject()
				.put("code", rStatus.code())
				.put("class", rStatus.codeClass().name())
				.put("status", rStatus.reasonPhrase())
				.put("message", message).encode();
	}
}
