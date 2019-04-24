package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.tasks.CronJob;
import com.re.paas.api.utils.Dates;
import com.re.paas.apps.rex.models.BaseAgentModel;
import com.re.paas.internal.billing.Authorise3dSecureRequest;
import com.re.paas.internal.billing.AuthorizationResult;
import com.re.paas.internal.billing.BaseCardInfo;
import com.re.paas.internal.billing.InvoiceAuthorizationResult;
import com.re.paas.internal.billing.InvoiceItem;
import com.re.paas.internal.billing.InvoicePaymentSpec;
import com.re.paas.internal.billing.InvoicePaymentStatus;
import com.re.paas.internal.billing.InvoiceSpec;
import com.re.paas.internal.billing.InvoiceStatus;
import com.re.paas.internal.billing.IpnEventType;
import com.re.paas.internal.billing.PaymentRequest;
import com.re.paas.internal.classes.CronInterval;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.models.errors.BillingError;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.tables.payments.BillingContextTable;
import com.re.paas.internal.models.tables.payments.CardTable;
import com.re.paas.internal.models.tables.payments.InvoiceItemTable;
import com.re.paas.internal.models.tables.payments.InvoicePaymentHistoryTable;
import com.re.paas.internal.models.tables.payments.InvoicePaymentTable;
import com.re.paas.internal.models.tables.payments.InvoiceStatusHistoryTable;
import com.re.paas.internal.models.tables.payments.InvoiceTable;

public class BillingModel extends BaseModel {

	@Override
	public String path() {
		return "core/payments";
	}

	@Override
	public void preInstall() {

		// Create cron for automatically authorizing payments for all due invoices

		ConfigModel.put(ConfigKeys.PAYMENT_SETTLEMENT_CRON_JOB_ID,
				TaskModel.newTask("Payment Settlement Cron Job", CronInterval.MONTHLY, CronJob.get(() -> {
					payAllPendingInvoices();
					return TaskExecutionOutcome.SUCCESS;
				}), -1, true));

		ConfigModel.put(ConfigKeys.PAYMENT_SETTLEMENT_CRON_INTERVAL, CronInterval.MONTHLY.getValue());
	}

	@Override
	public void install(InstallOptions options) {

	}

	public static Long setPaymentMethod(Long accountId, BaseCardInfo spec) {

		CardTable e = EntityHelper.fromObjectModel(accountId, spec);
		Long id = ofy().save().entity(e).now().getId();

		// Add to activity stream
		return id;
	}

	public static void removePaymentMethod(Long accountId) {

		ofy().delete().type(CardTable.class).id(accountId);

		// Add to activity stream
	}

	public static BaseCardInfo getPaymentMethod(Long accountId) {

		CardTable e = ofy().load().type(CardTable.class).id(accountId).now();

		if (e != null) {
			return EntityHelper.toObjectModel(e);
		} else {
			return null;
		}
	}

	public static void onNotificationReceived(IpnEventType event) {

		Long invoicePaymentId = event.getMerchantReference();

		InvoicePaymentHistoryTable e = ofy().load().type(InvoicePaymentHistoryTable.class).id(invoicePaymentId)
				.safe();

		InvoicePaymentStatus status = InvoicePaymentStatus.from(e.getStatus());

		switch (event) {

		case AUTHORISATION_FAILED:

		case AUTHORISATION_SUCCESS:

			if (status == InvoicePaymentStatus.PENDING_3D_SECURE_AUTHORIZATION) {

				// continue payment flow

			}

			break;
		case CAPTURE:
			break;
		case CAPTURE_FAILED:
			break;
		}

		switch (event) {
		case CANCELLATION:
			break;
		case CANCEL_OR_REFUND:
			break;
		case CHARGEBACK:
			break;
		case CHARGEBACK_REVERSED:
			break;
		case NOTIFICATION_OF_CHARGEBACK:
			break;
		case REFUND:
			break;
		case REFUNDED_REVERSED:
			break;
		case REPORT_AVAILABLE:
			break;
		case REQUEST_FOR_INFORMATION:
			break;
		}

	}

	public static InvoiceAuthorizationResult authorizeInvoicePayment(InvoiceSpec e) {

		InvoicePaymentSpec ip = new InvoicePaymentSpec().setInvoiceId(e.getId())
				// .setExtReference(null)
				.setDateCreated(Dates.now()).setDateUpdated(Dates.now());

		// Create Invoice Payment, set to CREATED
		createInvoicePayment(ip.setStatus(InvoicePaymentStatus.CREATED)
				.setMessage(ClientRBRef.get("invoice_payment_is_created")).setReconciled(true));

		// Create Invoice Payment, set to PENDING_AUTHORIZATION
		Long pendingStatus = createInvoicePayment(ip.setStatus(InvoicePaymentStatus.PENDING_AUTHORIZATION)
				.setMessage(ClientRBRef.get("invoice_payment_is_pending_authorization")).setReconciled(false));

		// Create Payment Request
		PaymentRequest paymentRequest = BaseAgentModel.createPaymentRequest(e.getAccountId())
				.setReference(pendingStatus.toString()).setCurrency(e.getCurrency()).setAmount(e.getTotalDue());

		AuthorizationResult paymentResult = BasePaymentModel.authorise(paymentRequest);

		if (paymentResult.isError()) {

			createInvoicePayment(pendingStatus,
					ip.setStatus(InvoicePaymentStatus.AUTHORIZATION_FAILED)
							.setMessage(ClientRBRef.get("error_occurred_while_authorizing_invoice_payment"))
							.setAdditionalInfo(paymentResult.getErrorMessage()).setReconciled(true));

		} else {

			ip.setExtReference(paymentResult.getExtReference());

			switch (paymentResult.getResultCode()) {

			case AUTHORISED:

				createInvoicePayment(pendingStatus, ip.setStatus(InvoicePaymentStatus.AUTHORIZATION_SUCCESS)
						.setMessage(ClientRBRef.get("invoice_payment_passed_authorization")));

				return InvoiceAuthorizationResult.success(paymentResult.getAuthCode());

			case REFUSED:

				createInvoicePayment(pendingStatus,
						ip.setStatus(InvoicePaymentStatus.AUTHORIZATION_FAILED)
								.setMessage(ClientRBRef.get("invoice_payment_failed_authorization"))
								.setAdditionalInfo(paymentResult.getRefusalReason()));

				break;

			case REDIRECTSHOPPER:
				// @Todo
				createInvoicePayment(pendingStatus, ip.setStatus(InvoicePaymentStatus.PENDING_3D_SECURE_AUTHORIZATION));

				Authorise3dSecureRequest request = paymentResult.getAuthorise3dRequest();

				// Given the request above, construct a suitable redirect url

				String redirectUrl = "...";

				return InvoiceAuthorizationResult.redirect(redirectUrl);

			case RECEIVED:
				// @Todo

			default:

				createInvoicePayment(pendingStatus,
						ip.setStatus(InvoicePaymentStatus.AUTHORIZATION_FAILED)
								.setMessage(ClientRBRef.get("invoice_payment_failed_authorization"))
								.setAdditionalInfo(paymentResult.getResultCode().toString()));
				break;

			}
		}

		return null;
	}

	public static boolean captureInvoicePayment(String authCode, InvoiceSpec spec) {

		return true;
	}

	// 0: success
	// set end date
	// update status to SUCCESSFUL by calling updateInvoiceStatus(..)
	// call newInvoice(..)

	// 1: first failure
	// update status to defaulting
	// rename payment invoice title to reflect the next month

	// 2: second failure
	// mark account as suspended

	// 3: for manually orchestrated payment
	// set end date

	public static void payOustandingInvoices(Long invoiceId) {

		EntityUtils.lazyQuery(InvoiceTable.class, QueryFilter.get("isOutstanding", true)).forEach(e -> {

			InvoiceSpec spec = EntityHelper.toObjectModel(e);

			InvoiceAuthorizationResult result = authorizeInvoicePayment(spec);

			if (result.getIsSuccess()) {

				if (captureInvoicePayment(result.getAuthCode(), spec)) {
					// ...
				}
			}

			if (result.isRedirectShopper()) {

				// Send SMS to customer, to enable him redirect to 3d secure URL
				result.getRedirectUrl();
			}

		});
	}

	public static void payAllOutstandingInvoices() {

	}

	public static void payAllPendingInvoices() {

	}

	public static void payAllDefaultingInvoices() {

	}

	public static InvoicePaymentSpec getInvoicePayment(Long invoiceId) {

		InvoicePaymentTable e = ofy().load().type(InvoicePaymentTable.class).id(invoiceId).safe();
		return EntityHelper.toObjectModel(e);
	}

	public static Long createInvoicePayment(InvoicePaymentSpec spec) {
		return createInvoicePayment(null, spec);
	}

	public static Long createInvoicePayment(Long id, InvoicePaymentSpec spec) {

		InvoicePaymentTable e = EntityHelper.fromObjectModel(spec);
		InvoicePaymentHistoryTable e2 = EntityHelper.fromObjectModel2(id, spec);

		ofy().save().entities(e, e2).now();

		e.setMerchantReference(e2.getId());

		ofy().save().entity(e).now();

		return e2.getId();
	}

	public static List<InvoicePaymentSpec> listInvoicePaymentHistory(Long invoiceId) {

		List<InvoicePaymentSpec> result = new ArrayList<InvoicePaymentSpec>();
		EntityUtils.query(InvoicePaymentHistoryTable.class, QueryFilter.get("invoiceId", invoiceId.toString()))
				.forEach(e -> {
					result.add(EntityHelper.toObjectModel(e));
				});
		return result;
	}

	public static InvoiceSpec getInvoice(Long invoiceId) {

		// fetch entity
		InvoiceTable ie = ofy().load().type(InvoiceTable.class).id(invoiceId).safe();

		// add items
		List<InvoiceItem> items = new ArrayList<>();

		ofy().load().type(InvoiceItemTable.class).ids(ie.getItems()).forEach((k, v) -> {
			items.add(EntityHelper.toObjectModel(v));
		});

		InvoiceSpec result = EntityHelper.toObjectModel(ie).setItems(items);

		return result;
	}

	public static Long newInvoice(InvoiceSpec spec) {

		// An invoice can only be created, if the previous one for the user ==
		// COMPLETED, or if none exists yet for the user yet

		BillingContextTable bce = ofy().load().type(BillingContextTable.class).id(spec.getAccountId()).now();

		if (bce != null) {
			if (InvoiceStatus.from(bce.getStatus()).isOutstanding()) {
				throw new PlatformException(BillingError.PREVIOUS_INVOICE_IS_OUTSTANDING);
			}
		}

		// Create Invoice
		InvoiceTable ie = EntityHelper.fromObjectModel(spec);

		ofy().save().entity(ie).now();

		// Set default billing context

		bce = new BillingContextTable().setAccountId(ie.getAccountId()).setInvoiceId(ie.getId())
				.setStatus(ie.getStatus()).setCurrency(ie.getCurrency()).setTotalDue(ie.getTotalDue())
				.setDateUpdated(Dates.now());

		ofy().save().entity(bce).now();

		updateInvoiceStatus(ie.getId(), InvoiceStatus.CREATED, ClientRBRef.get("invoice_was_created"));

		return ie.getId();
	}

	public static Long addItemToInvoice(Long invoiceId, InvoiceItem item) {

		if (item.getAmount().equals(0)) {
			throw new PlatformException(BillingError.NO_AMOUNT_ON_INVOICE_ITEM);
		}

		InvoiceTable ie = ofy().load().type(InvoiceTable.class).id(invoiceId).safe();

		InvoiceStatus iStatus = InvoiceStatus.from(ie.getStatus());

		BillingContextTable bce = ofy().load().type(BillingContextTable.class).id(ie.getAccountId()).safe();

		if (iStatus == InvoiceStatus.CREATED) {

			iStatus = InvoiceStatus.PENDING;

			ie.setStatus(InvoiceStatus.PENDING.getValue());
			bce.setStatus(InvoiceStatus.PENDING.getValue());
		}

		if (!iStatus.isUpdatable()) {
			throw new PlatformException(BillingError.CURRENT_INVOICE_STATUS_CANNOT_BE_UPDATED);
		}

		Date now = Dates.now();

		// add item
		InvoiceItemTable iie = EntityHelper.fromObjectModel(item);

		ofy().save().entity(iie).now();

		// update invoice entity

		ie.addItem(iie.getId());

		// update totals
		BigDecimal amount = BigDecimal.valueOf(iie.getAmount());

		ie.incrementTotalDue(amount).setDateUpdated(now);
		bce.incrementTotalDue(amount).setDateUpdated(now);

		ofy().save().entities(ie, bce).now();

		return iie.getId();
	}

	public static void updateInvoiceCurrency(Long invoiceId, String newCurrency) {

		BigDecimal newTotal = BigDecimal.ZERO;

		InvoiceTable ie = ofy().load().type(InvoiceTable.class).id(invoiceId).safe();

		BillingContextTable bce = ofy().load().type(BillingContextTable.class).id(ie.getAccountId()).safe();

		// recalculate amount on invoice items

		Collection<InvoiceItemTable> iies = ofy().load().type(InvoiceItemTable.class).ids(ie.getItems()).values();

		for (InvoiceItemTable iie : iies) {

			Double amount = CurrencyModel.getCurrencyRate(ie.getCurrency(), newCurrency) * iie.getAmount();
			iie.setAmount(amount);

			newTotal.add(BigDecimal.valueOf(amount));
		}

		// update invoice, billing context

		ie.setCurrency(newCurrency).setTotalDue(newTotal).setDateUpdated(Dates.now());
		bce.setCurrency(newCurrency).setTotalDue(newTotal).setDateUpdated(Dates.now());

		ofy().save().entities(iies, ie, bce).now();
	}

	public static void updateInvoiceStatus(Long invoiceId, InvoiceStatus status, ClientRBRef message) {

		InvoiceTable ie = ofy().load().type(InvoiceTable.class).id(invoiceId).safe();

		boolean save = !ie.getStatus().equals(status.getValue());

		ie.setStatus(status.getValue());

		InvoiceStatusHistoryTable e = new InvoiceStatusHistoryTable().setInvoiceId(invoiceId)
				.setStatus(status.getValue()).setMessage(message).setDateCreated(Dates.now());

		List<Object> entities = new ArrayList<>();
		entities.add(e);

		if (save) {

			BillingContextTable bce = ofy().load().type(BillingContextTable.class).id(ie.getAccountId()).safe();
			bce.setStatus(status.getValue());

			entities.add(ie);
			entities.add(bce);
		}

		ofy().save().entities(entities).now();
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
