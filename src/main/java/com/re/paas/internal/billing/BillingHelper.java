package com.re.paas.internal.billing;

import java.math.RoundingMode;

import com.adyen.model.Address;
import com.adyen.model.Amount;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.model.PaymentResult.ResultCodeEnum;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;

public class BillingHelper {

	public static PaymentRequest getAydenPaymentRequest(com.re.paas.internal.billing.PaymentRequest req) {

		PaymentRequest request = new PaymentRequest();

		request.setAmountData(req.getAmount().setScale(2, RoundingMode.HALF_UP).toString(), req.getCurrency());
		request.setReference(req.getReference());

		request.setShopperEmail(req.getCustomerEmail());
		request.setShopperReference(req.getCustomerId().toString());
		request.setTelephoneNumber(req.getCustomerPhone());
		request.setShopperLocale(req.getCustomerLocale());

		Address address = new Address();
		BillingAddress billingAddress = req.getBillingAddress();

		address.setHouseNumberOrName(billingAddress.getHouseNumberOrName());
		address.setStreet(billingAddress.getStreet());
		address.setCity(billingAddress.getCity());
		address.setPostalCode(billingAddress.getPostalCode());
		address.setStateOrProvince(billingAddress.getStateOrProvince());
		address.setCountry(billingAddress.getCountry());

		request.setBillingAddress(address);

		if (req.getCardInfo() instanceof CseTokenInfo) {

			String cardToken = ((CseTokenInfo) req.getCardInfo()).getCseToken();
			request.setCSEToken(cardToken);

		} else {

			CardInfo card = ((CardInfo) req.getCardInfo());

			request.setCardData(card.getCardNumber(), card.getCardHolder(), card.getExpiryMonth(), card.getExpiryYear(),
					card.getCvc());
		}

		return request;
	}

	public static AuthorizationResult toAuthorizationResult(PaymentResult res) {

		AuthorizationResult result = new AuthorizationResult();

		result.setPspReference(res.getPspReference());
		result.setResultCode(PaymentResultCode.valueOf(res.getResultCode().name()));

		if (res.isAuthorised()) {
			result.setAuthCode(res.getAuthCode());
		}

		if (res.isRefused()) {
			result.setIsError(true).setRefusalReason(res.getRefusalReason());
		}

		if (res.getResultCode() == ResultCodeEnum.REDIRECTSHOPPER) {
			result.setIs3dSecureOffered(true).setAuthorise3dRequest(
					new Authorise3dSecureRequest(res.getPaRequest(), res.getMd(), res.getIssuerUrl()));
		}

		return result;
	}

	public static CaptureRequest getAydenCaptureRequest(com.re.paas.internal.billing.CaptureRequest req) {

		CaptureRequest request = new CaptureRequest();

		request.setAuthorisationCode(req.getAuthCode());

		Amount amount = new Amount();
		amount.setCurrency(req.getCurrency());
		amount.setValue(req.getAmount().longValueExact());

		request.setModificationAmount(amount);

		request.setOriginalReference(req.getOriginalExtReference());
		request.setReference(req.getReference());

		return request;
	}

	public static CaptureResult toCaptureResult(ModificationResult res) {

		CaptureResult result = new CaptureResult();

		result.setReference(res.getPspReference());
		result.setError(false);

		return result;
	}
}