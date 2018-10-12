package com.re.paas.internal.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.internal.classes.CronInterval;
import com.re.paas.internal.classes.CronType;
import com.re.paas.internal.classes.spec.BaseCronJobSpec;
import com.re.paas.internal.entites.CronJobEntity;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.functionalities.TaskFunctionalities;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;

public class CronJobList extends Listable<BaseCronJobSpec>{

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.CRON_JOB;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return RoleModel.isAccessAllowed(BaseUserModel.getRole(userId), TaskFunctionalities.VIEW_JOBS);
	}

	@Override
	public Class<CronJobEntity> entityType() {
		return CronJobEntity.class;
	}
	
	@Override
	public boolean searchable() {
		return false;
	}

	@Override
	public Map<String, BaseCronJobSpec> getAll(List<String> keys) {
		
		Map<String, BaseCronJobSpec> result = new FluentHashMap<>();
		
		keys.forEach(k -> {
			
			Long id = Long.parseLong(k);
			
			CronJobEntity e = ofy().load().type(CronJobEntity.class).id(id).safe();
			
			BaseCronJobSpec spec = new BaseCronJobSpec()
					.setId(e.getId())
					.setName(e.getName())
					.setInterval(CronInterval.from(e.getInterval()))
					.setCronType(CronType.from(e.getCronType()))
					.setIsReady(e.getIsReady())
					.setTotalExecutionCount(e.getTotalExecutionCount())
					.setMaxExecutionCount(e.getMaxExecutionCount())
					.setIsInternal(e.getIsInternal())
					.setDateCreated(e.getDateCreated());
			
			result.put(k, spec);
		});
		
		return result;
	}

}
