package com.re.paas.api.forms.input;

public enum InputType {

	HIDDEN, FILE, TEXT, EMAIL, SECRET, BOOLEAN, PHONE, DATE_OF_BIRTH, NUMBER, NUMBER_2L, NUMBER_3L, NUMBER_4L, AMOUNT,
	IMAGE, PLAIN, SIGNATURE, LANGUAGE, COUNTRY, ADDRESS, TERRITORY, CITY, PREFERED_LOCALE, TIMEZONE;

	private TypeDescriptor descriptor;

	private InputType() {
	}

	private InputType(TypeDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public TypeDescriptor getDescriptor() {
		return descriptor;
	}

	public InputType setDescriptor(TypeDescriptor descriptor) {
		this.descriptor = descriptor;
		return this;
	}

}
