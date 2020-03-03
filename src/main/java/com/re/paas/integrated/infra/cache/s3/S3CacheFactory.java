package com.re.paas.integrated.infra.cache.s3;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.re.paas.api.Platform;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class S3CacheFactory implements CacheFactory<String, Object> {

	private static final String bucketPrefix = Platform.getPlatformPrefix();
	static final String expBucketSuffix = "_$exp";

	static final List<String> bucketList = new ArrayList<>();

	private final S3Adapter adapter;
	private final String name;

	private final BasicAWSCredentials awsCreds;
	private final String region;
	private final String endpoint;

	S3CacheFactory(S3Adapter adapter, String name, BasicAWSCredentials awsCreds, String region, String endpoint) {
		this.adapter = adapter;
		this.name = name;
		this.awsCreds = awsCreds;
		this.region = region;
		this.endpoint = endpoint;
	}

	@Override
	public CacheAdapter getAdapter() {
		return this.adapter;
	}

	@Override
	public String getName() {
		return this.name;
	}

	AmazonS3 createClient() {

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.endpoint, this.region))

				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		return s3Client;
	}

	@Override
	public Cache<String, Object> get(String bucketName) {

		bucketName = bucketPrefix + bucketName;

		if (!bucketList.contains(bucketName)) {
			bucketList.add(bucketName);
		}

		AmazonS3 s3Client = createClient();

		Bucket mainBucket = null;
		Bucket expBucket = null;

		if (s3Client.doesBucketExistV2(bucketName)) {

			// Get existing bucket
			mainBucket = getBucket(s3Client, bucketName);
			expBucket = getBucket(s3Client, bucketName + expBucketSuffix);

			if (expBucket == null) {
				Exceptions.throwRuntime("Could not find bucket: " + bucketName + expBucketSuffix);
			}

		} else {

			// Create bucket
			try {

				mainBucket = createBucket(s3Client, bucketName);
				expBucket = createBucket(s3Client, bucketName + expBucketSuffix);

			} catch (AmazonS3Exception e) {
				Exceptions.throwRuntime(e);
			}
		}

		return new S3Cache(mainBucket, awsCreds, new S3Cache(expBucket, awsCreds, null));
	}

	private static Bucket createBucket(AmazonS3 s3Client, String bucket_name) {
		Bucket b = s3Client.createBucket(new CreateBucketRequest(bucket_name));
		return b;
	}

	private static Bucket getBucket(AmazonS3 s3Client, String bucket_name) {
		Bucket bucket = null;
		List<Bucket> buckets = s3Client.listBuckets();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucket_name)) {
				bucket = b;
			}
		}
		return bucket;
	}

	@Override
	public Boolean supportsAutoExpiry() {
		return false;
	}

	@Override
	public List<String> bucketList() {
		return bucketList;
	}

	@BlockerTodo
	@Override
	public void shutdown() {
		
		CacheEvicter evicter = ((S3Adapter)getAdapter()).getCacheEvicter();
		
		if (evicter != null) {
			evicter.stop();
		}

		AmazonS3 s3Client = createClient();

		for (String bucketName : bucketList) {

			s3Client.deleteBucket(bucketName);
			s3Client.deleteBucket(bucketName + expBucketSuffix);
		}
		
		S3CacheFactory.bucketList.clear();
	}

}
