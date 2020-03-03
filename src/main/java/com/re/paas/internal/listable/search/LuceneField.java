package com.re.paas.internal.listable.search;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableFieldType;

/**
 * This class was created to gain package-level access to the {@link fieldsData} field.
 * As this is necessary to directly assign a number value to it
 * 
 * @author anthonyanyanwu
 *
 */
public class LuceneField extends Field {

	public LuceneField(String name, byte[] value, IndexableFieldType type) {
		super(name, value, type);
	}

	public LuceneField(String name, String value, IndexableFieldType type) {
		super(name, value, type);
	}

	public LuceneField(String name, Number value, IndexableFieldType type) {
		super(name, type);
		this.fieldsData = value;
	}
}