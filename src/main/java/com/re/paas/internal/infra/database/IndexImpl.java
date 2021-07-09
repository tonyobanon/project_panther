package com.re.paas.internal.infra.database;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

public class IndexImpl implements Index {

	private final com.amazonaws.services.dynamodbv2.document.Index awsIndex;
	private final Table table;
	private final Namespace namespace;
	
	IndexImpl(Table table, com.amazonaws.services.dynamodbv2.document.Index awsIndex) {
		this.awsIndex = awsIndex;
		this.table = table;
		this.namespace = Namespace.from(table.name(), awsIndex.getIndexName());
	}

	@Override
	public Table getTable() {
		return table;
	}

	@Override
	public IndexDescriptor getDescriptor() {
		return new IndexDescriptor(table.name(), awsIndex.getIndexName());
	}

	@Override
	public QueryResult query(QuerySpec spec) {
		
		ItemCollection<QueryOutcome> a = awsIndex.query(Marshallers.toQuerySpec(spec));
		com.amazonaws.services.dynamodbv2.model.QueryResult b = a.getLastLowLevelResult().getQueryResult();

		CapacityProvisioner.consume(namespace, Marshallers.fromConsumedCapacity(b.getConsumedCapacity()));
		
		return Marshallers.fromQueryResult(b);
	}

	@Override
	public ScanResult scan(ScanSpec spec) {
		
		ItemCollection<ScanOutcome> a = awsIndex.scan(Marshallers.toScanSpec(spec));
		com.amazonaws.services.dynamodbv2.model.ScanResult b = a.getLastLowLevelResult().getScanResult();

		CapacityProvisioner.consume(namespace, Marshallers.fromConsumedCapacity(b.getConsumedCapacity()));
		
		return Marshallers.fromScanResult(b);
	}
}
