package com.re.paas.internal.fusion.services;

import javax.servlet.http.HttpServletResponse;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.ui.AbstractUIComponentDelegate;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.models.PlatformModel;

public class PlatformService extends BaseService {
	
	@Override
	public String uri() {
		return "/platform/tools";
	}

	@FusionEndpoint(uri = "/setup", bodyParams = {
			"payload" }, method = HttpMethod.POST, isBlocking = true, 
					functionality = PlatformFunctionalities.Constants.PLATFORM_INSTALLATION)
	public void doSetup(RoutingContext context) {

		try {

			if (PlatformModel.isInstalled()) {
				return;
			}

			JsonObject body = context.getBodyAsJson();

			InstallOptions spec = GsonFactory.getInstance().fromJson(body.getJsonObject("payload").encode(),
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
