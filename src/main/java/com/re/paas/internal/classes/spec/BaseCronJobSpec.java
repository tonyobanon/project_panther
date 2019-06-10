package com.re.paas.internal.classes.spec;

import java.util.Date;

import com.re.paas.internal.classes.TaskInterval;
import com.re.paas.internal.classes.CronType;

public class BaseCronJobSpec {

	Long id;
	
	String name;
	
	TaskInterval interval;

	CronType cronType;
	
	Boolean isReady;
	
	Integer totalExecutionCount;
	
	Integer maxExecutionCount;
	
	boolean isInternal;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public BaseCronJobSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseCronJobSpec setName(String name) {
		this.name = name;
		return this;
	}

	public TaskInterval getInterval() {
		return interval;
	}

	public BaseCronJobSpec setInterval(TaskInterval interval) {
		this.interval = interval;
		return this;
	}

	public CronType getCronType() {
		return cronType;
	}

	public BaseCronJobSpec setCronType(CronType cronType) {
		this.cronType = cronType;
		return this;
	}

	public Boolean getIsReady() {
		return isReady;
	}

	public BaseCronJobSpec setIsReady(Boolean isReady) {
		this.isReady = isReady;
		return this;
	}

	public Integer getTotalExecutionCount() {
		return totalExecutionCount;
	}

	public BaseCronJobSpec setTotalExecutionCount(Integer totalExecutionCount) {
		this.totalExecutionCount = totalExecutionCount;
		return this;
	}

	public Integer getMaxExecutionCount() {
		return maxExecutionCount;
	}

	public BaseCronJobSpec setMaxExecutionCount(Integer maxExecutionCount) {
		this.maxExecutionCount = maxExecutionCount;
		return this;
	}

	public boolean getIsInternal() {
		return isInternal;
	}

	public BaseCronJobSpec setIsInternal(boolean isInternal) {
		this.isInternal = isInternal;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BaseCronJobSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
