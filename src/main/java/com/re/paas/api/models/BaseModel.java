package com.re.paas.api.models;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

@BlockerTodo("Create classes containing entity attribute names. See project <eaa>")
public abstract class BaseModel extends AbstractResource {
	
	public BaseModel() {
		super(SpiType.NODE_ROLE);
	}
	
	public static AbstractModelDelegate getDelegate() {
		return Singleton.get(AbstractModelDelegate.class);
	}

	/**
	 * This method is used by models to populate data into their tables after
	 * Install Options are available, as well as add default metric data. It also
	 * should contain logic required to start the model
	 */
	public void install(InstallOptions options) {

	}

	public abstract String path();

	public void start() {

	}

	public void update() {

	}

	/**
	 * This method is used by models to populate data into their tables before
	 * Install Options are available, as well as add default metric data.
	 */
	public void preInstall() {
	}

	public void unInstall() {
	}

}
