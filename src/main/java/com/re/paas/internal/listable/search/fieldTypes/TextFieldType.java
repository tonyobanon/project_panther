package com.re.paas.internal.listable.search.fieldTypes;

import com.re.paas.api.listable.search.IndexOptions;
import com.re.paas.internal.listable.search.FieldType;

public final class TextFieldType {

	/** Indexed, tokenized, not stored. */
	public static final FieldType TYPE_NOT_STORED = new FieldType();

	/** Indexed, tokenized, stored. */
	public static final FieldType TYPE_STORED = new FieldType();

	static {
		TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_NOT_STORED.setTokenized(true);
		TYPE_NOT_STORED.freeze();

		TYPE_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.freeze();
	}
}