package com.re.paas.integrated.models;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;

@BlockerTodo("Implement ASAP")
public class ConfigurationModel extends BaseModel {

	@Override
	public String path() {
		return "core/configuration";
	}
	
	@ModelMethod(functionality = PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION)
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
