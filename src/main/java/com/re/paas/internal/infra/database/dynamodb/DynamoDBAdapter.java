package com.re.paas.internal.infra.database.dynamodb;

import java.util.Map;

import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.document.Database;

public class DynamoDBAdapter implements DatabaseAdapter {

	@Override
	public String name() {
		return "dynamodb";
	}

	@Override
	public String title() {
		return "Dynamo DB";
	}

	@Override
	public String iconUrl() {
		return "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/DynamoDB.png/220px-DynamoDB.png";
	}

	@Override
	public Form initForm() {

		Section section = new Section();
		section.setTitle("aws_credentials")

				.withField(new SimpleField("access_key", InputType.TEXT, "access_key"))
				.withField(new SimpleField("secret_key", InputType.SECRET, "secret_key"))
				.withField(new SimpleField("region", InputType.TEXT, "region"));

		return new Form().addSection(section);
	}

	@Override
	public Database getResource(Map<String, String> fields) {
		
//		String accessKey = fields.get("access_key");
//		String secretKey = fields.get("secret_key");
//		String region = fields.get("region");
		
		return null;
	}
}
