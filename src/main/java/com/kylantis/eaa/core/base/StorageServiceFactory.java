package com.kylantis.eaa.core.base;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.SingleThreadExecutor;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.infra.database.dynamodb.qopt.tools.DynamoDBAdapter;

public class StorageServiceFactory {

	private static final Logger LOG = Logger.get(Logger.class);
	
	private static String redisEndpoint;

	private static String dynamodbEndpoint;
	private static String dynamodbRegion;

	private static String s3Endpoint;
	private static String s3Region;

	private static AWSCredentialsProvider credentialsProvider;

	public static void start() {

		Logger.info("Starting StorageServiceFactory");

		SingleThreadExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		}, Application.getConfigAsInt("storage.factory.refreshInterval"),
				Application.getConfigAsInt("storage.factory.refreshInterval"), TimeUnit.SECONDS);
		refresh();
	}

	public static DynamoDB getDocumentDatabase() {
		return new DynamoDBAdapter(getDatabase());
	}

	public static AmazonS3 getS3Storage() {
		return AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration(s3Endpoint, s3Region))
				.withCredentials(getAWSCredentials()).build();
	}

	public static DynamoDB getDirectDatabase() {
		return new DynamoDB(getDatabase());
	}

	public static AmazonDynamoDBClient getDatabase() {
		return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration(dynamodbEndpoint, dynamodbRegion))
				.withCredentials(getAWSCredentials()).build();
	}

	public static DynamoDBMapper getMapperInstance() {
		AmazonDynamoDB client = getDatabase();
		DynamoDBMapper mapper = new DynamoDBMapper(client, DynamoDBMapperConfig.builder().build());
		return mapper;
	}

	public static String getRedisEndpoint() {
		return redisEndpoint;
	}

	private static AWSCredentialsProvider getAWSCredentials() {
		return credentialsProvider;
	}

	@Todo("Synchronize before updating static fields to avoid dreadlock")
	private static void refresh() {

		// Refresh storage clients, while also avoiding dreadlock

		redisEndpoint = System.getenv("REDIS_ENDPOINT");
		Redis.reset();

		dynamodbEndpoint = System.getenv("DYNAMODB_ENDPOINT");

		dynamodbRegion = System.getenv("DYNAMODB_REGION");

		s3Endpoint = System.getenv("S3_ENDPOINT");

		s3Region = System.getenv("S3_REGION");

		String awsAccessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
		String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

		credentialsProvider = new AWSStaticCredentialsProvider(new AWSCredentials() {

			@Override
			public String getAWSSecretKey() {
				return awsSecretKey;
			}

			@Override
			public String getAWSAccessKeyId() {
				return awsAccessKeyId;
			}
		});

		Logger.info("Redis Endpoint: " + redisEndpoint);
		Logger.info("DynamoDB Endpoint: " + dynamodbEndpoint);
		Logger.info("DynamoDB Region: " + dynamodbRegion);
		Logger.info("S3 Endpoint: " + s3Endpoint);
		Logger.info("S3 Region: " + s3Region);
	}

}
