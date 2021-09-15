package com.re.paas.internal.infra.database.dynamodb;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.IndexStatus;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.exceptions.IndexNotReadyException;

public class IndexImpl implements Index {

	private final com.amazonaws.services.dynamodbv2.document.Index awsIndex;
	private final Table table;
	private final Namespace namespace;

	private final Type type;

	IndexImpl(Table table, com.amazonaws.services.dynamodbv2.document.Index awsIndex, Type type) {
		this.awsIndex = awsIndex;
		this.table = table;
		this.namespace = Namespace.from(table.name(), awsIndex.getIndexName());
		this.type = type;
	}

	@Override
	public TableImpl getTable() {
		return (TableImpl) table;
	}

	Type getType() {
		return type;
	}

	String name() {
		return awsIndex.getIndexName();
	}

	com.amazonaws.services.dynamodbv2.document.Index getAwsIndex() {
		return awsIndex;
	}

	@Override
	public IndexDescriptor getDescriptor() {
		return new IndexDescriptor(table.name(), awsIndex.getIndexName());
	}

	private boolean isIndexQueryable(boolean waitIfCreating) {

		if (getType() == Type.LSI) {

			// LSIs are inherently part of the table, and always queryable
			return true;
		}

		GlobalSecondaryIndexImpl index = (GlobalSecondaryIndexImpl) ((Index) this);

		// Note: Due to the time-sensitive nature of this method invocation, we are
		// unable to call describe() on the table every time to get the latest info,
		// so it is possible for the index info retrieved to be stale.

		IndexStatus status = index.getDescriptor().getIndexStatus();

		switch (status) {

		case UPDATING:
		case ACTIVE:
			return true;

		case CREATING:

			if (waitIfCreating) {

				index.waitForActive();
				return true;
			}

		case DELETING:
		default:
			return false;

		}
	}

	private void ensureIndexQueryable() {
		if (!isIndexQueryable(true)) {
			throw new IndexNotReadyException(name());
		}
	}

	@Override
	public QueryResult query(QuerySpec spec) {

		ensureIndexQueryable();

		ItemCollection<QueryOutcome> a = awsIndex.query(Marshallers.toQuerySpec(spec));
		com.amazonaws.services.dynamodbv2.model.QueryResult b = a.getLastLowLevelResult().getQueryResult();

		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity());
		
		return Marshallers.fromQueryResult(b);
	}

	@Override
	public ScanResult scan(ScanSpec spec) {

		ensureIndexQueryable();

		ItemCollection<ScanOutcome> a = awsIndex.scan(Marshallers.toScanSpec(spec));
		com.amazonaws.services.dynamodbv2.model.ScanResult b = a.getLastLowLevelResult().getScanResult();

		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity());
		
		return Marshallers.fromScanResult(b);
	}
}
