package com.re.paas.internal.listable.search;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import com.re.paas.api.listable.search.AbstractDocument;
import com.re.paas.api.listable.search.AbstractIndexableField;
import com.re.paas.api.listable.search.IndexableFieldType;

public class DocumentImpl implements AbstractDocument {

	private final List<AbstractIndexableField> fields;
	
	public DocumentImpl() {
		this.fields = new ArrayList<>();
	}
	
	public DocumentImpl(AbstractDocument doc) {
		this.fields = doc.getFields();
	}
	
	public DocumentImpl(Document doc) {
		
		List<IndexableField> fields = doc.getFields();
		List<AbstractIndexableField> fieldList = new ArrayList<>(fields.size());
		
		for(IndexableField field : fields) {
			fieldList.add(new IndexableFieldImpl(field));
		}
		
		this.fields = fieldList;
	}
	
	public DocumentImpl(List<IndexableField> fields) {

		List<AbstractIndexableField> fieldList = new ArrayList<>(fields.size());

		for (IndexableField f : fields) {
			fieldList.add(new IndexableFieldImpl(f));
		}

		this.fields = fieldList;
	}
	
	public final Document asLuceneDocument() {
		Document doc = new Document();
		
		for(AbstractIndexableField f : fields) {
			IndexableFieldImpl field = new IndexableFieldImpl(f);
			doc.add(field.asLuceneField());
		}
		return doc;
	}

	@Override
	public Iterator<AbstractIndexableField> iterator() {
		return fields.iterator();
	}

	public DocumentImpl add(String name, Object value, IndexableFieldType type) {
		fields.add(new IndexableFieldImpl(name, value, new FieldType(type)));
		return this;
	}
	
	@Override
	public void add(AbstractIndexableField field) {
		fields.add(field);
	}

	@Override
	public void removeField(String name) {
		Iterator<AbstractIndexableField> it = fields.iterator();
		while (it.hasNext()) {
			AbstractIndexableField field = it.next();
			if (field.name().equals(name)) {
				it.remove();
				return;
			}
		}
	}

	@Override
	public void removeFields(String name) {
		Iterator<AbstractIndexableField> it = fields.iterator();
		while (it.hasNext()) {
			AbstractIndexableField field = it.next();
			if (field.name().equals(name)) {
				it.remove();
			}
		}
	}

	@Override
	public ByteBuffer[] getBinaryValues(String name) {
		final List<ByteBuffer> result = new ArrayList<>();
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name)) {
				final ByteBuffer bytes = field.binaryValue();
				if (bytes != null) {
					result.add(bytes);
				}
			}
		}

		return result.toArray(new ByteBuffer[result.size()]);
	}

	@Override
	public ByteBuffer getBinaryValue(String name) {
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name)) {
				final ByteBuffer bytes = field.binaryValue();
				if (bytes != null) {
					return bytes;
				}
			}
		}
		return null;
	}

	@Override
	public AbstractIndexableField getField(String name) {
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name)) {
				return field;
			}
		}
		return null;
	}

	@Override
	public AbstractIndexableField[] getFields(String name) {
		List<AbstractIndexableField> result = new ArrayList<>();
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name)) {
				result.add(field);
			}
		}

		return result.toArray(new AbstractIndexableField[result.size()]);
	}

	@Override
	public List<AbstractIndexableField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	private final static String[] NO_STRINGS = new String[0];

	@Override
	public String[] getValues(String name) {
		List<String> result = new ArrayList<>();
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name) && field.stringValue() != null) {
				result.add(field.stringValue());
			}
		}

		if (result.size() == 0) {
			return NO_STRINGS;
		}

		return result.toArray(new String[result.size()]);
	}

	@Override
	public String get(String name) {
		for (AbstractIndexableField field : fields) {
			if (field.name().equals(name) && field.stringValue() != null) {
				return field.stringValue();
			}
		}
		return null;
	}

	/** Prints the fields of a document for human consumption. */
	@Override
	public final String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Document<");
		for (int i = 0; i < fields.size(); i++) {
			AbstractIndexableField field = fields.get(i);
			buffer.append(field.toString());
			if (i != fields.size() - 1) {
				buffer.append(" ");
			}
		}
		buffer.append(">");
		return buffer.toString();
	}

	/** Removes all the fields from document. */
	public void clear() {
		fields.clear();
	}
}
