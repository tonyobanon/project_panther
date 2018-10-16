package com.re.paas.internal.documents.pdf.gen;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.re.paas.api.forms.Column;
import com.re.paas.api.forms.SizeSpec;

public class Row implements Iterable<Column> {

	private SizeSpec padding;
	private float width;

	private SizeSpec componentSizeSpec;
	private SizeSpec fontSpec;

	private LinkedList<Column> columnValues;

	public Row(SizeSpec fontSpec) {
		this.columnValues = new LinkedList<Column>();
		this.fontSpec = fontSpec;

	}

	public Row withColumn(Column o) {
		if(o != null) {
			this.columnValues.add(o);
		}
		return this;
	}

	public Row withComponentSizeSpec(SizeSpec sizeSpec) {
		this.componentSizeSpec = sizeSpec;
		return this;
	}

	public Row withFontSpec(SizeSpec fontSpec) {
		this.fontSpec = fontSpec;
		return this;
	}

	public Row withColumns(Column... values) {
		this.columnValues = new LinkedList<Column>();
		for (Column o : values) {
			this.columnValues.add(o);
		}
		return this;
	}

	public Collection<Column> getColumns() {
		return this.columnValues;
	}

	public SizeSpec getFontSpec() {
		return fontSpec;
	}

	public SizeSpec getComponentSizeSpec() {
		return componentSizeSpec;
	}

	public int length() {
		return columnValues.size();
	}

	@Override
	public Iterator<Column> iterator() {
		return columnValues.iterator();
	}

	public float getWidth() {
		return width;
	}

	protected Row withWidth(float width) {
		this.width = width;
		for (Column o : columnValues) {
			o.withContainerWidth(this.width);
		}
		return this;
	}

	public SizeSpec getPadding() {
		return padding;
	}

	public Row withPadding(SizeSpec padding) {
		this.padding = padding;
		return this;
	}
	
	

}
