package com.re.paas.integrated.fusion.services;

import javax.servlet.http.HttpServletResponse;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.fusion.ui.deprecated.AbstractUIComponentDelegate;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.models.PlatformModel;
import com.re.paas.internal.classes.Json;

public class PlatformService extends BaseService {
	
	@Override
	public String uri() {
		return "/platform/tools";
	}

	@FusionEndpoint(uri = "/setup", bodyParams = {
			"payload" }, method = HttpMethod.POST, 
					functionality = PlatformFunctionalities.Constants.PLATFORM_INSTALLATION)
	public static void doSetup(RoutingContext context) {

		try {

			if (PlatformModel.isInstalled()) {
				return;
			}

			JsonObject body = context.getBodyAsJson();

			InstallOptions spec = Json.getGson().fromJson(body.getJsonObject("payload").encode(),
					InstallOptions.class);

			// Perform installation
			PlatformModel.doInstall(spec);

			// Go to console
			context.response().putHeader("X-Location", AbstractUIComponentDelegate.DEFAULT_CONSOLE_URI)
					.setStatusCode(HttpServletResponse.SC_FOUND);

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}
	}

}
