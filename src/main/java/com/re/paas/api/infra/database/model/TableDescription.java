package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableDescription extends BaseTableDefinition {

	private TableStatus tableStatus;

	/**
	 * <p>
	 * The total size of the specified table, in bytes. DynamoDB updates this value
	 * approximately every six hours. Recent changes might not be reflected in this
	 * value.
	 * </p>
	 */
	private Long tableSizeBytes;

	/**
	 * <p>
	 * The number of items in the specified table.
	 * </p>
	 */
	private Long itemCount;

	/**
	 * <p>
	 * The date and time when the table was created, in
	 * <a href="http://www.epochconverter.com/">UNIX epoch time</a> format.
	 * </p>
	 */
	private java.util.Date creationDateTime;

	private java.util.List<LocalSecondaryIndexDescription> localSecondaryIndexes = new ArrayList<>();

	private java.util.List<GlobalSecondaryIndexDescription> globalSecondaryIndexes = new ArrayList<>();

	public TableStatus getTableStatus() {
		return tableStatus;
	}

	public TableDescription setTableStatus(TableStatus tableStatus) {
		this.tableStatus = tableStatus;
		return this;
	}

	public Long getTableSizeBytes() {
		return tableSizeBytes;
	}

	public TableDescription setTableSizeBytes(Long tableSizeBytes) {
		this.tableSizeBytes = tableSizeBytes;
		return this;
	}

	public Long getItemCount() {
		return itemCount;
	}

	public TableDescription setItemCount(Long itemCount) {
		this.itemCount = itemCount;
		return this;
	}

	public java.util.Date getCreationDateTime() {
		return creationDateTime;
	}

	public TableDescription setCreationDateTime(java.util.Date creationDateTime) {
		this.creationDateTime = creationDateTime;
		return this;
	}

	public java.util.List<LocalSecondaryIndexDescription> getLocalSecondaryIndexes() {
		return localSecondaryIndexes;
	}

	public TableDescription setLocalSecondaryIndexes(
			java.util.List<LocalSecondaryIndexDescription> localSecondaryIndexes) {
		this.localSecondaryIndexes = localSecondaryIndexes;
		return this;
	}

	public java.util.List<GlobalSecondaryIndexDescription> getGlobalSecondaryIndexes() {
		return globalSecondaryIndexes;
	}

	public TableDescription setGlobalSecondaryIndexes(
			java.util.List<GlobalSecondaryIndexDescription> globalSecondaryIndexes) {
		this.globalSecondaryIndexes = globalSecondaryIndexes;
		return this;
	}
	
	public List<String> getIndexNames() {
		return Stream.concat(
				this.getLocalSecondaryIndexes().stream().map(lsi -> lsi.getIndexName()),
				this.getGlobalSecondaryIndexes().stream().map(gsi -> gsi.getIndexName())
		)
                .collect(Collectors.toList());
	}

}
