
package com.re.paas.integrated.models;

import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.emailing.EmailingModel;
import com.re.paas.internal.i18n.LocationModel;

@Model(dependencies = { ActivityStreamModel.class, ApplicationModel.class, BaseUserModel.class,
		ConfigModel.class, ConfigurationModel.class, EmailingModel.class,
		FormModel.class, LocaleModel.class, LocationModel.class, PlatformModel.class,
		RoleModel.class, SearchModel.class, SystemMetricsModel.class })
public class PrototypingModel extends BaseModel {

	@Override
	public String path() {
		return "tmp/prototyping";
	}

	@Override
	public void preInstall() {
	}

	@Override
	public void install(InstallOptions options) {

	}

	public static void addMocks() {
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
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
