package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeField extends BaseSimpleField {

	private String itemsSource;
	private Map<String, Object> items;

	private ArrayList<String> defaultSelections;
	private Boolean allowMultipleChoice;

	CompositeField() {
		super(null, null);
	}

	@Override
	public CompositeField importData(AbstractField source) {

		CompositeField sourceField = (CompositeField) source;

		
		if (sourceField.getId() != null) {
			this.setId(sourceField.getId());
		}
		
		if (sourceField.getTitle() != null) {
			this.setTitle(sourceField.getTitle());
		}

		if (sourceField.getSortOrder() != null) {
			this.setSortOrder(sourceField.getSortOrder());
		}

		if (sourceField.getContext() != null) {
			this.setContext(sourceField.getContext());
		}

		if (sourceField.getIsRequired() != null) {
			this.setIsRequired(sourceField.getIsRequired());
		}

		if (sourceField.getIsVisible() != null) {
			this.setIsVisible(sourceField.getIsVisible());
		}

		if (sourceField.getIsDefault() != null) {
			this.setIsDefault(sourceField.getIsDefault());
		}

		if (sourceField.getItemsSource() != null) {
			this.setItemsSource(sourceField.getItemsSource());
		}

		if ((sourceField.getItems() != null && !sourceField.getItems().isEmpty())) {
			this.withItems(sourceField.getItems());
		}

		if (sourceField.getDefaultSelections() != null && !sourceField.getDefaultSelections().isEmpty()) {
			this.setDefaultSelections(sourceField.getDefaultSelections());
		}

		if (sourceField.isAllowMultipleChoice() != null) {
			this.setAllowMultipleChoice(sourceField.isAllowMultipleChoice());
		}
		
		return this;
	}

	public CompositeField(String title) {
		this(null, title);
	}

	public CompositeField(Object id, String title) {
		super(id, title);
		items = new HashMap<>();
	}

	public Map<String, Object> getItems() {
		return items;
	}

	public CompositeField withItems(Map<String, Object> items) {
		this.items.putAll(items);
		return this;
	}

	public CompositeField withItem(String k, Object v) {
		this.items.put(k, v);
		return this;
	}

	public Boolean isAllowMultipleChoice() {
		return allowMultipleChoice;
	}

	public CompositeField setAllowMultipleChoice(boolean allowMultipleChoice) {
		this.allowMultipleChoice = allowMultipleChoice;
		return this;
	}

	public String getItemsSource() {
		return itemsSource;
	}

	public CompositeField setItemsSource(String itemsSource) {
		this.itemsSource = itemsSource;
		return this;
	}

	public ArrayList<String> getDefaultSelections() {
		return defaultSelections;
	}

	public CompositeField setDefaultSelections(List<String> defaultSelections) {
		this.defaultSelections = new ArrayList<>(defaultSelections);
		return this;
	}

}
