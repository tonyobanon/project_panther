package com.re.paas.internal.models.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.classes.AppDirectory;

@BlockerTodo("Think of what to do with this, since we are going hybrid cloud")
public class GoogleHelper {

	private static final Credentials GOOGLE_CREDENTIALS;

	public static Credentials getCredentials() {
		return GOOGLE_CREDENTIALS;
	}

	static {

		// Setup Google Credentials
		
		Logger.get().info("Setting up Google Service Account Credentials ..");

		Credentials googleCredentials = null;
		try {
			Path path = AppDirectory.getPath("application_data/rems-11ab32e9d8b0.json");
			googleCredentials = ServiceAccountCredentials
					.fromStream(Files.newInputStream(path));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		GOOGLE_CREDENTIALS = googleCredentials;		
	}
}
