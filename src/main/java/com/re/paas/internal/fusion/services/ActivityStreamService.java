package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.classes.ActivitityStreamTimeline;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.models.ActivityStreamModel;

public class ActivityStreamService extends BaseService {
	
	@Override
	public String uri() {
		return "/activity-stream";
	}
	
	@FusionEndpoint(uri = "/set-timeline", requestParams = { "timeline" }, method = HttpMethod.POST,
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public void setActivityStreamTimeline(RoutingContext ctx) {
		ActivitityStreamTimeline timeline = ActivitityStreamTimeline.from(Integer.parseInt(ctx.request().getParam("timeline")));
		ActivityStreamModel.setActivityTimeline(timeline);
	}
	        
	@FusionEndpoint(uri = "/get-timeline",
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public void getActivityStreamTimeline(RoutingContext ctx) {
		ActivitityStreamTimeline timeline = ActivityStreamModel.getActivityTimeline();
		ctx.response().write(new JsonObject().put("timeline", timeline.getValue()).encode()); 
	}  
	         
	@FusionEndpoint(uri = "/is-enabled", 
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public void isActivityStreamEnabled(RoutingContext ctx) {
		Boolean isEnabled = ActivityStreamModel.isEnabled();
		ctx.response().write(new JsonObject().put("isEnabled", isEnabled).encode());
	}    
	 
	@FusionEndpoint(uri = "/disable", method = HttpMethod.POST, 
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public void disableActivityStream(RoutingContext ctx) {
		ActivityStreamModel.disable(); 
	}
	
	@FusionEndpoint(uri = "/enable", method = HttpMethod.POST,
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public void enableActivityStream(RoutingContext ctx) {
		ActivityStreamModel.enable();
	}
	
}
