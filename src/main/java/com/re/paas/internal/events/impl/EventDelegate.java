package com.re.paas.internal.events.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.events.EventError;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.compute.Scheduler;

public class EventDelegate extends AbstractEventDelegate {

	private static Map<String, Map<Method, BiConsumer<Boolean, BaseEvent>>> listeners = Maps.newHashMap();
	private static Map<String, List<Consumer<BaseEvent>>> volatileListeners = Maps.newHashMap();

	@Override
	public DelegateInitResult init() {
		Consumer<Class<EventListener>> consumer = (c) -> {
			registerListener(c);
		};
		forEach(consumer);
		return DelegateInitResult.SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer) {

		String eventClass = eventType.getName();

		if (!volatileListeners.containsKey(eventClass)) {
			volatileListeners.put(eventClass, new ArrayList<>());
		}

		volatileListeners.get(eventClass).add((Consumer<BaseEvent>) consumer);
	}

	@Override
	protected void add(List<Class<EventListener>> classes) {
		classes.forEach(c -> {
			registerListener(c);
		});
	}

	@Override
	protected List<Class<EventListener>> remove(List<Class<EventListener>> classes) {
		classes.forEach(c -> {
			unregisterListener(c);
		});
		return Collections.emptyList();
	}

	private static void unregisterListener(Class<?> c) {

		for (Method m : c.getMethods()) {

			Subscribe s = m.getAnnotation(Subscribe.class);

			if (s == null) {
				return;
			}

			if (BaseEvent.class.isAssignableFrom(m.getParameterTypes()[0])
					&& !(m.getParameterTypes()[0]).getName().equals(BaseEvent.class.getName())) {

				String eventClass = (m.getParameterTypes()[0]).getName();
				listeners.get(eventClass).remove(m);
			}
		}

	}

	private static void registerListener(Class<?> c) {

		Object o = ClassUtils.createInstance(c);

		for (Method m : c.getMethods()) {

			Subscribe s = m.getAnnotation(Subscribe.class);

			if (s == null) {
				return;
			}

			if (BaseEvent.class.isAssignableFrom(m.getParameterTypes()[0])
					&& !(m.getParameterTypes()[0]).getName().equals(BaseEvent.class.getName())) {

				String eventClass = (m.getParameterTypes()[0]).getName();

				if (!listeners.containsKey(eventClass)) {
					listeners.put(eventClass, new HashMap<>());
				}

				listeners.get(eventClass).put(m, (isAsync, evt) -> {

					if (isAsync && !s.allowAsyncEvent()) {
						Exceptions.throwRuntime(
								PlatformException.get(EventError.ASYNC_EVENTS_ARE_DISABLED, m.getName(), c.getName()));
					}

					try {
						m.invoke(o, evt);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						Exceptions.throwRuntime(e);
					}
				});
			}
		}

	}

	public void dispatch(BaseEvent evt) {
		dispatch(evt, true);
	}

	public void dispatch(BaseEvent evt, boolean isAsync) {

		Runnable r = () -> {

			if (listeners.get(evt.getClass().getName()) != null) {
				listeners.get(evt.getClass().getName()).values().forEach(o -> {
					o.accept(isAsync, evt);
				});
			}

			List<Consumer<BaseEvent>> vL = volatileListeners.remove(evt.getClass().getName());

			if (vL != null) {
				vL.forEach(c -> {
					c.accept(evt);
				});
			}
		};

		if (isAsync) {
			Scheduler.now(() -> {
				r.run();
			});
		} else {
			r.run();
		}
	}

}
