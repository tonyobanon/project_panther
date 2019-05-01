package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.models.PrototypingModel;

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
