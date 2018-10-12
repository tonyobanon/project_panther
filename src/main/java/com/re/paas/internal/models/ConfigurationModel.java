package com.re.paas.internal.models;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;

@BlockerTodo("Implement ASAP")
public class ConfigurationModel implements BaseModel {

	@Override
	public String path() {
		return "core/configuration";
	}
	
	@ModelMethod(functionality = RoleFunctionalities.VIEW_SYSTEM_CONFIGURATION)
	public static final void getAll() {
		
		//get all config entries that is marked as front-end
		
	}

	@Override
	public void install(InstallOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
