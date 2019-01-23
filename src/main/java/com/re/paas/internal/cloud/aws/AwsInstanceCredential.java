package com.re.paas.internal.cloud.aws;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;
import com.re.paas.internal.classes.Json;

public class AwsInstanceCredential extends InstanceCredential {

	private final String name;
	private final String keyFingerprint;
	private final String keyMaterial;

	@Override
	public Class<? extends CloudEnvironment> provider() {
		return AWSEnvironment.class;
	}

	public AwsInstanceCredential(String instanceId, String name, String keyFingerprint, String keyMaterial) {
		super(instanceId);
		this.name = name;
		this.keyFingerprint = keyFingerprint;
		this.keyMaterial = keyMaterial;
	}

	public String getName() {
		return name;
	}

	/**
	 * The SHA-1 digest of the DER encoded private key.
	 */
	public String getKeyFingerprint() {
		return keyFingerprint;
	}

	/**
	 * An unencrypted PEM encoded RSA private key
	 */
	public String getKeyMaterial() {
		return keyMaterial;
	}

	@Override
	public final String toString() {
		
		Map<String, String> props = new HashMap<>();
		
		props.put("name", name);
		props.put("fingerprint", keyFingerprint);
		props.put("material", keyMaterial);
		
		return Json.fromMap(props).toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		
		Map<String, String> props = Json.toMap(stringVal);
		
		return new AwsInstanceCredential(instanceId, props.get("name"), props.get("fingerprint"),
				props.get("material"));
	}

}
