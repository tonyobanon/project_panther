package com.re.paas.api.tasks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractTaskDelegate extends SpiDelegate<TaskModel> {

	@SecureMethod
	public abstract void registerTask(Task task);
	
	public abstract Integer intervalInSecs();
	
	public abstract void registerTask(String modelName, Map<String, Object> parameters);
	
	public abstract void removeTask(String id);
	
	public abstract Map<String, String> getTaskModelNames();
	
	public abstract TaskModel getTaskModel(String name);
	
	public abstract <P, R> CompletableFuture<R> execute(ParameterizedExecutable<P, R> executable);
}
