package com.re.paas.api.forms;


public abstract class AbstractField implements Cloneable {

	protected String id;

	public AbstractField(String id) {
		this.id = id;
	}
	
	@Override
	public AbstractField clone() {
		return AbstractField.copyOf(this);
	}
	
	static AbstractField copyOf(AbstractField src) {
		
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
	 * Copies the field
	 * 
	 * @param source The section to copy
	 * @param copyReference This indicates whether or not the reference should be copied from the source
	 */
	public abstract AbstractField importData(AbstractField source);
	
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

}
