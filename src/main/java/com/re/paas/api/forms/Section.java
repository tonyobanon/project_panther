package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Section {

	private String id;
	private SectionReference reference;
	private Object title;
	private Object summary;
	private List<AbstractField> fields = new ArrayList<AbstractField>();

	/**
	 * Copies the section without copying its reference
	 * @param source The section to copy
	 * @param withFields
	 */
	public void importData(Section source, boolean withFields) {
		
		if(this == source) {
			return;
		}
		
		setId(source.getId());
		setTitle(source.getTitle());
		setSummary(source.getSummary());
		if (withFields) {
			withFields(source.getFields());
		}
	}

	public AbstractField getField(Reference reference) {
		for (AbstractField field : fields) {
			if (field.getReference().value().equals(reference.value())) {
				return field;
			}
		}
		return null;
	}

	public List<AbstractField> getFields() {
		return fields;
	}

	public <T> List<T> getFields(Class<T> adapter) {
		List<T> fields = new ArrayList<>(this.fields.size());
		this.fields.forEach(f -> {
			@SuppressWarnings("unchecked")
			T field = (T) f;
			fields.add(field);
		});
		return fields;
	}

	public void removeField(String id) {
		Iterator<AbstractField> it = fields.iterator();
		while (it.hasNext()) {
			if (it.next().getId().equals(id)) {
				it.remove();
				break;
			}
		}
	}

	public Section withField(AbstractField field) {
		return withFields(Arrays.asList(field));
	}

	public Section withFields(List<AbstractField> fields) {
		this.fields.addAll(fields);
		return this;
	}

	public String getTitle() {
		return title.toString();
	}

	public Section setTitle(Object title) {
		this.title = title;
		return this;
	}

	public String getSummary() {
		return summary.toString();
	}

	public Section setSummary(Object summary) {
		this.summary = summary;
		return this;
	}

	public String getId() {
		return id;
	}

	public Section setId(String id) {
		this.id = id;
		return this;
	}

	public SectionReference getReference() {
		return reference;
	}

	public Section setReference(SectionReference reference) {
		this.reference = reference;
		return this;
	}

}
