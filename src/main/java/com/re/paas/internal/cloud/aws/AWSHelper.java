package com.re.paas.internal.cloud.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.internal.cloud.KVPair;
import com.re.paas.internal.clustering.classes.Utils;

public class AWSHelper {

	private static InstanceProfileCredentialsProvider credentialsProvider;
	private static BasicAWSCredentials credentials;

	private static Boolean isIamCredentials;

	public AWSHelper() {
	}

	public static String name(){
		return "AWS";
	}
	
	public static Boolean isValidInstanceType(String instanceType) {
		try {
			InstanceType o = InstanceType.fromValue(instanceType);
			if (o != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static Boolean isValidRegion(String region) {
		try {
			Regions o = Regions.fromName(region);
			if (o != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static String getInstanceMetaData(String category) {

		HttpGet req = new HttpGet(URI.create("http://169.254.169.254/latest/meta-data/" + category));
		// req.addHeader("Content-Type", "application/x-octetstream");

		try {
		
		CloseableHttpResponse resp = HttpClients.createMinimal().execute(req);

		InputStream in = resp.getEntity().getContent();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		while ((c = in.read()) != -1) {
			baos.write(c);
		}

		in.close();
		if (baos.size() > 0) {
			return baos.toString();
		} else {
			return null;
		}
		
		}catch (IOException e) {
			return (String) Exceptions.throwRuntime(e);
		}
	}

	public static JsonObject getInstanceIdentityDocument() throws IOException {

		HttpGet req = new HttpGet(URI.create("http://169.254.169.254/latest/dynamic/instance-identity/document"));
		// req.addHeader("Content-Type", "application/json");

		CloseableHttpResponse resp = HttpClients.createMinimal().execute(req);

		InputStream in = resp.getEntity().getContent();

		return com.re.paas.api.utils.Utils.getJson(in);
	}

	private static AWSCredentials getCredentials() {
		return credentials;
	}

	private static InstanceProfileCredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	public static AmazonEC2 getEC2Client() {
		AmazonEC2 ec2 = null;
		if (isIamCredentials) {
			ec2 = AmazonEC2ClientBuilder.standard().withCredentials(getCredentialsProvider()).build();
		} else {
			ec2 = new AmazonEC2Client(getCredentials());
		}
		return ec2;
	}

	public static AmazonEC2 getEC2Client(String region) {
		AmazonEC2 ec2 = null;
		if (isIamCredentials) {
			ec2 = AmazonEC2ClientBuilder.standard().withCredentials(getCredentialsProvider()).withRegion(region)
					.build();
		} else {
			ec2 = new AmazonEC2Client(getCredentials()).withRegion(Region.getRegion(Regions.fromName(region)));
		}
		return ec2;
	}

	public static String toUserData(Map<String, String> props) {
		StringBuilder txt = new StringBuilder();
		Boolean b = false;

		for (Entry<String, String> e : props.entrySet()) {
			if (b) {
				txt.append("\n");
			}
			txt.append(e.getKey() + ": " + e.getValue());
			if (!b) {
				b = true;
			}
		}

		return txt.toString();
	}

	public static Map<String, String> getUserData() {

		// Retrieve Instance Userdata

		String[] lines = null;
		try {
			lines = Utils.getLines(URI.create("http://169.254.169.254/latest/user-data"), Maps.newHashMap());
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		Map<String, String> props = new HashMap<>(lines.length);

		for (int i = 0; i < lines.length; i++) {

			if (!KVPair.line().matcher(lines[i]).matches()) {
				throw new RuntimeException("Unable to read line at index: " + i + " for the provided instance data.");
			}

			String[] entry = (lines[i]).split(KVPair.separator().pattern());
			props.put(entry[0], entry[1]);
		}

		return props;
	}

	static {
		
		CloudEnvironment env = CloudEnvironment.get();

		String accessKey = env.getInstanceTags().get(AwsTags.AWS_ACCESS_KEY);
		String secretKey = env.getInstanceTags().get(AwsTags.AWS_SECRET_KEY);

		if (accessKey == null || secretKey == null) {
			credentialsProvider = new InstanceProfileCredentialsProvider(true);
			isIamCredentials = true;
		} else {
			credentials = new BasicAWSCredentials(accessKey, secretKey);
		}
	}
}
