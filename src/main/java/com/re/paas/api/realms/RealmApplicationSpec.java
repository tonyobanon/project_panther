package com.re.paas.api.realms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.models.classes.ApplicationDeclineReason;
import com.re.paas.api.sentences.ObjectType;

public class RealmApplicationSpec {

	private boolean requiresReview;
	private Functionality reviewFunctionality;
	private ObjectType baseObjectType;
	private ListableIndex<?> listableIndex;
	private List<String> variableNames = new ArrayList<>();
	private BiConsumer<Long, Long> onAccept;
	private BiConsumer<Long, ApplicationDeclineReason> onDecline;
	private String listingRefField;
	private boolean autoAccept;


	public boolean isRequiresReview() {
		return requiresReview;
	}

	public RealmApplicationSpec setRequiresReview(boolean requiresReview) {
		this.requiresReview = requiresReview;
		return this;
	}

	public Functionality getReviewFunctionality() {
		return reviewFunctionality;
	}

	public RealmApplicationSpec setReviewFunctionality(Functionality reviewFunctionality) {
		this.reviewFunctionality = reviewFunctionality;
		return this;
	}

	public ObjectType getBaseObjectType() {
		return baseObjectType;
	}

	public RealmApplicationSpec setBaseObjectType(ObjectType baseObjectType) {
		this.baseObjectType = baseObjectType;
		return this;
	}

	public ListableIndex<?> getListableIndex() {
		return listableIndex;
	}

	public RealmApplicationSpec setListableIndex(ListableIndex<?> listableIndex) {
		this.listableIndex = listableIndex;
		return this;
	}

	public List<String> getVariableNames() {
		return variableNames;
	}

	public RealmApplicationSpec setVariableNames(List<String> variableNames) {
		this.variableNames = variableNames;
		return this;
	}

	public BiConsumer<Long, Long> getOnAccept() {
		return onAccept;
	}

	public RealmApplicationSpec setOnAccept(BiConsumer<Long, Long> onAccept) {
		this.onAccept = onAccept;
		return this;
	}

	public BiConsumer<Long, ApplicationDeclineReason> getOnDecline() {
		return onDecline;
	}

	public RealmApplicationSpec setOnDecline(BiConsumer<Long, ApplicationDeclineReason> onDecline) {
		this.onDecline = onDecline;
		return this;
	}

	public String getListingRefField() {
		return listingRefField;
	}

	public RealmApplicationSpec setListingRefField(String listingRefField) {
		this.listingRefField = listingRefField;
		return this;
	}

	public boolean isAutoAccept() {
		return autoAccept;
	}

	public RealmApplicationSpec setAutoAccept(boolean autoAccept) {
		this.autoAccept = autoAccept;
		return this;
	}
}
