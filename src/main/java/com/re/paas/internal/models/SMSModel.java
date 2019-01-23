package com.re.paas.internal.models;

import java.util.Arrays;
import java.util.Map;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.Application;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.realms.AdminRealm;

import infobip.api.client.SendSingleTextualSms;
import infobip.api.config.BasicAuthConfiguration;
import infobip.api.model.sms.mt.send.textual.SMSTextualRequest;

public class SMSModel implements BaseModel {

	private static BasicAuthConfiguration authConfiguration = null;

	@Override
	public String path() {
		return "core/ext/sms_service";
	}

	@Override
	public void preInstall() {

		// Create configuration fields

		String sectionId = FormModel.newSection(ClientRBRef.get("infobip_settings"), null,
				FormSectionType.SYSTEM_CONFIGURATION, new AdminRealm());

		ConfigModel.put(ConfigKeys.INFOBIP_SETTINGS_FORM_SECTION_ID, sectionId);

		String baseUrlField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("base_url")));

		String usernameField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("username")));

		String passwordField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("password")));

		ConfigModel.putAll(new FluentHashMap<String, Object>().with(ConfigKeys.INFOBIP_BASEURL_FIELD_ID, baseUrlField)
				.with(ConfigKeys.INFOBIP_USERNAME_FIELD_ID, usernameField)
				.with(ConfigKeys.INFOBIP_PASSWORD_FIELD_ID, passwordField));
	}

	@Override
	public void start() {

	}

	private static BasicAuthConfiguration _getAuthConfiguration() {
		
		Map<String, Object> keys = ConfigModel.getAll(ConfigKeys.INFOBIP_BASEURL_FIELD_ID,
				ConfigKeys.INFOBIP_USERNAME_FIELD_ID, ConfigKeys.INFOBIP_PASSWORD_FIELD_ID);

		Map<String, Object> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String baseUrl = (String) values.get(keys.get(ConfigKeys.INFOBIP_BASEURL_FIELD_ID));
		String username = (String) values.get(keys.get(ConfigKeys.INFOBIP_USERNAME_FIELD_ID));
		String password = (String) values.get(keys.get(ConfigKeys.INFOBIP_PASSWORD_FIELD_ID));

		BasicAuthConfiguration authConfiguration = new BasicAuthConfiguration(baseUrl, username, password);

		return SMSModel.authConfiguration = authConfiguration;
	}

	private static BasicAuthConfiguration getAuthConfiguration() {
		return authConfiguration != null ? authConfiguration : _getAuthConfiguration();
	}

	@BlockerTodo("Consolidate response, query for the delivery status of the message(s)")
	protected static void sendMessage(String message, String... recipients) {

		SendSingleTextualSms client = new SendSingleTextualSms(getAuthConfiguration());

		SMSTextualRequest requestBody = new SMSTextualRequest();

		requestBody.setFrom(Application.APPLICATION_NAME);
		requestBody.setTo(Arrays.asList(recipients));
		requestBody.setText(message);

		client.execute(requestBody);
	}

	@Override
	public void install(InstallOptions options) {
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
