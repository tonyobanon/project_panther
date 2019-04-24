package com.re.paas.api.infra.database.document;

import java.util.stream.Stream;

import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

public interface Index {

	Table getTable();

	IndexDescriptor getDescriptor();

	Stream<QueryResult> query(QuerySpec spec);
	
	Stream<ScanResult> scan(ScanSpec spec);
}
