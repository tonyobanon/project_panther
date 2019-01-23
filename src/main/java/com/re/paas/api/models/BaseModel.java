package com.re.paas.api.models;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.models.classes.InstallOptions;

@BlockerTodo("Create classes containing entity attribute names. See project <eaa>")
public interface BaseModel {
	
	public static AbstractModelDelegate getDelegate() {
		return Singleton.get(AbstractModelDelegate.class);
	}

	/**
	 * This method is used by models to populate data into their tables after
	 * Install Options are available, as well as add default metric data. It also
	 * should contain logic required to start the model
	 */
	public default void install(InstallOptions options) {

	}

	public abstract String path();

	public default void start() {

	}

	public default void update() {

	}

	/**
	 * This method is used by models to populate data into their tables before
	 * Install Options are available, as well as add default metric data.
	 */
	public default void preInstall() {

	}

	public default void unInstall() {

	}

}
