package com.re.paas.api.forms;


public abstract class AbstractField implements Cloneable {

	protected String id;
	private Reference reference;

	public AbstractField(String id) {
		this.id = id;
	}
	
	@Override
	public AbstractField clone() {
		return AbstractField.copyOf(this, true);
	}
	
	static AbstractField copyOf(AbstractField src, boolean copyReference) {
		
		AbstractField field = null;
		
		if(src instanceof SimpleField) {
			field = new SimpleField();
		} else if(src instanceof CompositeField) {
			field = new CompositeField();
		}
		
		field.importData(src, copyReference);
		return field;
	}
	
	/**
	 * Copies the field
	 * 
	 * @param source The section to copy
	 * @param copyReference This indicates whether or not the reference should be copied from the source
	 */
	public abstract AbstractField importData(AbstractField source, boolean copyReference);
	
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
