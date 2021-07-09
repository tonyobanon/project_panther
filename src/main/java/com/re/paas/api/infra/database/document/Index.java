package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

public interface Index {

	Table getTable();

	IndexDescriptor getDescriptor();

	QueryResult query(QuerySpec spec);

	ScanResult scan(ScanSpec spec);
}
