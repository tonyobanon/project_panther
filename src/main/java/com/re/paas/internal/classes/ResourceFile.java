package com.re.paas.internal.classes;

import java.nio.file.Files;
import java.nio.file.Path;

import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;

public abstract class ResourceFile {

	private final Path file;
	
	protected ResourceFile(String fileName) {
		this.file = Platform.getResourcePath().resolve(fileName);
	}
	
	protected <T> T load(Class<T> type) {
		if (!Files.exists(file)) {
			return null;
		}
		return GsonFactory.fromJson(Utils.getString(file), type);
	}
	
	protected void save() {
		Utils.saveString(GsonFactory.getInstance().toJson(this), file);
	}
	
	protected Path getFile() {
		return file;
	}
}
