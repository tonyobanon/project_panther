package com.re.paas.internal.cloud.azure;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.CloudException;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.OperatingSystemTypes;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.rest.LogLevel;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.classes.OsPlatform;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.clustering.classes.Utils;
import com.re.paas.internal.errors.ClusteringError;

public class AzureHelper {

	private static final AzureTokenCredentials credentials;

	private static final String resourceGroup;
	private static final String subscription;

	public AzureHelper() {
	}

	public static String name() {
		return "Azure";
	}

	public static String dnsNameSuffix() {
		return "cloudapp.azure.com";
	}

	public static String getResourceGroup() {
		return resourceGroup;
	}

	public static String getSubscription() {
		return subscription;
	}

	private static ApplicationTokenCredentials getCredentials() {
		return (ApplicationTokenCredentials) credentials;
	}

	public static Azure getAzure() {
		try {
			return Azure.configure().withLogLevel(LogLevel.NONE).authenticate(getCredentials())
					.withDefaultSubscription();
		} catch (CloudException e) {
			return (Azure) Exceptions.throwRuntime(PlatformException
					.get(ClusteringError.ERROR_OCCURED_WHILE_MAKING_SERVICE_CALL, "Azure", e.getMessage().toString()));
		} catch (IOException e) {
			return (Azure) Exceptions.throwRuntime(e);
		}
	}

	public static Boolean isValidVMSizeType(String vmSizeType) {
		try {
			VirtualMachineSizeTypes.class.getField(vmSizeType);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	public static OperatingSystemTypes toOsType(OsPlatform platform) {
		OperatingSystemTypes type = null;
		switch (platform) {
		case LINUX:
			type = OperatingSystemTypes.LINUX;
			break;
		case WINDOWS:
			type = OperatingSystemTypes.WINDOWS;
			break;
		case MAC:
			break;
		case SOLARIS:
			break;
		}
		return type;
	}

	public static Boolean isValidRegion(String region) {
		try {
			Region o = Region.findByLabelOrName(region);
			if (o != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static final String defaultAPIVersion() {
		return "2017-12-01";
	}

	public static Map<String, String> getInstanceTags() {

		// Retrieve Instance Tags

		String tags = Utils.getString(
				URI.create("http://169.254.169.254/metadata/instance/compute/tags?api-version=" + defaultAPIVersion()),
				FluentHashMap.forNameMap().with("Metadata", "true"));

		Map<String, String> tagsMap = Json.getGson().fromJson(tags, new TypeToken<Map<String, String>>() {
		}.getType());

		return tagsMap;
	}

	static {
		
		CloudEnvironment env = CloudEnvironment.get();

		// Setup Credentials

		String clientId = env.getInstanceTags().get(AzureTags.AZURE_CLIENT);
		String key = env.getInstanceTags().get(AzureTags.AZURE_KEY);

		subscription = env.getInstanceTags().get(AzureTags.AZURE_SUBSCRIPTION);

		credentials = new ApplicationTokenCredentials(clientId, null, key, AzureEnvironment.AZURE)
				.withDefaultSubscriptionId(subscription);

		// Set Resource Group
		resourceGroup = env.getInstanceTags().get(AzureTags.AZURE_RESOURCE_GROUP);
	}

}
