package com.re.paas.api.infra.database.textsearch;

import com.re.paas.api.infra.database.document.PrimaryKey;

public class Checkpoint {

	private int partitionId;

	private Integer[] entityTypes;
	private String keyword;

	private Integer limit;
	private PrimaryKey lastEvaluatedKey;

	
	public Checkpoint(String keyword) {
		this.keyword = keyword;
		this.entityTypes = new Integer[] {};
		this.limit = 12;
		partitionId = 1;
	}

	public Checkpoint(String keyword, Integer limit) {
		this.keyword = keyword;
		this.entityTypes = new Integer[] {};
		this.limit = limit;
		this.partitionId = 1;
	}

	public Checkpoint(String keyword, int limit, Integer... entityTypes) {
		this.keyword = keyword;
		this.entityTypes = entityTypes;
		this.limit = limit;
		this.partitionId = 1;
	}

	public int getPartitionId() {
		return partitionId;
	}

	public Checkpoint setPartitionId(int partitionId) {
		this.partitionId = partitionId;
		return this;
	}

	public Checkpoint incrementPartitionId(int partitionId) {
		this.partitionId += partitionId;
		return this;
	}

	public PrimaryKey getLastEvaluatedKey() {
		return lastEvaluatedKey;
	}

	public Checkpoint setLastEvaluatedKey(PrimaryKey lastEvaluatedKey) {
		this.lastEvaluatedKey = lastEvaluatedKey;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer[] getEntityType() {
		return entityTypes;
	}

	public void setEntityType(Integer... entityTypes) {
		this.entityTypes = entityTypes;
	}

}
