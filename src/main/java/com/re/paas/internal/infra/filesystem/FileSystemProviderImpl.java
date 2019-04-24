package com.re.paas.internal.infra.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.filesystem.AbstractFileSystemProvider;
import com.re.paas.api.runtime.ThreadSecurity;
import com.re.paas.internal.Platform;
import com.re.paas.internal.utils.ObjectUtils;

/**
 * This class implements the FileSystemProvider interface.
 * 
 * @author Tony
 */
public class FileSystemProviderImpl extends AbstractFileSystemProvider {

	private static final ReentrantLock swapLock = new ReentrantLock();
	private static volatile FileSystemProviderState state = FileSystemProviderState.EMPTY;

	private static AtomicInteger referenceCount = new AtomicInteger(0);
	private static Map<Long, Thread> activeTransactions = Collections.synchronizedMap(new HashMap<>());

	private static final String basePath = "/" + Platform.getPlatformPrefix() + "/appdata";

	private static FileSystemProvider provider;
	private static FileSystem fs;

	@ProtectionContext(allowJdkAccess = true)
	public FileSystemProviderImpl(FileSystemProvider provider) {
		this(provider.getFileSystem(URI.create("file:///")));
	}

	@ProtectionContext
	public FileSystemProviderImpl(FileSystem fs) {
		setFileSystem(fs);
	}

	@ProtectionContext
	public static void setFileSystem(FileSystem fs) {
		FileSystemProviderImpl.provider = fs.provider();
		FileSystemProviderImpl.fs = fs;		
	}

	@Override
	public String getScheme() {
		return "file";
	}

	@Override
	public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
		return newFileSystem(path.toUri(), env);
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileSystem getFileSystem(URI uri) {
		if (fs == null) {
			Exceptions.throwRuntime(new FileSystemNotFoundException(uri.toString()));
		}
		return new FileSystemImpl(this);
	}

	@Override
	public Path getPath(URI uri) {
		
		if(!uri.getScheme().equals("file")) {
			Exceptions.throwRuntime(new IllegalArgumentException("URI scheme is not \"file\""));
		}

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		Path p = null;
		try {
			p = provider.getPath(transform(uri));
			referenceCount.decrementAndGet();
		} catch (Exception e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return new PathImpl(new FileSystemImpl(this), p);
	}
	

	@Override
	public Path readSymbolicLink(Path link) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		Path p = null;
		try {
			p = provider.readSymbolicLink(transform(link));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return new PathImpl(new FileSystemImpl(this), p);
	}


	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		SeekableByteChannel c = null;
		try {
			c = provider.newByteChannel(transform(path), options, attrs);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return c;
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		DirectoryStream<Path> ds = null;
		try {
			ds = provider.newDirectoryStream(transform(dir), filter);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return ds;
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		try {
			provider.createDirectory(transform(dir), attrs);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public void delete(Path path) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		try {
			provider.delete(transform(path));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		try {
			provider.copy(transform(source), transform(target), options);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		try {
			provider.move(transform(source), transform(target), options);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		Boolean b = null;
		try {
			b = provider.isSameFile(transform(path), transform(path2));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return b;
	}

	@Override
	public boolean isHidden(Path path) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		Boolean b = null;
		try {
			b = provider.isHidden(transform(path));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return b;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		FileStore fs = null;
		try {
			fs = provider.getFileStore(transform(path));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return fs;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		try {
			provider.checkAccess(transform(path), modes);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		V fav = null;
		try {
			fav = provider.getFileAttributeView(transform(path), type, options);
			referenceCount.decrementAndGet();
		} catch (Exception e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return fav;
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
			throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		A ra = null;
		try {
			ra = provider.readAttributes(transform(path), type, options);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return ra;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		Map<String, Object> ra = null;
		try {
			ra = provider.readAttributes(transform(path), attributes, options);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return ra;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);
		try {
			provider.setAttribute(transform(path), attribute, value, options);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public void createLink(Path link, Path existing) throws IOException {
		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);
		try {
			provider.createLink(transform(link), transform(existing));
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);
		try {
			provider.createSymbolicLink(transform(link), transform(target), attrs);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}
	}

	@Override
	public AsynchronousFileChannel newAsynchronousFileChannel(Path path, Set<? extends OpenOption> options,
			ExecutorService executor, FileAttribute<?>... attrs) throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		AsynchronousFileChannel afc = null;
		try {
			afc = provider.newAsynchronousFileChannel(transform(path), options, executor, attrs);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return afc;
	}

	@Override
	public FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {

		FileSystemProvider provider = getProvider();

		referenceCount.addAndGet(1);

		FileChannel fc = null;
		try {
			fc = provider.newFileChannel(transform(path), options, attrs);
			referenceCount.decrementAndGet();
		} catch (IOException e) {
			referenceCount.decrementAndGet();
			throw e;
		}

		return fc;
	}

	/**
	 * This returns the underlying provider
	 * 
	 * @return
	 */
	FileSystemProvider getProvider() {

		// If the current context is a transaction, then it is guaranteed that the lock
		// will not be acquired before the transaction completes
		boolean hasTransaction = activeTransactions.containsKey(Thread.currentThread().getId());

		if (!hasTransaction) {
			// If there is a pending swap, the thread need to wait until the swap operation
			// completes
			checkPendingSwap();
		}

		return provider;
	}
	
	FileSystem getFileSystem() {
		return fs;
	}

	private final Path transform(Path path) {

		if (!(path instanceof PathImpl)) {
			Exceptions.throwRuntime(new SecurityException("Unrecognized path: " + path.toString()));
		}

		// This is required inorder to avoid a ProviderMismatchException
		return getProvider().getPath(path.toUri());
	}

	final URI transform(URI uri) {
		return URI.create(getProvider().getScheme() + "://" + basePath + "/" + ThreadSecurity.getAppId() + uri.getPath());
	}

	public static FileSystemProviderState getState() {
		return state;
	}

	public static void setState(FileSystemProviderState state) {
		FileSystemProviderImpl.state = state;
	}

	private void checkPendingSwap() {

		if (state == FileSystemProviderState.PENDING_SWAP) {

			// A swap is about to happen, so we need to wait for the swap to complete first
			// Since the lock may not have been acquired yet, we need to do a force wait

			ObjectUtils.awaitLock(swapLock, true);

			if (state != FileSystemProviderState.ACTIVE) {
				Exceptions.throwRuntime("FileSystemProvider is not active; status: " + state);
			}
		}
	}

	@Override
	public void newContext() {
		checkPendingSwap();
		Thread t = Thread.currentThread();
		activeTransactions.put(t.getId(), t);
	}

	/**
	 * This function is called to acquire the lock. Before the lock is acquired, we
	 * ensure that no active transaction exist
	 */
	static void acquireLock() {

		state = FileSystemProviderState.PENDING_SWAP;

		while (!activeTransactions.isEmpty()) {
			LockSupport.parkNanos(500);
		}

		while (referenceCount.get() != 0) {
			LockSupport.parkNanos(500);
		}

		swapLock.lock();
	}

	static void releaseLock() {

		state = FileSystemProviderState.ACTIVE;

		swapLock.unlock();
		swapLock.notifyAll();
	}

	@Override
	public void endContext() {
		Thread t = Thread.currentThread();
		activeTransactions.remove(t.getId());
	}

	@Override
	public Integer getReferenceCount() {
		return referenceCount.get();
	}

	@Override
	public String toString() {
		return "tony";
	}

}
