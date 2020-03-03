package com.re.paas.integrated.fusion.services;

import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.integrated.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.billing.BasePaymentModel;

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
	public static void notificationHook(RoutingContext ctx) {

	}
}
