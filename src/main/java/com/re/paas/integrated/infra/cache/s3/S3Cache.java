package com.re.paas.integrated.infra.cache.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.cache.CacheException;

public class S3Cache extends ObjectStorageCache {

	final Bucket bucket;
	private final BasicAWSCredentials awsCreds;
	
	final S3Cache expiryCache;

	S3Cache(Bucket bucket, BasicAWSCredentials awsCreds, S3Cache expiryCache) {

		this.bucket = bucket;
		this.awsCreds = awsCreds;
		
		this.expiryCache = expiryCache;
	}

	private AmazonS3 getClient() {
		return createS3Client(this.awsCreds);
	}

	private AmazonS3 createS3Client(BasicAWSCredentials awsCreds) {
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	}
	
	String getBucket() {
		return this.bucket.getName();
	}
	
	@Override
	protected ObjectStorageCache getExpiryCache() {
		return expiryCache;
	}

	@Override
	protected CompletableFuture<ByteBuffer> doGet(String key) {

		S3Object o = getClient().getObject(this.bucket.getName(), key);

		S3ObjectInputStream in = o.getObjectContent();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			in.transferTo(out);
		} catch (IOException e) {
			Exceptions.throwRuntime(new CacheException(e));
		}

		ByteBuffer result = ByteBuffer.wrap(out.toByteArray());

		return CompletableFuture.completedFuture(result);
	}

	@Override
	protected CompletableFuture<?> doSet(String key, ByteBuffer value) {

		return CompletableFuture.runAsync(() -> {

			try {

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentType("application/octet-stream");
				metadata.setContentLength(value.position());

				ByteArrayInputStream in = new ByteArrayInputStream(value.array());

				getClient().putObject(this.bucket.getName(), key, in, metadata);

				in.close();

			} catch (AmazonServiceException | IOException e) {
				Exceptions.throwRuntime(new CacheException(e));
			}
		});
	}

	@Override
	protected CompletableFuture<?> doDelete(String... keys) {

		List<CompletableFuture<?>> futures = new ArrayList<>(keys.length);

		for (String key : keys) {
			futures.add(doDelete(key));
		}

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
	}

	private CompletableFuture<?> doDelete(String key) {
		return CompletableFuture.runAsync(() -> {
			getClient().deleteObject(this.bucket.getName(), key);
		});

	}
}