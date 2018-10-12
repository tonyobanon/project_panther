package com.re.paas.api.reporting.pdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Table implements Iterable<Row>{
	
	private final TableConfig config;
	
	private int columns;
	private ArrayList<Row> rows = new ArrayList<Row>();
	private Iterator<Row> it;
		
	
	public Table() {
		this.config = new TableConfig();
	}
	
	public Table(TableConfig config) {
		this.config = config;
	}

	public Collection<Row> values(){
		return this.rows;
	}
	
	public int columns(){
		return this.columns;
	}
	
	public Row getRow(int index){
		return this.rows.get(index);
	}
	
	public int length(){
		return rows.size();
	}
	
	public Table commit(){
		it = rows.iterator();
		return this;
	}
	
	public void remove(Row o){
		this.rows.remove(o);
	}
	
	public Table withRow(Row o){
		this.rows.add(o.withWidth(config.getWidth()));
		return this;
	}
	

	@Override
	public Iterator<Row> iterator() {
		return new Iterator<Row>() {
			
			@Override
			public Row next() {
				Row o = it.next();
				columns = o.length();
				return o;
			}
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
		};
	}

	public TableConfig getConfig() {
		return config;
	}
}
