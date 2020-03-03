package com.re.paas.integrated.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.realms.Realm;
import com.re.paas.integrated.tables.defs.users.BaseUserTable;
import com.re.paas.internal.classes.spec.BaseUserSpec;

public class UserModel extends BaseModel {

	@Override
	public String path() {
		return "core/users";
	}

	private static List<BaseUserSpec> getBaseUserSpec(Map<String, String> keys) {

		List<BaseUserSpec> result = new ArrayList<>();
		
		ofy().load().type(BaseUserTable.class).ids(keys.keySet()).forEach((k, v) -> {

			BaseUserSpec spec = new BaseUserSpec().setId(v.getId()).setRole(v.getRole()).setDescription(keys.forEach(k))
					.setName(v.getFirstName() + ClientResources.HtmlCharacterEntities.SPACE + v.getLastName())
					.setDateCreated(v.getDateCreated()).setDateUpdated(v.getDateUpdated()).setImage(v.getImage());

			result.add(spec);

		});
		return result;
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
