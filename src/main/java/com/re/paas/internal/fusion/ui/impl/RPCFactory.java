package com.re.paas.internal.fusion.ui.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.Functionality;

public class RPCFactory {

	private static boolean prependDomainVariableToUrl = true;

	public static void setPrependDomainVariableToUrl(boolean prependDomainVariableToUrl) {
		RPCFactory.prependDomainVariableToUrl = prependDomainVariableToUrl;
	}

	/**
	 * Generate javascript client stubs
	 */
	@BlockerTodo("Verify that none of the parameters are javascript reserved keywords")
	public static String generateXHRClient(BaseService service, Method method, FusionEndpoint endpoint, Route route) {

		Functionality functionality = BaseService.getDelegate().getRouteFunctionality(route);

		StringBuilder clientFunction = new StringBuilder();

		clientFunction.append("\n");
		clientFunction.append("\n");

		clientFunction.append(" function " + method.getName() + " (");

		List<String> headerParamList = Lists.newArrayList(endpoint.headerParams());

		String[] headerParams = functionality.requiresBasicAuth() ? headerParamList.toArray(new String[headerParamList.size()])
				: endpoint.headerParams();

		List<String> functionParamList = new ArrayList<>();

		for (int i = 0; i < headerParams.length; i++) {
			functionParamList.add(headerParams[i]);
		}

		for (int i = 0; i < endpoint.requestParams().length; i++) {
			functionParamList.add(endpoint.requestParams()[i]);
		}

		if (!endpoint.enableMultipart()) {
			for (int i = 0; i < endpoint.bodyParams().length; i++) {
				functionParamList.add(endpoint.bodyParams()[i]);
			}
		} else {
			functionParamList.add("formData");
		}

		String[] functionParams = functionParamList.toArray(new String[functionParamList.size()]);

		for (int i = 0; i < functionParams.length; i++) {
			clientFunction.append(functionParams[i]);
			if (i < functionParams.length - 1) {
				clientFunction.append(", ");
			}
		}

		clientFunction.append(") {");
		clientFunction.append("\n");

		clientFunction.append("\t return new Promise(function(resolve, reject) {");
		clientFunction.append("\n");
		clientFunction.append("\t\t $.ajax({");
		clientFunction.append("\n");
		clientFunction.append("\t\t\t method : \"" + endpoint.method().toString() + "\",");
		clientFunction.append("\n");
		clientFunction.append("\t\t\t async: " + endpoint.isAsync() + ",");
		clientFunction.append("\n");
		clientFunction.append("\t\t\t processData: false,");
		clientFunction.append("\n");
		clientFunction.append(
				"\t\t\t statusCode: {302: function(jqXHR, status, error) { window.location = jqXHR.getResponseHeader(\"X-Location\");}},");

		clientFunction.append("\n");

		if (endpoint.bodyParams().length > 0 && !endpoint.enableMultipart()) {

			clientFunction.append("\t\t\t contentType : 'application/json', ");

			clientFunction.append("\n");

			clientFunction.append("\t\t\t data :");

			clientFunction.append(" JSON.stringify(");

			clientFunction.append("{");

			for (int i = 0; i < endpoint.bodyParams().length; i++) {
				// clientFunction.append("\n");
				// clientFunction.append("\t\t\t\t ")
				clientFunction.append(endpoint.bodyParams()[i]).append(": ").append(endpoint.bodyParams()[i]);
				if (i < endpoint.bodyParams().length - 1) {
					clientFunction.append(", ");
				}
			}

			// clientFunction.append("\n");
			// clientFunction.append("\t\t\t");

			clientFunction.append("}");

			clientFunction.append("), ");

			clientFunction.append("\n");

		} else if (endpoint.enableMultipart()) {

			clientFunction.append("\t\t\t cache : false, ");

			clientFunction.append("\n");

			clientFunction.append("\t\t\t contentType : 'multipart/form-data', ");

			clientFunction.append("\n");

			clientFunction.append("\t\t\t data : formData, ");

			clientFunction.append("\n");

		}

		if (headerParams.length > 0) {
			clientFunction.append("\t\t\t headers : {");

			for (int i = 0; i < headerParams.length; i++) {
				clientFunction.append("\n");
				clientFunction.append("\t\t\t\t ").append(headerParams[i]).append(": ").append(headerParams[i]);
				if (i < headerParams.length - 1) {
					clientFunction.append(", ");
				}
			}
			clientFunction.append("\n");
			clientFunction.append("\t\t\t }, ");
			clientFunction.append("\n");
		}

		StringBuilder requestParams = new StringBuilder();

		if (endpoint.requestParams().length > 0) {
			for (int i = 0; i < endpoint.requestParams().length; i++) {
				requestParams.append(i == 0 ? "?" : "&").append(endpoint.requestParams()[i]).append("=").append("\"")
						.append(" + ").append(endpoint.requestParams()[i]);

				if (i < endpoint.requestParams().length - 1) {
					requestParams.append(" +").append(" \"");
				}
			}
		} else {
			requestParams.append("\"");
		}

		clientFunction.append("\t\t\t url: " + (prependDomainVariableToUrl ? "FUSION_API_URL + " : "") + "\""
				+ AbstractServiceDelegate.BASE_PATH + service.uri() + endpoint.uri() + requestParams.toString());

		clientFunction.append("\n");

		clientFunction.append("\t\t\t }).done(function(o) {");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t\t resolve(o);");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t })");

		clientFunction.append(".fail(function(jqXHR, status, error){");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t\t if(jqXHR.getResponseHeader(\"X-Location\") === null && jqXHR.status !== 302){");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t\t\t reject(jqXHR);");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t\t }");

		clientFunction.append("\n");

		clientFunction.append("\t\t\t });");

		clientFunction.append("\n");

		clientFunction.append("\t\t });");

		clientFunction.append("\n");

		clientFunction.append(" }");

		clientFunction.append("\n");

		return clientFunction.toString();

	}
}
