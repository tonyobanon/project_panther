package com.re.paas.internal.cloud.aws;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.cloud.InstanceCredential;

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
		JsonObject o = Json.createObjectBuilder().add("name", name).add("fingerprint", keyFingerprint)
				.add("material", keyMaterial).build();
		return o.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		JsonObject credentialObj = Json.createReader(new StringReader(stringVal)).readObject();
		return new AwsInstanceCredential(instanceId, credentialObj.getString("name"), credentialObj.getString("fingerprint"),
				credentialObj.getString("material"));
	}

}
