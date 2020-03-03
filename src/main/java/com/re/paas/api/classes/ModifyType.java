package com.re.paas.api.classes;

public enum ModifyType {

	/**
	 * Section: Create a new section. <br>
	 * Field: Create a new field.
	 */
	ADD,

	/**
	 * Section: Removes an existing section. <br>
	 * Field: Removes an existing field.
	 */
	REMOVE,

	/**
	 * Section: Update the fields of an existing section Field: Copies the non-null
	 * <br>
	 * specified attributes into an existing field
	 */
	UPDATE;
}
