package com.re.paas.internal.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.codec.binary.Base64;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.PaymentResult;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;
import com.adyen.service.Payment;
import com.adyen.service.exception.ApiException;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.fusion.server.HttpServerRequest;
import com.re.paas.api.fusion.server.JsonArray;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.billing.AuthorizationResult;
import com.re.paas.internal.billing.CaptureRequest;
import com.re.paas.internal.billing.CaptureResult;
import com.re.paas.internal.billing.IpnEventType;
import com.re.paas.internal.billing.PaymentRequest;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.models.errors.BillingError;
import com.re.paas.internal.models.helpers.BillingHelper;
import com.re.paas.internal.realms.AdminRealm;

public class BasePaymentModel extends BaseModel {

	public static final String IPN_CALLBACK_URL = "/ipn_callback";

	private static Client client = null;

	@Override
	public String path() {
		return "core/base_payments";
	}

	@Override
	public void preInstall() {

		// Create configuration fields

		String sectionId = FormModel.newSection(ClientRBRef.get("ayden_settings"), null,
				FormSectionType.SYSTEM_CONFIGURATION, new AdminRealm());

		ConfigModel.put(ConfigKeys.AYDEN_SETTINGS_FORM_SECTION_ID, sectionId);

		String usernameField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("username")));

		String passwordField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("password")));

		String applicationNameField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("application_name")));

		String liveEnvironmentField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.BOOLEAN, ClientRBRef.get("live_environment")));

		String authUsernameField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.TEXT, ClientRBRef.get("notification_server_auth_username")));

		String authPasswordField = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId,
				new SimpleField(InputType.SECRET, ClientRBRef.get("notification_server_auth_password")));

		ConfigModel.putAll(new FluentHashMap<String, Object>().with(ConfigKeys.AYDEN_USERNAME_FIELD_ID, usernameField)
				.with(ConfigKeys.AYDEN_PASSWORD_FIELD_ID, passwordField)
				.with(ConfigKeys.AYDEN_APPLICATION_NAME_FIELD_ID, applicationNameField)
				.with(ConfigKeys.AYDEN_LIVE_ENVIRONMENT_FIELD_ID, liveEnvironmentField)
				.with(ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_USERNAME_FIELD_ID, authUsernameField)
				.with(ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_PASSWORD_FIELD_ID, authPasswordField));

		ConfigModel.putAll(new FluentHashMap<String, Object>().with(usernameField, "").with(passwordField, "")
				.with(applicationNameField, "").with(liveEnvironmentField, false).with(authUsernameField, "")
				.with(authPasswordField, ""));
	}

	@Override
	public void start() {
		_getAydenClient();
	}

	private static Client _getAydenClient() {

		Map<String, Object> keys = ConfigModel.getAll(ConfigKeys.AYDEN_USERNAME_FIELD_ID,
				ConfigKeys.AYDEN_PASSWORD_FIELD_ID, ConfigKeys.AYDEN_APPLICATION_NAME_FIELD_ID,
				ConfigKeys.AYDEN_LIVE_ENVIRONMENT_FIELD_ID);

		Map<String, Object> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String username = (String) values.get(keys.get(ConfigKeys.AYDEN_USERNAME_FIELD_ID));
		String password = (String) values.get(keys.get(ConfigKeys.AYDEN_PASSWORD_FIELD_ID));
		String applicationName = (String) values.get(keys.get(ConfigKeys.AYDEN_APPLICATION_NAME_FIELD_ID));
		Boolean isLive = (Boolean) values.get(keys.get(ConfigKeys.AYDEN_LIVE_ENVIRONMENT_FIELD_ID));

		Client client = new Client(username, password, isLive ? Environment.LIVE : Environment.TEST, applicationName);

		BasePaymentModel.client = client;
		return client;
	}

	private static Client getAydenClient() {
		return client != null ? client : _getAydenClient();
	}

	public static boolean isModuleReady() {

		String sectionId = ConfigModel.get(ConfigKeys.AYDEN_SETTINGS_FORM_SECTION_ID);

		for (AbstractField o : FormModel.getFields(FormSectionType.SYSTEM_CONFIGURATION, sectionId)) {
			if (ConfigModel.get(o.getId().toString()) == null) {
				return false;
			}
		}
		return true;
	}

	public static void onNotificationReceived(HttpServerRequest req, Consumer<List<IpnEventType>> consumer)
			throws PlatformException {

		Map<String, Object> keys = ConfigModel.getAll(ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_USERNAME_FIELD_ID,
				ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_PASSWORD_FIELD_ID);

		Map<String, Object> values = ConfigModel.getAll(keys.values().toArray(new String[keys.values().size()]));

		String username = (String) values.get(keys.get(ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_USERNAME_FIELD_ID));
		String password = (String) values.get(keys.get(ConfigKeys.AYDEN_NOTIFICATION_SERVER_AUTH_PASSWORD_FIELD_ID));

		String authHeader = req.getHeader("Authorization");

		if (authHeader == null) {
			throw new PlatformException(BillingError.AUTH_HEADER_NOT_FOUND);
		} else {
			// Decode username and password from Authorization header
			String encodedAuth = authHeader.split(" ")[1];
			String decodedAuth = new String(Base64.decodeBase64(encodedAuth));

			String requestUser = decodedAuth.split(":")[0];
			String requestPassword = decodedAuth.split(":")[1];

			// Throw exception if username and/or password are incorrect
			if (!username.equals(requestUser) || !password.equals(requestPassword)) {
				throw new PlatformException(BillingError.AUTH_HEADER_NOT_FOUND);
			}
		}

		List<IpnEventType> result = new ArrayList<>();

		JsonObject body = new JsonObject(req.body());

		// Get all notification items in this request
		JsonArray notificationItems = body.getJsonArray("notificationItems");

		for (Object notificationItem : notificationItems) {

			// Extract and handle single notification
			JsonObject notification = (JsonObject) ((JsonObject) notificationItem)
					.getJsonObject("NotificationRequestItem");

			Long merchantReference = Long.parseLong(notification.getString("merchantReference"));
			IpnEventType event = null;

			switch (notification.getString("eventCode")) {

			case "AUTHORISATION":
				// Handle AUTHORISATION notification.
				// Confirms whether the payment was authorised successfully.
				// The authorisation is successful if the "success" field has the value true.
				// In case of an error or a refusal, it will be false and the "reason" field
				// should be consulted for the cause of the authorisation failure.

				boolean isSucessful = notification.getBoolean("success");

				if (isSucessful) {
					event = IpnEventType.AUTHORISATION_SUCCESS;
				} else {
					event = IpnEventType.AUTHORISATION_FAILED;
				}
				break;

			case "CANCELLATION":
				event = IpnEventType.CANCELLATION;
				break;

			case "REFUND":
				event = IpnEventType.REFUND;
				break;

			case "CANCEL_OR_REFUND":
				event = IpnEventType.CANCEL_OR_REFUND;
				break;

			case "CAPTURE":
				event = IpnEventType.CAPTURE;
				break;

			case "REFUNDED_REVERSED":
				event = IpnEventType.REFUNDED_REVERSED;
				break;

			case "CAPTURE_FAILED":
				event = IpnEventType.CAPTURE_FAILED;
				break;

			case "REQUEST_FOR_INFORMATION":
				event = IpnEventType.REQUEST_FOR_INFORMATION;
				break;

			case "NOTIFICATION_OF_CHARGEBACK":
				event = IpnEventType.NOTIFICATION_OF_CHARGEBACK;
				break;

			case "CHARGEBACK":
				// Handle CHARGEBACK notification. Payment was charged back. This is not sent if
				// a REQUEST_FOR_INFORMATION or
				// NOTIFICATION_OF_CHARGEBACK notification has already been sent.
				event = IpnEventType.CHARGEBACK;
				break;

			case "CHARGEBACK_REVERSED":
				event = IpnEventType.CHARGEBACK_REVERSED;
				break;

			case "REPORT_AVAILABLE":
				// Handle REPORT_AVAILABLE notification.
				// There is a new report available, the URL of the report is in the "reason"
				// field.
				event = IpnEventType.REPORT_AVAILABLE;
				break;

			}

			result.add(event.setMerchantReference(merchantReference).setMessage(notification.getString("reason")));
		}

		consumer.accept(result);
	}

	public static AuthorizationResult authorise(PaymentRequest req) {

		Payment payment = new Payment(getAydenClient());

		com.adyen.model.PaymentRequest request = BillingHelper.getAydenPaymentRequest(req);

		try {

			PaymentResult paymentResult = payment.authorise(request);
			return BillingHelper.toAuthorizationResult(paymentResult);

		} catch (Exception e) {
			Logger.get().error(e.getMessage());
			return new AuthorizationResult().setError(true).setErrorMessage(Exceptions.recurseCause(e).getMessage());
		}
	}

	public static CaptureResult capture(CaptureRequest req) {

		Modification modification = new Modification(getAydenClient());

		com.adyen.model.modification.CaptureRequest capture = BillingHelper.getAydenCaptureRequest(req);

		try {

			ModificationResult captureResult = modification.capture(capture);
			return BillingHelper.toCaptureResult(captureResult);

		} catch (IOException | ApiException e) {
			Logger.get().error(e.getMessage());
			return new CaptureResult().setError(true).setErrorMessage(Exceptions.recurseCause(e).getMessage());
		}
	}

	@Todo
	public static void CancelOrRefundPayment() {

	}

	@Todo
	public static void CancelPayment() {

	}

	@Todo
	public static void RefundPayment() {

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
