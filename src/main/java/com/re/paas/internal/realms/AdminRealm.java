package com.re.paas.internal.realms;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.models.listables.IndexedNameTypes;
import com.re.paas.internal.sentences.ObjectTypes;

public final class AdminRealm extends Realm {

	@Override
	public String name() {
		return "Admin";
	}

	@Override
	public Functionality[] functionalities() {
		List<Functionality> o = new FluentArrayList<Functionality>();
		for (Functionality f : Functionality.all()) {
			if (f.requiresAuth()) {
				o.add(f);
			}
		}
		return o.toArray(new Functionality[o.size()]);
	}

	@Override
	public Section[] onboardingForm() {
		return new Section[] {};
	}

	@Override
	public Integer authority() {
		return 20;
	}

	@Override
	public RealmApplicationSpec applicationSpec() {
		return new RealmApplicationSpec()
				.setRequiresReview(true)
				.setBaseObjectType(ObjectTypes.ADMIN_APPLICATION)
				.setReviewFunctionality(UserApplicationFunctionalities.REVIEW_ADMIN_APPLICATION)
				.setIndexedNameType(IndexedNameTypes.ADMIN_APPLICATION);
	}
}
