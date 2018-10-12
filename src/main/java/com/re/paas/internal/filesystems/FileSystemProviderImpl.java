package com.re.paas.internal.filesystems;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

import com.re.paas.api.filesystems.FileSystemAdapter;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;

/**
 * This class implements the FileSystemProvider interface.
 * 
 * @author Tony
 */
public class FileSystemProviderImpl extends FileSystemProvider {

	private static FileSystemAdapter adapter;
	private static FileSystemProvider provider;

	public FileSystemProviderImpl() {
		
	}

	/**
	 * Checks if the underlying file system supports file watchers
	 * @return
	 */
	public static boolean supportsWatchers() {
		return adapter == null ? true : adapter.supportsWatchers();
	}

	/**
	 * This attempts to load the file system provider preferred by the user.
	 */
	static void loadProvider() {

		FileSystemConfig fsConfig = FileSystemConfig.get();
		if (fsConfig != null) {
			adapter = CloudEnvironmentAdapter.getFileSystemAdapter(fsConfig.getAdapterName());
			provider = adapter.fileSystemProvider(fsConfig.getFields());
		}
	}

	@Override
	public String getScheme() {
		return "file";
	}

	@Override
	public java.nio.file.FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		return getProvider().newFileSystem(uri, env);
	}

	@Override
	public java.nio.file.FileSystem getFileSystem(URI uri) {
		return getProvider().getFileSystem(uri);
	}

	@Override
	public Path getPath(URI uri) {
		return getProvider().getPath(uri);
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		return getProvider().newByteChannel(path, options, attrs);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
		return getProvider().newDirectoryStream(dir, filter);
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		getProvider().createDirectory(dir, attrs);
	}

	@Override
	public void delete(Path path) throws IOException {
		getProvider().delete(path);
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {
		getProvider().copy(source, target, options);
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		getProvider().move(source, target, options);
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		return getProvider().isSameFile(path, path2);
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		return getProvider().isHidden(path);
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		return getProvider().getFileStore(path);
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		getProvider().checkAccess(path, modes);
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
		return getProvider().getFileAttributeView(path, type, options);
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
			throws IOException {
		return getProvider().readAttributes(path, type, options);
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		return getProvider().readAttributes(path, attributes, options);
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
		this.getProvider().setAttribute(path, attribute, value, options);
	}

	public FileSystemProvider getProvider() {
		return provider;
	}

}
