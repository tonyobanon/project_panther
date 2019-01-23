package com.re.paas.internal.fusion.services;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.classes.CronInterval;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.fusion.functionalities.TaskFunctionalities;
import com.re.paas.internal.fusion.services.impl.FusionHelper;
import com.re.paas.internal.models.TaskModel;
import com.re.paas.internal.tasks.ModelTask;

public class TaskService extends BaseService {

	@Override
	public String uri() {
		return "/task-service";
	}

	@FusionEndpoint(uri = "/get-image-names", requestParams = {}, method = HttpMethod.GET, 
			functionality = TaskFunctionalities.Constants.GET_IMAGE)
	public void getImageNames(RoutingContext ctx) {
		Map<String, ClientRBRef> result = TaskModel.getImageNames();
		FusionHelper.response(ctx, result);
	}

	@FusionEndpoint(uri = "/get-image-fields", requestParams = {
			"name" }, method = HttpMethod.GET, 
					functionality = TaskFunctionalities.Constants.GET_IMAGE)
	public void getImageFields(RoutingContext ctx) {

		String name = ctx.request().getParam("name");

		List<AbstractField> result = TaskModel.getImageFields(name);
		FusionHelper.response(ctx, result);
	}

	@FusionEndpoint(uri = "/new-task", bodyParams = { "name", "interval", "task",
			"maxExecutionCount" }, method = HttpMethod.PUT, 
			functionality = TaskFunctionalities.Constants.CREATE_TASK)
	public void newTask(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String name = body.getString("name");
		CronInterval interval = CronInterval.from(body.getInteger("interval"));

		ModelTask task = Json.getGson().fromJson(body.getJsonObject("task").encode(), ModelTask.class);

		Integer maxExecutionCount = body.getInteger("maxExecutionCount");

		Long id = TaskModel.newTask(name, interval, task, maxExecutionCount, false);
		FusionHelper.response(ctx, id);
	}

	@FusionEndpoint(uri = "/delete-task", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = TaskFunctionalities.Constants.DELETE_TASK)
	public void deleteTask(RoutingContext ctx) {

		Long id = Long.parseLong(ctx.request().getParam("id"));

		TaskModel.deleteTask(id);
	}

}
