package com.re.paas.internal.cloud.aws;

import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;

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
		return new JsonObject().put("name", name).put("fingerprint", keyFingerprint).put("material", keyMaterial)
				.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {

		JsonObject props = new JsonObject(stringVal);

		return new AwsInstanceCredential(instanceId, props.getString("name"), props.getString("fingerprint"),
				props.getString("material"));
	}

}
