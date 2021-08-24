package com.re.paas.internal.infra.cache.s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.Lists;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExternalContext;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.ParameterizedInvokable;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.tasks.TaskModel;
import com.re.paas.api.utils.Dates;

public class CacheEvicter {

	private S3CacheFactory factory;
	private ScheduledExecutorService executor;

	private static Integer queryLimitSize = 1000000;
	private static Integer perNodeBatchSize = 100000;
	private static Integer perThreadBatchSize = 1000;

	CacheEvicter(S3CacheFactory factory) {
		this.factory = factory;
	}

	void start() {
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(this::reap, 60, 60, TimeUnit.SECONDS);
	}

	void stop() {
		executor.shutdown();
	}

	private void reap() {

		List<String> bucketList = S3CacheFactory.bucketList;

		if (bucketList.isEmpty()) {
			return;
		}

		AmazonS3 s3 = factory.createClient();

		EvictionContext ctx = buildEvictionContext();

		for (String b : factory.bucketList()) {

			String bucketName = b + S3CacheFactory.expBucketSuffix;
			AtomicInteger totalCount = new AtomicInteger();

			ctx.begin(bucketName);

			ListObjectsV2Request listRequest = new ListObjectsV2Request();
			listRequest.setBucketName(bucketName);
			listRequest.setMaxKeys(queryLimitSize);

			boolean done = false;
			while (!done) {

				ListObjectsV2Result listObjResponse = s3.listObjectsV2(listRequest);

				for (S3ObjectSummary object : listObjResponse.getObjectSummaries()) {
					totalCount.addAndGet(1);
					ctx.addKey(object.getKey());
				}

				if (listObjResponse.getContinuationToken() == null) {
					done = true;
				}

				listRequest.setContinuationToken(listObjResponse.getContinuationToken());
			}

			Integer deletedCount = ctx.end();

			Logger.get(CacheEvicter.class)
					.info("Evicted " + deletedCount + "/" + totalCount + " in  bucket: " + bucketName);
		}
	}

	private static EvictionContext buildEvictionContext() {

		ParameterizedInvokable<EvictionSpec, Integer> invokable = spec -> {

			S3CacheFactory factory = (S3CacheFactory) CacheAdapter.getDelegate().getCacheFactory();
			AmazonS3 client = factory.createClient();

			// Bucket bucket = S3CacheFactory.getBucket(client, spec.getBucket());

			List<List<String>> subLists = Lists.partition(spec.getKeys(), perThreadBatchSize);

			Date now = Dates.now();

			AtomicInteger deletedCount = new AtomicInteger(0);

			for (int i = 0; i < subLists.size(); i++) {

				List<String> l = subLists.get(i);

				for (String key : l) {

					String dateStr = null;

					try {
						dateStr = new String(
								client.getObject(spec.getBucket(), key).getObjectContent().readAllBytes());
					} catch (SdkClientException | IOException e) {
						Exceptions.throwRuntime(e);
					}

					if (Dates.toDate(dateStr).after(now)) {
						client.deleteObject(spec.getBucket(), key);
						deletedCount.addAndGet(1);
					}
				}
			}

			return deletedCount.get();
		};

		List<CompletableFuture<?>> futures = new ArrayList<>();
		ObjectWrapper<AtomicInteger> deletedCount = new ObjectWrapper<>();

		
		EvictionContext ctx = new EvictionContext(perNodeBatchSize, spec -> {

			ParameterizedExecutable<EvictionSpec, Integer> i = ExecutorFactory.get().buildFunction(
					new ObjectWrapper<ClassLoader>(ClassLoaders.getClassLoader()), invokable, spec,
					new ExternalContext(AppProvisioner.DEFAULT_APP_ID, false, Affinity.ANY)
			);

			CompletableFuture<?> f = TaskModel.getDelegate().execute(i)
					.thenAccept(delta -> {
				deletedCount.get().addAndGet(delta);
			});

			futures.add(f);
			
		}).onStart(() -> {

			futures.clear();
			deletedCount.set(new AtomicInteger());

		}).onEnd(() -> {
			
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
			return deletedCount.get().get();
		});

		return ctx;
	}

	private static class EvictionContext {

		private final int maxKeySize;
		private final Consumer<EvictionSpec> keysConsumer;

		private String bucket;
		private List<String> keys;

		private Runnable onStart;
		private Supplier<Integer> onEnd;

		public EvictionContext(Integer maxKeySize, Consumer<EvictionSpec> keysConsumer) {
			this.maxKeySize = maxKeySize;
			this.keysConsumer = keysConsumer;
		}

		void begin(String bucket) {
			this.bucket = bucket;
			this.keys = new ArrayList<>(maxKeySize);

			this.onStart.run();
		}

		void addKey(String key) {

			this.keys.add(key);

			if (keys.size() >= maxKeySize) {
				flushKeys();
				this.keys = new ArrayList<>(maxKeySize);
			}
		}

		private void flushKeys() {

			List<String> l = new ArrayList<>(maxKeySize);
			Collections.copy(this.keys, l);

			keysConsumer.accept(new EvictionSpec(this.bucket, l));
		}

		Integer end() {
			if (!keys.isEmpty()) {
				flushKeys();
			}
			return this.onEnd.get();
		}

		EvictionContext onStart(Runnable onStart) {
			this.onStart = onStart;
			return this;
		}

		EvictionContext onEnd(Supplier<Integer> onEnd) {
			this.onEnd = onEnd;
			return this;
		}
	}

	public static class EvictionSpec {

		private final String bucket;
		private final List<String> keys;

		public EvictionSpec(String bucket, List<String> keys) {
			super();
			this.bucket = bucket;
			this.keys = keys;
		}

		public String getBucket() {
			return bucket;
		}

		public List<String> getKeys() {
			return keys;
		}
	}

}
