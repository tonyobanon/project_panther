package com.re.paas.api.infra.filesystem;

import java.nio.file.spi.FileSystemProvider;

public abstract class AbstractFileSystemProvider extends FileSystemProvider {

	/**
	 * This should be used by threads to indicate that it wants to start a new
	 * transaction with the file system. This provides a way to tell the platform
	 * that no file system hot swapping should take place until the transaction
	 * ends. <br>
	 * If as at the time this function was invoked, a swap was about to happen, then
	 * the calling thread blocks until the swap completes
	 */
	public abstract void newContext();

	/**
	 * This indicates that the previously started transaction has ended
	 */
	public abstract void endContext();

	
	public abstract Integer getReferenceCount();
}
