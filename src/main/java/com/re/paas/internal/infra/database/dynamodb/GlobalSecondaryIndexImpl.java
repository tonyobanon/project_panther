package com.re.paas.internal.infra.database.dynamodb;

import com.re.paas.api.infra.database.document.GlobalSecondaryIndex;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.IndexStatus;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

class GlobalSecondaryIndexImpl implements GlobalSecondaryIndex {

	private final IndexImpl indexImpl;

	public GlobalSecondaryIndexImpl(IndexImpl indexImpl) {
		this.indexImpl = indexImpl;
	}

	@Override
	public TableImpl getTable() {
		return (TableImpl) indexImpl.getTable();
	}

	private GlobalSecondaryIndexDescription getDescription() {
		return getTable().getGsiDescription(indexImpl.name());
	}

	@Override
	public GlobalSecondaryIndexDescription getDescriptor() {
		return getDescription();
	}

	@Override
	public QueryResult query(QuerySpec spec) {
		return indexImpl.query(spec);
	}

	@Override
	public ScanResult scan(ScanSpec spec) {
		return indexImpl.scan(spec);
	}

	private void waitForActive0() {
		try {
			indexImpl.getAwsIndex().waitForActive();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public GlobalSecondaryIndex waitForActive() {

		IndexStatus status = getDescriptor().getIndexStatus();

		if (status == IndexStatus.CREATING) {

			waitForActive0();

			// We cannot be fully sure if the status is ACTIVE or UPDATING (because of
			// back-filling) so, we need to re-describe

			getTable().describe();
		}

		return this;
	}

	public GlobalSecondaryIndex delete() {
		getTable().deleteGSI(getDescriptor().getIndexName());
		return this;
	}
	
	private void waitForDelete0() {
		try {
			indexImpl.getAwsIndex().waitForDelete();
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	public GlobalSecondaryIndex waitForDelete() {

		IndexStatus status = getDescription().getIndexStatus();

		if (status == IndexStatus.DELETING) {
			waitForDelete0();
		}

		return this;
	}
}
