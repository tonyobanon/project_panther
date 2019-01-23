package com.re.paas.internal.models;

import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.utils.BackendObjectMarshaller;

public class PlatformModel implements BaseModel {

	private static Boolean isInstalled;

	@Override
	public String path() {
		return "core/platform";
	}

	@ModelMethod(functionality = PlatformFunctionalities.Constants.PLATFORM_INSTALLATION)
	public static void doInstall(InstallOptions spec) {

		// Install all models
		Logger.get().debug("Installing Models");

		BaseModel.getDelegate().getModels().forEach(e -> {
			Logger.get().debug("Installing " + e.getClass().getSimpleName());
			e.install(spec);
		});

		isInstalled = true;
		ConfigModel.put(ConfigKeys.IS_INSTALLED, BackendObjectMarshaller.marshal(true));
	}

	public static boolean isInstalled() {
		if (isInstalled == null) {
			isInstalled = BackendObjectMarshaller.unmarshalBool(ConfigModel.get(ConfigKeys.IS_INSTALLED));
		}
		return isInstalled;
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
