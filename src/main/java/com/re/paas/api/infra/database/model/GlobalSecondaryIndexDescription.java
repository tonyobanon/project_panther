package com.re.paas.api.infra.database.model;

public class GlobalSecondaryIndexDescription extends IndexDefinition {
	
	public GlobalSecondaryIndexDescription(String tableName, String indexName) {
		super(tableName, indexName);
	}

	/**
	 * <p>
	 * The total size of the specified index, in bytes.
	 * </p>
	 */
	private Long indexSizeBytes;
	
	private IndexStatus indexStatus;
	

	public Long getIndexSizeBytes() {
		return indexSizeBytes;
	}

	public GlobalSecondaryIndexDescription setIndexSizeBytes(Long indexSizeBytes) {
		this.indexSizeBytes = indexSizeBytes;
		return this;
	}

	public IndexStatus getIndexStatus() {
		return indexStatus;
	}

	public GlobalSecondaryIndexDescription setIndexStatus(IndexStatus indexStatus) {
		this.indexStatus = indexStatus;
		return this;
	}
}
