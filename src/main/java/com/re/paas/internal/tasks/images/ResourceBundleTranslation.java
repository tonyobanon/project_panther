package com.re.paas.internal.tasks.images;

import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.tasks.TaskImage;
import com.re.paas.internal.classes.TaskInterval;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;

public class ResourceBundleTranslation extends TaskImage {

	@Override
	public String name() {
		return "rb_translation";
	}

	@Override
	public ClientRBRef title() {
		return ClientRBRef.get("resource_bundle_translation_task");
	}

	@Override
	public List<AbstractField> fields() {

		AbstractServiceDelegate serviceDelegate = BaseService.getDelegate();

		return new FluentArrayList<AbstractField>().with(new CompositeField("target_country", ClientRBRef.get("country"))
				.setItemsSource(serviceDelegate.getFunctionalityService(LocationFunctionalities.GET_COUNTRY_NAMES).get(0))
				.setSortOrder(1).setIsDefault(true));
	}

	@Override
	public TaskExecutionOutcome call() {

		//String country = getParameters().get("target_country");

		// Do some stuff

		return TaskExecutionOutcome.SUCCESS;
	}

	@Override
	public TaskInterval interval() {
		return TaskInterval.EVERY_DAY;
	}

}
