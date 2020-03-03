package com.re.paas.internal.listable;

import com.re.paas.api.listable.ListableIndex;
import com.re.paas.integrated.listable.ActivityStreamIndex;
import com.re.paas.integrated.listable.AdminApplicationsIndex;
import com.re.paas.integrated.listable.BaseUsersIndex;
import com.re.paas.integrated.listable.CronJobIndex;
import com.re.paas.integrated.listable.UserApplicationsIndex;

public class IndexRepository {

	public static final String USER = ListableIndex.getDelegate().toString(new BaseUsersIndex());
	public static final String ACTIVITY_STREAM = ListableIndex.getDelegate().toString(new ActivityStreamIndex());
	public static final String ADMIN_APPLICATION = ListableIndex.getDelegate().toString(new AdminApplicationsIndex());
	public static final String USER_APPLICATION = ListableIndex.getDelegate().toString(new UserApplicationsIndex());
	public static final String CRON_JOB = ListableIndex.getDelegate().toString(new CronJobIndex());
	
}
