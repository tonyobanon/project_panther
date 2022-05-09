package com.re.paas.internal.events.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.events.EventError;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClassUtil;

public class EventDelegate extends AbstractEventDelegate {

	private final static ExecutorService executorService = Executors.newWorkStealingPool();
	
	private static Map<String, Map<String, Subscription>> subscribers = Maps.newHashMap();
	private static Map<String, List<Consumer<BaseEvent>>> volatileSubscribers = Maps.newHashMap();

	@Override
	public DelegateInitResult init() {

		addResources(this::add);

		return DelegateInitResult.SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer) {

		String typeString = ClassUtils.asString(eventType);

		if (!volatileSubscribers.containsKey(typeString)) {
			volatileSubscribers.put(typeString, new ArrayList<>());
		}

		volatileSubscribers.get(typeString).add((Consumer<BaseEvent>) consumer);
	}

	private static boolean isEventClass(Class<?> eventType) {
		String typeString = ClassUtils.asString(eventType);

		return BaseEvent.class.isAssignableFrom(eventType) && !typeString.equals(ClassUtils.asString(BaseEvent.class));
	}

	protected ResourceStatus remove(Class<EventListener> c) {

		boolean changed = false;

		for (Method m : c.getMethods()) {

			Subscribe s = m.getAnnotation(Subscribe.class);

			if (s == null || m.getParameterTypes().length != 1) {
				continue;
			}

			Class<?> eventType = m.getParameterTypes()[0];

			if (isEventClass(eventType)) {
				changed = true;

				String typeString = ClassUtils.asString(eventType);

				subscribers.get(typeString).remove(ClassUtils.asString(m));
			}
		}

		return changed ? ResourceStatus.UPDATED : ResourceStatus.NOT_UPDATED;
	}

	protected ResourceStatus add(Class<EventListener> c) {

		Object o = null;
		boolean changed = false;

		for (Method m : c.getMethods()) {

			Subscribe s = m.getAnnotation(Subscribe.class);

			if (s == null || m.getParameterTypes().length != 1) {
				continue;
			}

			Class<?> eventType = m.getParameterTypes()[0];

			if (isEventClass(eventType)) {

				if (!changed) {
					o = ClassUtil.createInstance(c);
					changed = true;
				}

				String typeString = ClassUtils.asString(eventType);

				if (!subscribers.containsKey(typeString)) {
					subscribers.put(typeString, new HashMap<>());
				}

				subscribers.get(typeString).put(ClassUtils.asString(m),
						new Subscription(o, m, s.allowAsyncEvents(), s.affinity()));
			}
		}

		return changed ? ResourceStatus.UPDATED : ResourceStatus.NOT_UPDATED;
	}

	@Override
	public void dispatch(BaseEvent evt) {
		dispatch(evt, true);
	}

	@Override
	public void dispatch(BaseEvent evt, boolean isAsync) {

		String typeString = ClassUtils.asString(evt.getClass());

		List<Consumer<BaseEvent>> a = volatileSubscribers.remove(typeString);
		Map<String, Subscription> b = subscribers.get(typeString);

		boolean hasVolatileSubscribers = a != null && !a.isEmpty();
		boolean hasSubscribers = b != null && !b.isEmpty();

		if ((!hasVolatileSubscribers) && !hasSubscribers) {
			return;
		}

		Runnable r = () -> {

			if (hasVolatileSubscribers) {
				a.forEach(c -> {
					c.accept(evt);
				});
			}

			if (hasSubscribers) {
				b.values().forEach(subscription -> {

					ParameterizedExecutable<BaseEvent, Void> fn =  ExecutorFactory.get().buildFunction((event) -> {

						if (isAsync && !subscription.isAllowAsyncEvents()) {
							Exceptions.throwRuntime(PlatformException.get(EventError.ASYNC_EVENTS_ARE_DISABLED,
									ClassUtils.asString(subscription.getMethod())));
						}

						try {
							subscription.getMethod().invoke(subscription.getInstance(), event);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
							Exceptions.throwRuntime(ex);
						}

						return null;
					}, evt, subscription.getAffinity());
					
					ClusteringServices.get().execute(fn);
				});
			}
		};

		if (isAsync) {
			executorService.execute(r);
		} else {
			r.run();
		}
	}

}
