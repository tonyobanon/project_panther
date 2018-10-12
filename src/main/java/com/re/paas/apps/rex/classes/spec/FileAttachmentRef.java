package com.re.paas.apps.rex.classes.spec;

public class FileAttachmentRef {

	String name;
	String uri;
	String mimeType;
	
	
	public String getName() {
		return name;
	}
	
	public FileAttachmentRef setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getUri() {
		return uri;
	}
	public FileAttachmentRef setUri(String uri) {
		this.uri = uri;
		return this;
	}
	public String getMimeType() {
		return mimeType;
	}
	public FileAttachmentRef setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
}
