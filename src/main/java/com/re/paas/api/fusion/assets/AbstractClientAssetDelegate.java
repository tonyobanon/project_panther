package com.re.paas.api.fusion.assets;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractClientAssetDelegate extends SpiDelegate<ClientAsset> {

	public static final String BASE_PATH = "/assets";
	
	@SecureMethod
	public abstract Path getBasePath();
	
	@SecureMethod
	public abstract CompletableFuture<?> addArtifact(Path path, InputStream content);
	
	@SecureMethod
	public abstract CompletableFuture<?> removeArtifact(Path p);

	public abstract void handler(RoutingContext ctx);
	
}
