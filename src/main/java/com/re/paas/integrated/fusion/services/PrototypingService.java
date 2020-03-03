package com.re.paas.integrated.fusion.services;

import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.models.PrototypingModel;

public class PrototypingService extends BaseService {
	
@Override
public String uri() {
	return "/prototyping";
}
	@FusionEndpoint(uri = "/create-mocks", functionality = PlatformFunctionalities.Constants.ADD_SYSTEM_MOCK_DATA)
	public static void createMocks(RoutingContext ctx) {
		PrototypingModel.addMocks();
	}
	
}
