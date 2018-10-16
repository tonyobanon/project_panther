package com.re.paas.internal.documents.pdf.gen;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.forms.ColumnCollection;

public class Rowset extends ColumnCollection {

	private List<Row> rows;

	public Rowset() {
		this.rows = new ArrayList<Row>();
	}

	public Rowset(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> getRows() {
		return rows;
	}

	public Rowset setRows(List<Row> rows) {
		this.rows = rows;
		return this;
	}
	
	public Rowset withRow(Row row) {
		this.rows.add(row);
		return this;
	}
	
}
