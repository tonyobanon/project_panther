package com.re.paas.internal.fusion.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.Buffer;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.classes.spec.BlobSpec;
import com.re.paas.internal.fusion.functionalities.BlobFunctionalities;
import com.re.paas.internal.models.BlobStoreModel;

public class BlobstoreService extends BaseService {

	@Override
	public String uri() {
		return "/blobstore";
	}

	@FusionEndpoint(uri = "/save", method = HttpMethod.PUT, isBlocking = true, enableMultipart = true, 
			functionality = BlobFunctionalities.Constants.SAVE_BINARY_DATA)
	public void saveBlob(RoutingContext ctx) {
		List<String> blobIds = new ArrayList<>();
		ctx.fileUploads().forEach(f -> {
			try {
				String blobId = BlobStoreModel.save(null, Files.newInputStream(Paths.get(f.uploadedFileName())));
				blobIds.add(blobId);
			} catch (IOException e) {
				// Silently fail
			}
		});
		ctx.response().write(GsonFactory.getInstance().toJson(blobIds)).end();
	}

	@FusionEndpoint(uri = "/get", requestParams = {
			"blobId" }, createXhrClient = false, cache = true, 
					functionality = BlobFunctionalities.Constants.GET_BINARY_DATA)
	public void getBlob(RoutingContext ctx) {
		
		String blobId = ctx.request().getParam("blobId");
		BlobSpec blob = BlobStoreModel.get(blobId);
		
		ctx.response()
				.putHeader("Content-Type", blob.getMimeType())
				.putHeader("Content-Length", blob.getSize().toString())
				.write(Buffer.buffer(blob.getData()));
	}

	@FusionEndpoint(uri = "/delete", requestParams = { "blobId" }, 
			functionality = BlobFunctionalities.Constants.MANAGE_BINARY_DATA)
	public void deleteBlob(RoutingContext ctx) {
		String blobId = ctx.request().getParam("blobId");
		BlobStoreModel.delete(blobId);
	}

	@FusionEndpoint(uri = "/list", 
			functionality = BlobFunctionalities.Constants.MANAGE_BINARY_DATA)
	public void list(RoutingContext ctx) {
		List<BlobSpec> entries = BlobStoreModel.list();
		ctx.response().write(GsonFactory.getInstance().toJson(entries)).end();
	}
}
