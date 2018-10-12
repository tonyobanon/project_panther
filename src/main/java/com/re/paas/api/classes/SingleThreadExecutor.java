package com.re.paas.api.classes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SingleThreadExecutor {

	private static ScheduledExecutorService executorService;

	public static void execute(Runnable runnable){
		executorService.execute(runnable);
	}
	
	public static void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
		executorService.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

	static {
		executorService = Executors.newScheduledThreadPool(2, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(false);
				return t;
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				executorService.shutdown();
		}));
	}

}
