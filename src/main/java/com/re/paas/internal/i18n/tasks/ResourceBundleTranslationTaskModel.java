package com.re.paas.internal.i18n.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.tasks.Task;
import com.re.paas.api.tasks.TaskModel;
import com.re.paas.integrated.fusion.functionalities.LocationFunctionalities;

public class ResourceBundleTranslationTaskModel extends TaskModel {

	private static final String TARGET_COUNTRY_FIELD = "target_country";
	private static final String EXECUTION_DATE_FIELD = "execution_date";
	
	@Override
	public String name() {
		return "rb_translation";
	}

	@Override
	public ClientRBRef title() {
		return ClientRBRef.get("resource_bundle_translation_task");
	}

	@Override
	public List<Section> fields() {

		AbstractServiceDelegate serviceDelegate = BaseService.getDelegate();

		Section r = new Section()
				.setTitle(ClientRBRef.get("Choose target country"));
		
		List<AbstractField> fields = new FluentArrayList<AbstractField>()
				
				
				.with(new CompositeField(TARGET_COUNTRY_FIELD, ClientRBRef.get("country"))
						.setItemsSource(serviceDelegate.getService(LocationFunctionalities.GET_COUNTRY_NAMES).getUri())
						.setSortOrder(1)
						.setIsDefault(true))
				
				
				.with(new SimpleField(EXECUTION_DATE_FIELD, InputType.DATE, ClientRBRef.get("execution_date"))
						.setSortOrder(2)
						.setIsDefault(true))
				;
		
		r.withFields(fields);
		
		return Arrays.asList(r);
	}
	
	@Override
	@BlockerTodo
	public Task build(Map<String, Object> parameters) {
		
		// Based on the information provided by the user
		
		return null;
	}
}
