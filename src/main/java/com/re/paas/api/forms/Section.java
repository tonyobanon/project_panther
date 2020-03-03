package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Section implements Cloneable {

	private String id;
	private SectionReference reference;
	private Object title;
	private Object summary;
	private List<AbstractField> fields = new ArrayList<AbstractField>();

	@Override
	public Section clone() {
		Section s = new Section();
		s.importData(this, true, false);
		return s;
	}

	public Section deepClone() {
		Section s = new Section();
		s.importData(this, true, true);
		return s;
	}

	/**
	 * Copies the section
	 * 
	 * @param source        The section to copy
	 * @param copyReference Specifies whether to copy reference
	 * @param withFields    Indicates that fields should also be copied. Note: a
	 *                      deep copy is created for the fields
	 */
	public Section importData(Section source, boolean copyReference, boolean withFields) {

		if (this == source) {
			return this;
		}

		if (copyReference && source.getReference() != null) {
			this.setReference(source.getReference());
		}

		if (source.getId() != null) {
			this.setId(source.getId());
		}

		if (source.getTitle() != null) {
			this.setTitle(source.getTitle());
		}

		if (source.getSummary() != null) {
			this.setSummary(source.getSummary());
		}

		if (withFields && !source.getFields().isEmpty()) {

			source.getFields().forEach(f -> {
				this.withField(AbstractField.copyOf(f, true));
			});
		}

		return this;
	}

	public AbstractField getField(Reference reference) {
		for (AbstractField field : fields) {
			if (field.getReference().value().equals(reference.value())) {
				return field;
			}
		}
		return null;
	}

	public AbstractField getField(String id) {
		for (AbstractField field : fields) {
			if (field.getId().equals(id)) {
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

	public void removeField(Reference reference) {
		Iterator<AbstractField> it = fields.iterator();
		while (it.hasNext()) {
			if (it.next().getReference().asString().equals(reference.asString())) {
				it.remove();
				break;
			}
		}
	}

	public Section withField(AbstractField field) {

		// First, remove any existing field with same reference
		this.filter(f -> !f.getReference().value().equals(field.getReference().value()));

		this.fields.add(field);
		return this;
	}

	public Section filter(Predicate<AbstractField> predicate) {
		Iterator<AbstractField> it = this.fields.iterator();

		while (it.hasNext()) {
			AbstractField f = it.next();
			if (!predicate.test(f)) {
				it.remove();
			}
		}

		return this;
	}

	public Section withFields(Collection<AbstractField> fields) {
		for (AbstractField f : fields) {
			withField(f);
		}
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
