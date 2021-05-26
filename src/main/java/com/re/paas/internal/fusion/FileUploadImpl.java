package com.re.paas.internal.fusion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.FileUpload;
import com.re.paas.api.utils.Utils;

public class FileUploadImpl implements FileUpload {

	private static Tika TIKA_INSTANCE = new Tika();

	private final long size;
	private final String fileName;
	private final String mimeType;
	
	public FileUploadImpl(InputStream in) {

		byte[] bytes = null;
		File tmpFile = null;

		try {

			bytes = IOUtils.toByteArray(in);
			tmpFile = new File("/tmp/file-uploads/" + Utils.newRandom());

			FileUtils.writeByteArrayToFile(tmpFile, bytes);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		this.mimeType = TIKA_INSTANCE.detect(bytes);
		this.size = bytes.length;
		this.fileName = tmpFile.getAbsolutePath();
	}

	@Override
	public String name() {
		return fileName;
	}

	@Override
	public String uploadedFileName() {
		return fileName;
	}

	@Override
	public String fileName() {
		return fileName;
	}

	@Override
	public long size() {
		return size;
	}

	@Override
	public String contentType() {
		return mimeType;
	}

	@Override
	public String contentTransferEncoding() {
		return "UTF-8";
	}

	@Override
	public String charSet() {
		return Charset.defaultCharset().name();
	}
}
