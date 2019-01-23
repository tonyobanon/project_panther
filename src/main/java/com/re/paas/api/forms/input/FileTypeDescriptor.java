package com.re.paas.api.forms.input;

public class FileTypeDescriptor extends TypeDescriptor {

	private boolean storeInternally;

	public FileTypeDescriptor(boolean storeInternally) {
		this.storeInternally = storeInternally;
	}

	public boolean isStoreInternally() {
		return storeInternally;
	}

	public FileTypeDescriptor setStoreInternally(boolean storeInternally) {
		this.storeInternally = storeInternally;
		return this;
	}

}
