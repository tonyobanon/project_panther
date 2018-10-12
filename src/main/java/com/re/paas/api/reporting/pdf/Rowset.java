package com.re.paas.api.reporting.pdf;

import java.util.ArrayList;
import java.util.List;

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
