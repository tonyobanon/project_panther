package com.re.paas.internal.infra.database.dynamodb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.DocumentInterface;
import com.re.paas.internal.infra.database.dynamodb.nosql.DynamoDBNoSQLDatabase;
import com.re.paas.internal.infra.database.dynamodb.sql.DynamoDBSchemaFactory;

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
	public Connection getSQLDatabase(Map<String, String> fields) {
		
		try {

			// Eagerly load calcite jdbc adapter
			Class.forName("org.apache.calcite.jdbc.Driver");

			Properties info = new Properties();
			info.setProperty("schemaFactory", DynamoDBSchemaFactory.class.getName());
			info.setProperty("lex", "JAVA");

			fields.forEach((k, v) -> {
				info.setProperty("schema." + k, v);
			});

			Connection connection = DriverManager.getConnection("jdbc:calcite:", info);

			return connection;

		} catch (SQLException | ClassNotFoundException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	@Override
	public DocumentInterface getNoSQLDatabase(Map<String, String> fields) {

		String accessKey = fields.get("access_key");
		String secretKey = fields.get("secret_key");
		String region = fields.get("region");

		return new DynamoDBNoSQLDatabase(accessKey, secretKey, region);
	}

}
