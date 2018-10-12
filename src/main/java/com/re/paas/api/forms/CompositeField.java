package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeField extends BaseSimpleField {

	private String itemsSource;
	private Map<Object, Object> items;

	private ArrayList<String> defaultSelections;
	private boolean allowMultipleChoice;

	CompositeField() {
		super(null, null);
	}
	
	@Override
	public void importData(AbstractField source) {
		
		CompositeField sourceField = (CompositeField) source;

		this.setId(sourceField.getId());

		this.setTitle(sourceField.getTitle());
		this.setSortOrder(sourceField.getSortOrder());
		this.setContext(sourceField.getContext());

		this.setIsRequired(sourceField.getIsRequired());
		this.setIsVisible(sourceField.getIsVisible());
		this.setIsDefault(sourceField.getIsDefault());

		this.setItemsSource(sourceField.getItemsSource());
		this.withItems(sourceField.getItems());
		this.setDefaultSelections(sourceField.getDefaultSelections());
		this.setAllowMultipleChoice(sourceField.isAllowMultipleChoice());
		
	}
	
	public CompositeField(Object title) {
		this(null, title);
	}

	public CompositeField(Object id, Object title) {
		super(id, title);
		items = new HashMap<>();
	}

	public Map<Object, Object> getItems() {
		return items;
	}

	public CompositeField withItems(Map<Object, Object> items) {
		this.items.putAll(items);
		return this;
	}

	public CompositeField withItem(Object k, Object v) {
		this.items.put(k, v);
		return this;
	}

	public boolean isAllowMultipleChoice() {
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
