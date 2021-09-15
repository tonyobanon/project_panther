package com.re.paas.internal.clustering;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractClusterFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;

@DelegateSpec(dependencies = { SpiType.NODE_ROLE, SpiType.FUNCTION })
public class ClusterFunctionDelegate extends AbstractClusterFunctionDelegate {

	private static Map<Short, AbstractClusterFunction<Object, Object>> functions = Collections
			.synchronizedMap(new HashMap<Short, AbstractClusterFunction<Object, Object>>(32767));

	@Override
	public DelegateInitResult init() {
		addResources(this::add);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected ResourceStatus add(Class<AbstractClusterFunction<Object, Object>> c) {
		
		AbstractClusterFunction<Object, Object> f = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		
		if (!hasValidRole(f)) {
			return ResourceStatus.ERROR.setMessage("Cluster function: " + ClassUtils.asString(c) + " does not have a valid role");
		}
		

		Short functionId = Function.getId(f.id());
		
		if (functions.containsKey(functionId)) {
			return ResourceStatus.ERROR.setMessage("Cluster function: " + f.id() + " already exists");
		}
		
		functions.put(functionId, f);
		
		return ResourceStatus.UPDATED;
	}
	
	@Override
	protected ResourceStatus remove(Class<AbstractClusterFunction<Object, Object>> c) {
		
		AbstractClusterFunction<Object, Object> f = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		
		Short functionId = Function.getId(f.id());
		
		if (!functions.containsKey(functionId)) {
			return ResourceStatus.NOT_UPDATED.setMessage("Cluster function: " + f.id() + " does not exist");
		}
		
		functions.remove(functionId);
		
		return ResourceStatus.UPDATED;
	}
	
	
	private Boolean hasValidRole(AbstractClusterFunction<Object, Object> f) {
		boolean b = false;

		for (AbstractRole role : AbstractRole.get().values()) {
			b = f.role().equals(role.getClass());
			if (b) {
				break;
			}
		}
		return b;
	}
	
	@Override
	public AbstractClusterFunction<Object, Object> getClusterFunction(Function function) {
		return functions.get(Function.getId(function));
	}
}
