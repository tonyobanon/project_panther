package com.re.paas.internal.billing;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.BOOL;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.NS;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ReturnValue;
import com.re.paas.api.infra.database.model.UpdateItemResult;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.models.BaseUserModel;
import com.re.paas.integrated.models.CurrencyModel;
import com.re.paas.integrated.models.errors.BillingError;
import com.re.paas.integrated.tables.defs.payments.BillingContextTable;
import com.re.paas.integrated.tables.defs.payments.CardTable;
import com.re.paas.integrated.tables.defs.payments.InvoiceItemTable;
import com.re.paas.integrated.tables.defs.payments.InvoicePaymentHistoryTable;
import com.re.paas.integrated.tables.defs.payments.InvoicePaymentTable;
import com.re.paas.integrated.tables.defs.payments.InvoiceStatusHistoryTable;
import com.re.paas.integrated.tables.defs.payments.InvoiceTable;
import com.re.paas.integrated.tables.spec.payments.BillingContextTableSpec;
import com.re.paas.integrated.tables.spec.payments.CardTableSpec;
import com.re.paas.integrated.tables.spec.payments.InvoiceItemTableSpec;
import com.re.paas.integrated.tables.spec.payments.InvoicePaymentHistoryTableSpec;
import com.re.paas.integrated.tables.spec.payments.InvoicePaymentTableSpec;
import com.re.paas.integrated.tables.spec.payments.InvoiceStatusHistoryTableSpec;
import com.re.paas.integrated.tables.spec.payments.InvoiceTableSpec;
import com.re.paas.integrated.tables.spec.users.BaseUserTableSpec;
import com.re.paas.internal.i18n.LocationModel;

public class BillingModel extends BaseModel {

	@Override
	public String path() {
		return "core/payments";
	}

	@Override
	public void preInstall() {
	}

	@Override
	public void install(InstallOptions options) {

	}

	public static void setPaymentMethod(Long accountId, BaseCardInfo spec) {

		Item i = new Item().withLong(CardTableSpec.ACCOUNT_ID, accountId).withDate(CardTableSpec.DATE_CREATED,
				Dates.now());

		if (spec instanceof CardInfo) {

			CardInfo info = (CardInfo) spec;

			i.with(CardTableSpec.CARD_NUMBER, info.getCardNumber())
					.with(CardTableSpec.CARD_HOLDER, info.getCardHolder())
					.with(CardTableSpec.EXPIRY_MONTH, info.getExpiryMonth())
					.with(CardTableSpec.EXPIRY_YEAR, info.getExpiryYear()).with(CardTableSpec.CVC, info.getCvc());

		} else {
			CseTokenInfo info = (CseTokenInfo) spec;
			i.with(CardTableSpec.CSE_TOKEN, info.getCseToken()).with(CardTableSpec.CARD_SUFFIX, info.getCardSuffix());
		}

		// Todo: Add to activity stream

	}

	public static void removePaymentMethod(Long accountId) {

		Database.get().getTable(CardTable.class).deleteItem(DeleteItemSpec.forKey(CardTableSpec.ACCOUNT_ID, accountId));

		// Add to activity stream
	}

	public static BaseCardInfo getPaymentMethod(Long accountId) {

		Item item = Database.get().getTable(CardTable.class)
				.getItem(GetItemSpec.forKey(CardTableSpec.ACCOUNT_ID, accountId));

		String cseToken = item.getString(CardTableSpec.CSE_TOKEN);

		if (cseToken == null) {

			CardInfo spec = new CardInfo().setAccountId(accountId)
					.setCardHolder(item.getString(CardTableSpec.CARD_HOLDER))
					.setCardNumber(item.getString(CardTableSpec.CARD_NUMBER)).setCvc(item.getString(CardTableSpec.CVC))
					.setExpiryMonth(item.getString(CardTableSpec.EXPIRY_MONTH))
					.setExpiryYear(item.getString(CardTableSpec.EXPIRY_YEAR))
					.setDateCreated(item.getDate(CardTableSpec.DATE_CREATED));

			return spec;
		} else {

			CseTokenInfo spec = new CseTokenInfo().setAccountId(accountId)
					.setDateCreated(item.getDate(CardTableSpec.DATE_CREATED)).setCseToken(cseToken)
					.setCardSuffix(item.getString(CardTableSpec.CARD_SUFFIX));

			return spec;
		}
	}

	public static void onNotificationReceived(IpnEventType event) {

		Long reference = event.getReference();

		Item item = Database.get().getTable(InvoicePaymentHistoryTable.class).getItem(GetItemSpec
				.forKey(InvoicePaymentHistoryTableSpec.ID, reference, InvoicePaymentHistoryTableSpec.STATUS));

		PaymentStatus status = PaymentStatus.from(item.getInt(InvoicePaymentHistoryTableSpec.STATUS));

		switch (event) {

		case AUTHORISATION_FAILED:

		case AUTHORISATION_SUCCESS:

			if (status == PaymentStatus.PENDING_3D_SECURE_AUTHORIZATION) {

				// continue payment flow

			}

			break;
		case CAPTURE:
			break;
		case CAPTURE_FAILED:
			break;
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
		default:
			break;
		}
	}

	@BlockerTodo("Create a unified way to store user address, for easy parsing")
	protected static PaymentRequest createPaymentRequest(InvoiceSpec spec) {

		PaymentRequest req = new PaymentRequest();

		UserProfileSpec profile = BaseUserModel.getProfile(spec.getAccountId(), BaseUserTableSpec.EMAIL,
				BaseUserTableSpec.PHONE, BaseUserTableSpec.ADDRESS, BaseUserTableSpec.CITY, BaseUserTableSpec.TERRITORY,
				BaseUserTableSpec.COUNTRY, BaseUserTableSpec.PREFERRED_LOCALE);

		req.setCustomerEmail(profile.getEmail()).setCustomerId(profile.getId()).setCustomerPhone(profile.getPhone())
				.setCustomerLocale(profile.getPreferredLocale());

		BillingAddress billingAddress = new BillingAddress();

		String[] address = profile.getAddress().split(",[\\s]*");

		billingAddress.setHouseNumberOrName(address[0]);
		billingAddress.setStreet(profile.getAddress().replace(address[0], ""));
		billingAddress.setPostalCode(LocationModel.getPostalCode(profile.getCity()).toString());
		billingAddress.setCity(LocationModel.getCityName(profile.getCity()));
		billingAddress.setStateOrProvince(LocationModel.getTerritoryName(profile.getTerritory()));
		billingAddress.setCountry(profile.getCountry());

		req.setBillingAddress(billingAddress);

		BaseCardInfo cardInfo = BillingModel.getPaymentMethod(spec.getAccountId());
		req.setCardInfo(cardInfo);

		return req;
	}

	@BlockerTodo
	private static void process(PaymentRequest req) {

	}

	@BlockerTodo("This implementation is unusable and very incomplete, please reference the api docs")
	public static InvoiceAuthorizationResult authorizeInvoicePayment(InvoiceSpec e) {

		// Create Invoice Payment
		Long reference = createInvoicePayment(e.getId());

		// Create Payment Request
		PaymentRequest paymentRequest = createPaymentRequest(e).setReference(reference.toString())
				.setCurrency(e.getCurrency()).setAmount(e.getTotalDue());

		// Call payment gateway
		AuthorizationResult paymentResult = BasePaymentModel.authorise(paymentRequest);

		if (paymentResult.getIsError()) {

			updateInvoicePayment(reference, paymentResult.getPspReference(),
					ClientRBRef.get("error_occurred_while_authorizing_invoice_payment"),
					paymentResult.getErrorMessage(), PaymentStatus.AUTHORIZATION_FAILED);

		} else {

			switch (paymentResult.getResultCode()) {

			case AUTHORISED:

				updateInvoicePayment(reference, paymentResult.getPspReference(),
						ClientRBRef.get("invoice_payment_passed_authorization"), null,
						PaymentStatus.AUTHORIZATION_SUCCESS);

				return InvoiceAuthorizationResult.success(paymentResult.getAuthCode());

			case REFUSED:

				updateInvoicePayment(reference, paymentResult.getPspReference(),
						ClientRBRef.get("invoice_payment_failed_authorization"), paymentResult.getErrorMessage(),
						PaymentStatus.AUTHORIZATION_FAILED);

				return InvoiceAuthorizationResult.failed(paymentResult.getErrorMessage());

			case REDIRECTSHOPPER:

				updateInvoicePayment(reference, paymentResult.getPspReference(),
						ClientRBRef.get("invoice_payment_pending_3d_secure_authorization"), null,
						PaymentStatus.PENDING_3D_SECURE_AUTHORIZATION);

				// Authorise3dSecureRequest request = paymentResult.getAuthorise3dRequest();

				// Given the request above, construct a suitable redirect url

				String redirectUrl = "...";

				return InvoiceAuthorizationResult.redirect(redirectUrl);

			case RECEIVED:
				// @Todo

			default:

				// @Todo

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

	@BlockerTodo
	public static void payAllOutstandingInvoices() {

		Stream<QueryResult> queryResult = Database.get().getTable(InvoiceTable.class)
				.getIndex(InvoiceTableSpec.IS_OUTSTANDING_INDEX).query(new ExpressionSpecBuilder()
						.withKeyCondition(BOOL(InvoiceTableSpec.IS_OUTSTANDING).eq(true)).buildForQuery());

		queryResult.forEach(page -> {

			List<Long> invoiceIds = page.getItems().stream().map(item -> item.getLong(InvoiceTableSpec.ID))
					.collect(Collectors.toList());

			getInvoices(invoiceIds.toArray(new Long[invoiceIds.size()])).forEach(spec -> {

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
		});
	}

	public static InvoicePaymentSpec getInvoicePayment(Long invoiceId) {

		Item i = Database.get().getTable(InvoicePaymentTable.class)
				.getItem(GetItemSpec.forKey(InvoicePaymentTableSpec.INVOICE_ID, invoiceId));

		return new InvoicePaymentSpec().setInvoiceId(invoiceId.toString())
				.setReference(i.getString(InvoicePaymentTableSpec.REFERENCE))
				.setGatewayReference(i.getString(InvoicePaymentTableSpec.GATEWAY_REFERENCE))
				.setStatus(PaymentStatus.from(i.getInt(InvoicePaymentTableSpec.STATUS)))
				.setMessage(ClientRBRef.forAll(i.getString(InvoicePaymentTableSpec.MESSAGE)))
				.setDateCreated(i.getDate(InvoicePaymentTableSpec.DATE_CREATED))
				.setDateUpdated(i.getDate(InvoicePaymentTableSpec.DATE_UPDATED));
	}

	@BlockerTodo
	public static Long updateInvoicePayment(Long reference, String gatewayReference, ClientRBRef message,
			String additionalInfo, PaymentStatus status) {

		// Get invoice_id for this reference from <invoice payment history>

		// Update gateway reference for <invoice payment history>
		// Update previous status for <invoice payment history>
		// Set additionalInfo for <invoice payment history>
		// Set message for <invoice payment history>
		// Set current status for <invoice payment history>
		// Based on status, set reconciled flag for <invoice payment history>

		// Set gateway reference for <invoice payment>
		// Set message for <invoice payment>
		// Set current status for <invoice payment>

		// If status is successful, update invoice outstanding status to false

		return null;

	}

	public static Long createInvoicePayment(Long invoiceId) {

		Date now = Dates.now();

		// Create invoice payment history

		Long reference = Database.get().getTable(InvoicePaymentHistoryTable.class)
				.putItem(PutItemSpec.forItem(new Item()

						.with(InvoicePaymentHistoryTableSpec.INVOICE_ID, invoiceId)
						.with(InvoicePaymentHistoryTableSpec.STATUS, PaymentStatus.CREATED)
						.with(InvoicePaymentHistoryTableSpec.MESSAGE,
								ClientRBRef.get("invoice_payment_is_created").toString())
						.with(InvoicePaymentHistoryTableSpec.DATE_CREATED, now)
						.with(InvoicePaymentHistoryTableSpec.DATE_UPDATED, now)

				).withReturnValues(ReturnValue.ALL_NEW)).getItem().getLong(InvoicePaymentHistoryTableSpec.ID);

		// Update invoice payment
		Database.get().getTable(InvoicePaymentTable.class).updateItem(

				new ExpressionSpecBuilder()

						.withCondition(N(InvoicePaymentTableSpec.INVOICE_ID).eq(invoiceId))

						.addUpdate(S(InvoicePaymentTableSpec.REFERENCE).set(reference.toString()))
						.addUpdate(N(InvoicePaymentTableSpec.STATUS).set(PaymentStatus.CREATED.getValue()))
						.addUpdate(D(InvoicePaymentTableSpec.DATE_UPDATED).set(now))

						.buildForUpdate());

		return reference;
	}

	public static List<InvoicePaymentSpec> getInvoicePayments(String invoiceId) {

		Collection<Item> entries = Database.get().getTable(InvoicePaymentHistoryTable.class)
				.getIndex(InvoicePaymentHistoryTableSpec.INVOICE_ID_INDEX).all(new ExpressionSpecBuilder()
						.withKeyCondition(S(InvoicePaymentHistoryTableSpec.INVOICE_ID).eq(invoiceId)).buildForQuery());

		return entries.stream().map(item -> getInvoicePaymentHistory(item)).collect(Collectors.toList());
	}

	public static InvoicePaymentSpec getInvoicePaymentHistory(Item i) {

		return new InvoicePaymentHistorySpec()
				.setAdditionalInfo(ClientRBRef.forAll(i.getString(InvoicePaymentHistoryTableSpec.ADDITIONAL_INFO)))
				.setPreviousStatus(InvoiceStatus.from(i.getInt(InvoicePaymentHistoryTableSpec.PREVIOUS_STATUS)))
				.setInvoiceId(i.getString(InvoicePaymentHistoryTableSpec.INVOICE_ID))
				.setGatewayReference(i.getString(InvoicePaymentHistoryTableSpec.GATEWAY_REFERENCE))
				.setStatus(PaymentStatus.from(i.getInt(InvoicePaymentHistoryTableSpec.STATUS)))
				.setMessage(ClientRBRef.forAll(i.getString(InvoicePaymentHistoryTableSpec.MESSAGE)))
				.setDateCreated(i.getDate(InvoicePaymentHistoryTableSpec.DATE_CREATED))
				.setDateUpdated(i.getDate(InvoicePaymentHistoryTableSpec.DATE_UPDATED));

	}

	public static InvoiceSpec getInvoice(Item item) {

		return new InvoiceSpec().setId(item.getLong(InvoiceTableSpec.ID))
				.setAccountId(Long.parseLong(item.getString(InvoiceTableSpec.ACCOUNT_ID)))
				.setStatus(InvoiceStatus.from(item.getInt(InvoiceTableSpec.STATUS)))
				.setStartDate(item.getDate(InvoiceTableSpec.START_DATE))
				.setEndDate(item.getDate(InvoiceTableSpec.END_DATE))

				.setItems(

						item.getNumberSet(InvoiceTableSpec.ITEMS).stream().map(itemId -> getInvoiceItem(itemId))
								.collect(Collectors.toList()))

				.setCurrency(item.getString(InvoiceTableSpec.CURRENCY))
				.setTotalDue(BigDecimal.valueOf(item.getDouble(InvoiceTableSpec.TOTAL_DUE)))
				.setComment(ClientRBRef.forAll(item.getString(InvoiceTableSpec.COMMENT)))
				.setDateCreated(item.getDate(InvoiceTableSpec.DATE_CREATED))
				.setDateUpdated(item.getDate(InvoiceTableSpec.DATE_UPDATED));
	}

	public static InvoiceSpec getInvoice(Long invoiceId) {

		Item item = Database.get().getTable(InvoiceTable.class).getItem(
				new ExpressionSpecBuilder().withCondition(N(InvoiceTableSpec.ID).eq(invoiceId)).buildForGetItem());

		return getInvoice(item);
	}

	public static List<InvoiceSpec> getInvoices(Long... invoiceIds) {

		GetItemsSpec spec = GetItemsSpec.forKeys(Arrays.asList(invoiceIds).stream()
				.map(invoiceId -> new PrimaryKey(InvoiceTableSpec.ID, invoiceId)).collect(Collectors.toList()));

		return Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(InvoiceTable.class, spec))
				.getResponses(InvoiceTable.class).stream().map(item -> getInvoice(item)).collect(Collectors.toList());
	}

	public static InvoiceItem getInvoiceItem(Number itemId) {

		Item invoiceItem = Database.get().getTable(InvoiceItemTable.class).getItem(
				new ExpressionSpecBuilder().withCondition(N(InvoiceItemTableSpec.ID).eq(itemId)).buildForGetItem());

		return new InvoiceItem().setId(itemId.longValue())
				.setName(ClientRBRef.forAll(invoiceItem.getString(InvoiceItemTableSpec.NAME)))
				.setDescription(ClientRBRef.forAll(invoiceItem.getString(InvoiceItemTableSpec.DESCRIPTION)))
				.setAmount(invoiceItem.getDouble(InvoiceItemTableSpec.AMOUNT))
				.setDateCreated(invoiceItem.getDate(InvoiceItemTableSpec.DATE_CREATED));

	}

	public static Long newInvoice(Long accountId, InvoiceSpec spec) {

		// An invoice can only be created, if the previous one for the user ==
		// COMPLETED, or if none exists yet for the user yet

		InvoiceStatus status = getBillingContextStatus(accountId);

		if (status != null && status.isOutstanding()) {
			throw new PlatformException(BillingError.PREVIOUS_INVOICE_IS_OUTSTANDING);
		}

		Date now = Dates.now();

		// Create Invoice

		Long invoiceId = Database.get().getTable(InvoiceItemTable.class)
				.putItem(PutItemSpec.forItem(new Item().with(InvoiceTableSpec.ACCOUNT_ID, accountId)
						.with(InvoiceTableSpec.IS_OUTSTANDING, false)
						// .with(InvoiceTableSpec.STATUS, InvoiceStatus.CREATED.getValue())
						.with(InvoiceTableSpec.START_DATE, now).with(InvoiceTableSpec.CURRENCY, spec.getCurrency())
						.with(InvoiceTableSpec.TOTAL_DUE, spec.getTotalDue())
						.with(InvoiceTableSpec.COMMENT, spec.getComment()).with(InvoiceTableSpec.DATE_CREATED, now)
						.with(InvoiceTableSpec.DATE_UPDATED, now)).withReturnValues(ReturnValue.ALL_NEW))
				.getItem().getLong(InvoiceTableSpec.ID);

		// Create empty Invoice Payment

		Database.get().getTable(InvoicePaymentTable.class)
				.putItem(PutItemSpec.forItem(new Item().with(InvoicePaymentTableSpec.INVOICE_ID, invoiceId)));

		// Update billing context for associated account

		Database.get().getTable(BillingContextTable.class).updateItem(

				new ExpressionSpecBuilder()

						.withCondition(N(BillingContextTableSpec.ACCOUNT_ID).eq(accountId))

						.addUpdate(S(BillingContextTableSpec.INVOICE_ID).set(invoiceId.toString()))
						.addUpdate(S(BillingContextTableSpec.CURRENCY).set(spec.getCurrency()))
						.addUpdate(N(BillingContextTableSpec.TOTAL_DUE).set(spec.getTotalDue()))
						.addUpdate(D(BillingContextTableSpec.DATE_UPDATED).set(now))

						.buildForUpdate());

		// Quick concern: Should status update be deferred until this call ?, rather
		// than done when the entities are created / updated above
		updateInvoiceStatus(invoiceId, InvoiceStatus.CREATED, ClientRBRef.get("invoice_was_created"));

		return invoiceId;
	}

	public static Long addItemToInvoice(Long invoiceId, InvoiceItem item) {

		if (item.getAmount().equals(0D)) {
			throw new PlatformException(BillingError.NO_AMOUNT_ON_INVOICE_ITEM);
		}

		ExpressionSpecBuilder invoiceUpdate = new ExpressionSpecBuilder()
				.withCondition(N(InvoiceTableSpec.ID).eq(invoiceId));

		ExpressionSpecBuilder billingContextUpdate = new ExpressionSpecBuilder()
				.withCondition(N(BillingContextTableSpec.ACCOUNT_ID).eq(getAccountId(invoiceId)));

		InvoiceStatus status = getInvoiceStatus(invoiceId);

		if (status == InvoiceStatus.CREATED) {

			status = InvoiceStatus.PENDING;

			invoiceUpdate.addUpdate(N(InvoiceTableSpec.STATUS).set(InvoiceStatus.PENDING.getValue()));
			billingContextUpdate.addUpdate(N(BillingContextTableSpec.STATUS).set(InvoiceStatus.PENDING.getValue()));
		}

		if (!status.isUpdatable()) {
			throw new PlatformException(BillingError.CURRENT_INVOICE_STATUS_CANNOT_BE_UPDATED);
		}

		Date now = Dates.now();

		Long itemId = Database.get().getTable(InvoiceItemTable.class)
				.putItem(
						PutItemSpec
								.forItem(new Item().with(InvoiceItemTableSpec.NAME, item.getName())
										.with(InvoiceItemTableSpec.DESCRIPTION, item.getDescription())
										.with(InvoiceItemTableSpec.AMOUNT, item.getAmount())
										.with(InvoiceItemTableSpec.DATE_CREATED, now))
								.withReturnValues(ReturnValue.ALL_NEW))
				.getItem().getLong(InvoiceItemTableSpec.ID);

		// update invoice to include itemId
		invoiceUpdate.addUpdate(NS(InvoiceTableSpec.ITEMS).append(itemId));

		// update totals on invoice and (billing context for the associated account)
		BigDecimal amount = BigDecimal.valueOf(item.getAmount());

		invoiceUpdate.addUpdate(N(InvoiceTableSpec.TOTAL_DUE).set(N(InvoiceTableSpec.TOTAL_DUE).plus(amount)))
				.addUpdate(D(InvoiceTableSpec.DATE_UPDATED).set(now));

		billingContextUpdate
				.addUpdate(N(BillingContextTableSpec.TOTAL_DUE).set(N(BillingContextTableSpec.TOTAL_DUE).plus(amount)))
				.addUpdate(D(BillingContextTableSpec.DATE_UPDATED).set(now));

		Database.get().getTable(InvoiceTable.class).updateItem(invoiceUpdate.buildForUpdate());
		Database.get().getTable(BillingContextTable.class).updateItem(billingContextUpdate.buildForUpdate());

		return itemId;
	}

	public static void updateInvoiceCurrency(Long invoiceId, String newCurrency) {

		BigDecimal newTotal = BigDecimal.ZERO;

		// First, get the invoice item
		Item invoiceItem = Database.get().getTable(InvoiceTable.class).getItem(GetItemSpec.forKey(InvoiceTableSpec.ID,
				invoiceId, InvoiceTableSpec.ACCOUNT_ID, InvoiceTableSpec.ITEMS, InvoiceTableSpec.CURRENCY));

		Long accountId = invoiceItem.getLong(InvoiceTableSpec.ACCOUNT_ID);
		Double rate = CurrencyModel.getCurrencyRate(invoiceItem.getString(InvoiceTableSpec.CURRENCY), newCurrency);

		// Recalculate amount on invoice items

		invoiceItem.getNumberSet(InvoiceTableSpec.ITEMS).forEach(itemId -> {

			// Since BatchWriteItem does not support UpdateItem requests, we need to perform
			// updates singularly.
			// Also. note that we would have performed an atomic update (by doing a
			// multiplication of the current amount)

			Double amount = rate * getItemAmount(itemId.longValueExact());

			Database.get().getTable(InvoiceItemTable.class)
					.updateItem(new ExpressionSpecBuilder().withCondition(N(InvoiceItemTableSpec.ID).eq(itemId))
							.addUpdate(N(InvoiceItemTableSpec.AMOUNT).set(amount)).buildForUpdate());

			newTotal.add(BigDecimal.valueOf(amount));
		});

		// Update invoice

		Database.get().getTable(InvoiceTable.class)
				.updateItem(new ExpressionSpecBuilder().withCondition(N(InvoiceTableSpec.ID).eq(invoiceId))
						.addUpdate(S(InvoiceTableSpec.CURRENCY).set(newCurrency))
						.addUpdate(N(InvoiceTableSpec.TOTAL_DUE).set(newTotal))
						.addUpdate(D(InvoiceTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate());

		// Update billing context for the associated account

		Database.get().getTable(BillingContextTable.class).updateItem(
				new ExpressionSpecBuilder().withCondition(N(BillingContextTableSpec.ACCOUNT_ID).eq(accountId))
						.addUpdate(S(BillingContextTableSpec.CURRENCY).set(newCurrency))
						.addUpdate(N(BillingContextTableSpec.TOTAL_DUE).set(newTotal))
						.addUpdate(D(BillingContextTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate());

	}

	private static Long getAccountId(Long invoiceId) {

		Table t = Database.get().getTable(InvoiceTable.class);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(InvoiceTableSpec.ACCOUNT_ID)
				.withCondition(N(InvoiceTableSpec.ID).eq(invoiceId));

		GetItemSpec spec = expr.buildForGetItem();
		return t.getItem(spec).getLong(InvoiceTableSpec.ACCOUNT_ID);
	}

	private static Double getItemAmount(Long itemId) {

		Table t = Database.get().getTable(InvoiceItemTable.class);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(InvoiceItemTableSpec.AMOUNT)
				.withCondition(N(InvoiceItemTableSpec.ID).eq(itemId));

		GetItemSpec spec = expr.buildForGetItem();
		return t.getItem(spec).getDouble(InvoiceItemTableSpec.AMOUNT);
	}

	private static InvoiceStatus getBillingContextStatus(Long accountId) {

		Table t = Database.get().getTable(BillingContextTable.class);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(BillingContextTableSpec.STATUS)
				.withCondition(N(BillingContextTableSpec.ACCOUNT_ID).eq(accountId));

		GetItemSpec spec = expr.buildForGetItem();
		Item i = t.getItem(spec);

		if (i != null) {
			Integer status = i.getInt(BillingContextTableSpec.STATUS);
			return InvoiceStatus.from(status);
		} else {
			return null;
		}
	}

	private static InvoiceStatus getInvoiceStatus(Long invoiceId) {

		Table t = Database.get().getTable(InvoiceTable.class);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(InvoiceTableSpec.STATUS)
				.withCondition(N(InvoiceTableSpec.ID).eq(invoiceId));

		GetItemSpec spec = expr.buildForGetItem();
		Integer status = t.getItem(spec).getInt(InvoiceTableSpec.STATUS);

		return InvoiceStatus.from(status);
	}

	public static void updateInvoiceStatus(Long invoiceId, InvoiceStatus newStatus, ClientRBRef message) {

		// Update invoice status
		UpdateItemResult invoiceUpdateResult = Database.get().getTable(InvoiceTable.class)
				.updateItem(new ExpressionSpecBuilder().withCondition(N(InvoiceTableSpec.ID).eq(invoiceId))
						.addUpdate(N(InvoiceTableSpec.STATUS).set(newStatus.getValue())).buildForUpdate()
						.withReturnValues(ReturnValue.ALL_OLD));

		// Add to invoice status history
		Database.get().getTable(InvoiceStatusHistoryTable.class)
				.putItem(PutItemSpec
						.forItem(new Item().withString(InvoiceStatusHistoryTableSpec.INVOICE_ID, invoiceId.toString())
								.withInt(InvoiceStatusHistoryTableSpec.STATUS, newStatus.getValue())
								.withString(InvoiceStatusHistoryTableSpec.MESSAGE, message.toString())
								.withDate(InvoiceStatusHistoryTableSpec.DATE_CREATED, Dates.now())));

		// Update the billing context for the associated account, only if the invoice
		// status changed

		if (!invoiceUpdateResult.getItem().getInt(InvoiceTableSpec.STATUS).equals(newStatus.getValue())) {
			Database.get().getTable(BillingContextTable.class)
					.updateItem(new ExpressionSpecBuilder()
							.withCondition(N(BillingContextTableSpec.ACCOUNT_ID).eq(getAccountId(invoiceId)))
							.addUpdate(N(InvoiceTableSpec.STATUS).set(newStatus.getValue())).buildForUpdate());
		}
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
