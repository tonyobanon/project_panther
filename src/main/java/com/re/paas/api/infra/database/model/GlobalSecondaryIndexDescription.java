package com.re.paas.api.infra.database.model;

public class GlobalSecondaryIndexDescription extends GlobalSecondaryIndexDefinition {
	
	private Long itemCount;
	
	private Boolean backfilling;

	/**
	 * <p>
	 * The total size of the specified index, in bytes.
	 * </p>
	 */
	private Long indexSizeBytes;
	
	private IndexStatus indexStatus;
	
	public GlobalSecondaryIndexDescription(String tableName, String indexName) {
		super(tableName, indexName);
	}

	public GlobalSecondaryIndexDescription(String indexName) {
		this(null, indexName);
	}
	
	
	public Long getItemCount() {
		return itemCount;
	}

	public GlobalSecondaryIndexDescription setItemCount(Long itemCount) {
		this.itemCount = itemCount;
		return this;
	}

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

	public Boolean isBackfilling() {
		return backfilling;
	}

	public GlobalSecondaryIndexDescription setBackfilling(Boolean backfilling) {
		this.backfilling = backfilling;
		return this;
	}
}
