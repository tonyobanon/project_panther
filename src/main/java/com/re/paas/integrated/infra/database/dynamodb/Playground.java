package com.re.paas.integrated.infra.database.dynamodb;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.model.QueryResult;

public class Playground {

	
	public Stream<QueryResult> query(QuerySpec spec) {
		
		long est = 0;// based on the limit, calculate an estimated size
		Stream<QueryResult> stream = StreamSupport.stream(Spliterators.spliterator(new Iterator<QueryResult>() {

			
			
			@Override
			public boolean hasNext() {
				
				// When, this is called, we start accumulated results until
				
				return false;
			}

			@Override
			public QueryResult next() {
				// TODO Auto-generated method stub
				return null;
			}
			
		}, est, Spliterator.ORDERED), false);
		
		return stream;
		
	}
	
	
}
