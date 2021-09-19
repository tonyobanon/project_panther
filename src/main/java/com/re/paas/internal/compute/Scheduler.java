package com.re.paas.internal.compute;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.utils.Utils;

/**
 * This class may be used to perform certain routine tasks.
 **/

@BlockerTodo("Stop using Executors.newSingleThreadScheduledExecutor()")
public class Scheduler {

	private static ScheduledExecutorService defaultExecutor = Executors.newSingleThreadScheduledExecutor();

	private static Map<String, Runnable> dailyTasks = Collections.synchronizedMap(new HashMap<String, Runnable>());

	public static void startDailyCron() {

		// Relative to System Time
		int totalDelay = 0;

		int PSToffset = TimeZone.getTimeZone("PST").getRawOffset();
		int Zoneoffset = TimeZone.getDefault().getRawOffset();

		int offset = PSToffset - Zoneoffset;

		Calendar now = GregorianCalendar.getInstance();
		now.add(Calendar.MILLISECOND, offset);

		// System.out.println(now.getTime().toString());

		// Time until the next midnight
		int tillMidnight = 24 - now.get(Calendar.HOUR_OF_DAY);

		// System.out.println(tillMidnight);

		totalDelay = tillMidnight - offset;

		defaultExecutor.scheduleAtFixedRate(() -> {
			for (Runnable o : dailyTasks.values()) {
				o.run();
			}
		}, (totalDelay * 3600000)/* Add 10 minutes */ + 600000 + offset, 86400000, TimeUnit.MILLISECONDS);
	}

	/**
	 * This executes a defined task on a daily basis using the PST TimeZone.
	 */
	public static void scheduleDaily(Runnable task) {
		dailyTasks.put(Utils.newRandom(), task);
	}

	/**
	 * This executes a defined task after the specified delay
	 */
	@BlockerTodo("We know that the JVM on which this request was submitted can go out service at any time."
			+ "Hence, since there may be multiple nodes in our cluster, we want to have the ability to keep track of tasks "
			+ "and transfer to other nodes on an as needed basis")

	public static void schedule(Runnable task, Long delay, TimeUnit unit) {
		schedule(null, task, delay, unit);;
	}
	
	public static void main(String[] args) {
      
		//GlobalConfigurationBuilder.defaultClusteredBuilder().
		
    }

	@BlockerTodo
	public static void schedule(String taskId, Runnable task, Long delay, TimeUnit unit) {

		ScheduledFuture<?> future = defaultExecutor.schedule(() -> {

			task.run();
			
			if(taskId != null) {
				unschedule(taskId);
			}

		}, delay, unit);

		if(taskId != null) {

			// Add future to data grid. Is this even valid?
		}
		
	}

	@BlockerTodo
	public static void unschedule(String taskId) {

		// Remove future from data grid. Is this even valid?
	}

	/**
	 * This executes a defined task after the specified delay
	 */
	public static void now(Runnable task) {
		defaultExecutor.schedule(task, 1, TimeUnit.NANOSECONDS);
	}

	public static ScheduledExecutorService getDefaultExecutor() {
		return defaultExecutor;
	}

}
