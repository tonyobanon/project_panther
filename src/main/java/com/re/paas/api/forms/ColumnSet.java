package com.re.paas.api.forms;

import java.util.ArrayList;
import java.util.List;

public class ColumnSet extends ColumnCollection {

	private List<Column> columns;
	private boolean singleRow;
	
	private SizeSpec rowPadding;
	private SizeSpec rowFontSpec;
	private SizeSpec rowComponentSizeSpec;

	public ColumnSet() {
		columns = new ArrayList<Column>();
	}

	public ColumnSet(List<Column> columns, boolean singleRow) {
		this.columns = columns;
		this.singleRow = singleRow;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public ColumnSet add(Column o) {
		columns.add(o);
		return this;
	}

	public ColumnSet setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public boolean isSingleRow() {
		return singleRow;
	}

	public ColumnSet setSingleRow(boolean singleRow) {
		this.singleRow = singleRow;
		return this;
	}
	
	public SizeSpec getRowPadding() {
		return rowPadding;
	}

	public ColumnSet setRowPadding(SizeSpec rowPadding) {
		this.rowPadding = rowPadding;
		return this;
	}

	public SizeSpec getRowFontSpec() {
		return rowFontSpec;
	}

	public ColumnSet setRowFontSpec(SizeSpec rowFontSpec) {
		this.rowFontSpec = rowFontSpec;
		return this;
	}

	public SizeSpec getRowComponentSizeSpec() {
		return rowComponentSizeSpec;
	}

	public ColumnSet setRowComponentSizeSpec(SizeSpec rowComponentSizeSpec) {
		this.rowComponentSizeSpec = rowComponentSizeSpec;
		return this;
	}

}
