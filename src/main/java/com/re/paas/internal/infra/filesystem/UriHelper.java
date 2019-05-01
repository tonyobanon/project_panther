package com.re.paas.internal.infra.filesystem;

import java.net.URI;

import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.internal.Platform;

public class UriHelper {

	private static final String basePath = "/" + Platform.getPlatformPrefix() + "/appdata";

	static URI transform(FileSystemProviderImpl provider, URI uri) {
		return URI.create(provider.getProvider().getScheme() + "://" + transform(provider, uri.getPath()));

	}

	static String transform(FileSystemImpl fs, String first, String... more) {

		StringBuilder uriBuilder = new StringBuilder();

		uriBuilder.append(first + fs.getSeparator());

		if (more != null) {
			for (String path : more) {
				uriBuilder.append(path + fs.getSeparator());
			}
		}

		return transform(fs.provider, normalizeURI(uriBuilder.toString()));
	}

	static String transform(FileSystemProviderImpl provider, String path) {
		if (provider.getProvider().equals(FileSystemProviders.getInternal().provider())) {
			// Do not modify the uri, if the backing provider is system internal
			return path;
		}
		return basePath + "/" + ClassLoaderSecurity.getAppId() + path;
	}

	/**
	 * Remove duplicated slash
	 */
	private static String normalizeURI(String uri) {
		return uri.replace("//", "/");
	}

}
