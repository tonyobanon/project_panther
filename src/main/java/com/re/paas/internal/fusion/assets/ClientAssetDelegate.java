package com.re.paas.internal.fusion.assets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.assets.AbstractClientAssetDelegate;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

@DelegateSpec(dependencies = { SpiType.FILESYSTEM_ADAPTER })

public class ClientAssetDelegate extends AbstractClientAssetDelegate {

	private static final Path ASSETS_BASE_DIR = FileSystemProviders.getResourcePath().resolve("web_static");

	@Override
	public DelegateInitResult init() {
		return DelegateInitResult.SUCCESS;
	}
	
	static Path getBasePath0() {
		return ASSETS_BASE_DIR;
	}

	@Override
	public Path getBasePath() {
		return getBasePath0();
	}

	@Override
	public CompletableFuture<?> addArtifact(Path p, InputStream content) {

		return CompletableFuture.runAsync(() -> {

			Path path = getBasePath().resolve(p);

			try {

				// Delete file, if exists
				Files.deleteIfExists(path);

				Files.createFile(path);
				Files.write(path, content.readAllBytes());

			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}

		});
	}

	@Override
	public CompletableFuture<?> removeArtifact(Path p) {

		return CompletableFuture.runAsync(() -> {

			Path path = getBasePath().resolve(p);

			try {

				// Delete file, if exists
				Files.deleteIfExists(path);

			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		});
	}

	@Override
	public void handler(RoutingContext ctx) {

	}

}
