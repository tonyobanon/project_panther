package com.re.paas.internal.filesystems;

public class FileSystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileSystemException(String msg) {
		super(msg);
	}
	
	public FileSystemException(Throwable t) {
		super(t);
	}
	
}
