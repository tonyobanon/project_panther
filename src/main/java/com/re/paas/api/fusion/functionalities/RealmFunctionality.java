package com.re.paas.api.fusion.functionalities;

import com.re.paas.api.classes.ModifyType;

public class RealmFunctionality {

	private final Functionality functionality;
	
	private ModifyType modifyType = ModifyType.ADD;
	
	RealmFunctionality(Functionality functionality) {
		this.functionality = functionality;
	}
	
	public Functionality getFunctionality() {
		return functionality;
	}
	
	public ModifyType getModifyType() {
		return modifyType;
	}
	
	public RealmFunctionality setModifyType(ModifyType modifyType) {
		this.modifyType = modifyType;
		return this;
	}
	
	public final String asString() {
		return functionality.asString();
	}
	
}
