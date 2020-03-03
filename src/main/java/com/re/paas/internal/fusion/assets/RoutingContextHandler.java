package com.re.paas.internal.fusion.assets;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.Buffer;
import com.re.paas.api.fusion.RoutingContext;

class RoutingContextHandler {

	// Use Google CDN for caching instead, for fine grained control over cached data
	private static final boolean CACHE_WEB_CONTENT = true;
	private static final Integer DEFAULT_CACHE_MAX_AGE = 259200;

	private static final Map<String, String> mimeTypes = new HashMap<String, String>();
	private static Tika TIKA_INSTANCE = new Tika();

	private static String getMimeType(String uri, byte[] file) {

		String mimeType = mimeTypes.get(uri);

		if (mimeType == null) {
			mimeType = TIKA_INSTANCE.detect(file);
			mimeTypes.put(uri, mimeType);
		}

		return mimeType;
	}

	static void handler(RoutingContext ctx) {

		String uri = ctx.request().path();

		if (CACHE_WEB_CONTENT) {
			// allow proxies to cache the data
			ctx.response().putHeader("Cache-Control", "public, max-age=" + DEFAULT_CACHE_MAX_AGE);
		} else {
			ctx.response().putHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		}

		// Improve security
		ctx.response()

				// prevents Internet Explorer from MIME -
				// sniffing a
				// response away from the declared content-type
				.putHeader("X-Content-Type-Options", "nosniff")
				// Strict HTTPS (for about ~6Months)
				.putHeader("Strict-Transport-Security", "max-age=" + 15768000)
				// IE8+ do not allow opening of attachments in
				// the context
				// of this resource
				.putHeader("X-Download-Options", "noopen")
				// enable XSS for IE
				.putHeader("X-XSS-Protection", "1; mode=block")
				// deny frames
				.putHeader("X-FRAME-OPTIONS", "DENY");

		try {

			Path p = ClientAssetDelegate.getBasePath0().resolve(uri);

			byte[] bytes = IOUtils.toByteArray(p.toUri());

			ctx.response().putHeader("content-type", getMimeType(uri, bytes)).write(Buffer.buffer(bytes));

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}
}
