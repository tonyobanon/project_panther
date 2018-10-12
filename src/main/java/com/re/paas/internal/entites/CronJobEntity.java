package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.condition.IfTrue;
import com.re.paas.api.tasks.CronJob;
import com.re.paas.internal.tasks.ModelTask;

@Cache
@Entity
public class CronJobEntity {

	@Id
	Long id;
	
	String name;
	
	@Index
	Integer interval;

	Integer cronType;
	
	@Serialize(zip=true) ModelTask task;
	
	@Serialize(zip=true) CronJob job;
	
	Boolean isReady;
	
	Integer totalExecutionCount;
	
	Integer maxExecutionCount;
	
	@Index(IfTrue.class)
	boolean isInternal;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public CronJobEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public CronJobEntity setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getInterval() {
		return interval;
	}

	public CronJobEntity setInterval(Integer interval) {
		this.interval = interval;
		return this;
	}

	public Integer getCronType() {
		return cronType;
	}

	public CronJobEntity setCronType(Integer cronType) {
		this.cronType = cronType;
		return this;
	}
	
	public ModelTask getTask() {
		return task;
	}

	public CronJobEntity setTask(ModelTask task) {
		this.task = task;
		return this;
	}

	public CronJob getJob() {
		return job;
	}

	public CronJobEntity setJob(CronJob job) {
		this.job = job;
		return this;
	}

	public Boolean getIsReady() {
		return isReady;
	}

	public CronJobEntity setIsReady(Boolean isReady) {
		this.isReady = isReady;
		return this;
	}

	public Integer getTotalExecutionCount() {
		return totalExecutionCount;
	}

	public CronJobEntity setTotalExecutionCount(Integer totalExecutionCount) {
		this.totalExecutionCount = totalExecutionCount;
		return this;
	}

	public Integer getMaxExecutionCount() {
		return maxExecutionCount;
	}

	public CronJobEntity setMaxExecutionCount(Integer maxExecutionCount) {
		this.maxExecutionCount = maxExecutionCount;
		return this;
	}

	public boolean getIsInternal() {
		return isInternal;
	}

	public CronJobEntity setIsInternal(boolean isInternal) {
		this.isInternal = isInternal;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CronJobEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
