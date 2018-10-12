package com.re.paas.internal.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;

/**
 * Helper class to get a database connection
 * @author Tony
 *
 */
public class Database {

	private static Connection connection;

	public static Connection get() {

		if (connection != null) {
			return connection;
		}

		// Get config
		DatabaseConfig dbConfig = DatabaseConfig.get();

		// Get adapter
		DatabaseAdapter adapter = CloudEnvironmentAdapter.getDatabaseAdapter(dbConfig.getAdapterName());

		try {

			Class.forName("org.apache.calcite.jdbc.Driver");

			Properties info = new Properties();
			info.setProperty("schemaFactory", adapter.schemaFactory());
			info.setProperty("lex", "JAVA");

			dbConfig.getFields().forEach((k, v) -> {
				info.setProperty("schema." + k, v);
			});

			Connection connection = DriverManager.getConnection("jdbc:calcite:", info);

			Database.connection = connection;
			return connection;

		} catch (SQLException | ClassNotFoundException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

}
