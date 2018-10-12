package com.re.paas.api.filesystems;

import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Map;

import com.re.paas.api.forms.Section;

public interface FileSystemAdapter {
	
	String name();
	
	String title();
	
	String icon();

	List<Section> initForm();
	
	Boolean supportsWatchers();

	FileSystemProvider fileSystemProvider(Map<String, String> fields);
}
