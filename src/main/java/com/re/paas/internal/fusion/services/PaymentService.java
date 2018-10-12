package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.models.BasePaymentModel;

public class PaymentService extends BaseService {
	
	@Override
	public String uri() {
		return "/payment-service";
	}

	/**
	 * Here, we need to create a custom authenticator
	 * @param ctx
	 */
	@FusionEndpoint(uri = BasePaymentModel.IPN_CALLBACK_URL, functionality = LocationFunctionalities.Constants.GET_AVAILABLE_COUNTRIES, createXhrClient = false)
	public void notificationHook(RoutingContext ctx) {

	}
}
