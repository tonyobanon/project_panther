package com.re.paas.integrated.fusion.services;

import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.models.ActivityStreamModel;
import com.re.paas.internal.classes.ActivitityStreamTimeline;

public class ActivityStreamService extends BaseService {
	
	@Override
	public String uri() {
		return "/activity-stream";
	}
	
	@FusionEndpoint(uri = "/set-timeline", requestParams = { "timeline" }, method = HttpMethod.POST,
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public static void setActivityStreamTimeline(RoutingContext ctx) {
		ActivitityStreamTimeline timeline = ActivitityStreamTimeline.from(Integer.parseInt(ctx.request().getParam("timeline")));
		ActivityStreamModel.setActivityTimeline(timeline);
	}
	        
	@FusionEndpoint(uri = "/get-timeline",
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public static void getActivityStreamTimeline(RoutingContext ctx) {
		ActivitityStreamTimeline timeline = ActivityStreamModel.getActivityTimeline();
		ctx.response().write(new JsonObject().put("timeline", timeline.getValue()).encode()); 
	}  
	         
	@FusionEndpoint(uri = "/is-enabled", 
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public static void isActivityStreamEnabled(RoutingContext ctx) {
		Boolean isEnabled = ActivityStreamModel.isEnabled();
		ctx.response().write(new JsonObject().put("isEnabled", isEnabled).encode());
	}    
	 
	@FusionEndpoint(uri = "/disable", method = HttpMethod.POST, 
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public static void disableActivityStream(RoutingContext ctx) {
		ActivityStreamModel.disable(); 
	}
	
	@FusionEndpoint(uri = "/enable", method = HttpMethod.POST,
			functionality = PlatformFunctionalities.Constants.MANAGE_ACTIVITY_STREAM)
	public static void enableActivityStream(RoutingContext ctx) {
		ActivityStreamModel.enable();
	}
	
}
