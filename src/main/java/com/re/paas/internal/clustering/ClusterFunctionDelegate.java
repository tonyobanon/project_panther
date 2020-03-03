package com.re.paas.internal.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractClusterFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;

@DelegateSpec(dependencies = { SpiType.NODE_ROLE, SpiType.FUNCTION })
public class ClusterFunctionDelegate extends AbstractClusterFunctionDelegate {

	private static Map<Short, AbstractClusterFunction<Object, Object>> functions = Collections
			.synchronizedMap(new HashMap<Short, AbstractClusterFunction<Object, Object>>(32767));

	@Override
	public DelegateInitResult init() {
		return forEach(ClusterFunctionDelegate::processClusterFunction);
	}

	@Override
	protected void add(List<Class<AbstractClusterFunction<Object, Object>>> classes) {
		classes.forEach(ClusterFunctionDelegate::processClusterFunction);
	}
	
	
	
	@Override
	protected Collection<?> getResourceObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	private static void processClusterFunction(Class<AbstractClusterFunction<Object, Object>> c) {

		AbstractClusterFunction<Object, Object> o = com.re.paas.internal.classes.ClassUtil.createInstance(c);

		boolean b = false;

		for (AbstractRole role : AbstractRole.get().values()) {
			b = o.role().equals(role.getClass());
			if (b) {
				break;
			}
		}
		
		Short functionId = Function.getId(o.id());

		if (b) {

			if (functions.containsKey(functionId)) {
				Logger.get().info("Adding Cluster Function: " + o.id());
				functions.put(functionId, o);
			}

		} else {
			if (functions.containsKey(functionId)) {
				Logger.get().info("Removing Cluster Function: " + o.id());
				functions.remove(functionId);
			}
		}
	}
	
	@Override
	public AbstractClusterFunction<Object, Object> getClusterFunction(Function function) {
		return functions.get(Function.getId(function));
	}
}
