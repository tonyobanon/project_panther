package com.re.paas.internal.listable.search;

import java.nio.ByteBuffer;

import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.listable.search.AbstractIndexableField;
import com.re.paas.api.utils.ValueType;

public class IndexableFieldImpl implements AbstractIndexableField {

	private final String name;
	private final Object value;
	
	private final FieldType type;
	
	public IndexableFieldImpl(AbstractIndexableField f) {
		
		this.name = f.name();
		this.value = f.value();
		
		this.type = new FieldType(f.fieldType());
	}

	public IndexableFieldImpl(IndexableField f) {

		Object value = null;

		if (f.numericValue() != null) {
			value = f.numericValue();
		} else if (f.binaryValue() != null) {
			value = ByteBuffer.wrap(f.binaryValue().bytes);
		} else {
			value = f.stringValue();
		}

		this.name = f.name();
		this.type = new FieldType(f.fieldType());
		
		this.value = value;
	}
	
	public IndexableFieldImpl(String name, Object value) {
		this(name, value, new FieldType(TextField.TYPE_STORED));
	}

	public IndexableFieldImpl(String name, Object value, FieldType type) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Object value() {
		return value;
	}

	@Override
	public ByteBuffer binaryValue() {
		assertType(ValueType.BINARY);
		return (ByteBuffer) this.value;
	}

	@Override
	public String stringValue() {
		return this.value.toString();
	}

	@Override
	public Number numericValue() {
		assertType(ValueType.NUMBER);
		return (Number) this.value;
	}

	private final void assertType(ValueType expected) {
		ValueType type = ValueType.getType(this.value);
		if (type != expected) {
			Exceptions.throwRuntime(new IllegalArgumentException("Mismatched type"));
		}
	}

	public final IndexableField asLuceneField() {
		ValueType type = ValueType.getType(this.value);
		IndexableField f = null;

		switch (type) {
		case BINARY:
			f = new LuceneField(this.name, this.binaryValue().array(), this.fieldType().asLuceneFieldType());
			break;
		case NUMBER:
			f = new LuceneField(this.name, this.numericValue(), this.fieldType().asLuceneFieldType());
			break;
		default:
			f = new LuceneField(this.name, this.stringValue(), this.fieldType().asLuceneFieldType());
			break;
		}

		return f;
	}

	@Override
	public FieldType fieldType() {
		return type;
	}

}
