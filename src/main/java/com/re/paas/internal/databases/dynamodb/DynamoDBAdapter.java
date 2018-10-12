package com.re.paas.internal.databases.dynamodb;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.api.forms.InputType;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;

public class DynamoDBAdapter implements DatabaseAdapter {

	@Override
	public String name() {
		return "dynamo_db";
	}
	
	@Override
	public String title() {
		return "dynamo_db";
	}

	@Override
	public String icon() {
		return "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/DynamoDB.png/220px-DynamoDB.png";
	}

	@Override
	public List<Section> initForm() {

		Section main = new Section();
		main.setTitle(ClientRBRef.get("aws_credentials"))
		
				.withField(new SimpleField("dynamo_db_access_key", InputType.TEXT, "dynamo_db_access_key"))
				.withField(new SimpleField("dynamo_db_secret_key", InputType.SECRET, "dynamo_db_secret_key"))
				.withField(new SimpleField("dynamo_db_region", InputType.TEXT, "dynamo_db_region"));
			
		return ImmutableList.of(main);
	}

	@Override
	public String schemaFactory() {
		return DynamoDBSchemaFactory.class.getName();
	}

}
