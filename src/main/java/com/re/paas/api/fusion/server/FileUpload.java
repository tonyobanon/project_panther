package com.re.paas.api.fusion.server;

public interface FileUpload {

	String name();

	String uploadedFileName();

	String fileName();

	long size();

	String contentType();

	String contentTransferEncoding();

	String charSet();

}