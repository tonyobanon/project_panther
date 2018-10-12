package com.re.paas.api.forms;


public abstract class AbstractField {

	protected String id;
	private Reference reference;

	public AbstractField(String id) {
		this.id = id;
	}
	
	public static AbstractField copyOf(AbstractField src) {
		
		AbstractField field = null;
		
		if(src instanceof SimpleField) {
			field = new SimpleField();
		} else if(src instanceof CompositeField) {
			field = new CompositeField();
		}
		
		field.importData(src);
		return field;
	}
	
	/**
	 * Copies the section without copying its reference
	 * @param source The section to copy
	 * @param withFields
	 */
	public abstract void importData(AbstractField source);
	
	public AbstractField setId(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}
	
	public String hash(){
		return Integer.toString(hashCode());
	}
	
	public Reference getReference() {
		return reference;
	}

	public AbstractField setReference(Reference reference) {
		this.reference = reference;
		return this;
	}

}
