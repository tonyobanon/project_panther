package com.re.paas.internal.fusion.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.server.FileUpload;
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

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#name()
	 */
	@Override
	public String name() {
		return fileName;
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#uploadedFileName()
	 */
	@Override
	public String uploadedFileName() {
		return fileName;
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#fileName()
	 */
	@Override
	public String fileName() {
		return fileName;
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#size()
	 */
	@Override
	public long size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#contentType()
	 */
	@Override
	public String contentType() {
		return mimeType;
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#contentTransferEncoding()
	 */
	@Override
	public String contentTransferEncoding() {
		return "UTF-8";
	}

	/* (non-Javadoc)
	 * @see com.re.paas.api.fusion.server.FileUpload#charSet()
	 */
	@Override
	public String charSet() {
		return Charset.defaultCharset().name();
	}

}
