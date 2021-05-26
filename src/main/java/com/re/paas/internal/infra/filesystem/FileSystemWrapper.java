package com.re.paas.internal.infra.filesystem;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public class FileSystemWrapper extends FileSystem {
	
	private FileSystem fs;
	private Boolean sealed = false;

	public FileSystemWrapper(FileSystem fs) {
		this.fs = fs;
	}
	
	public void setFileSystem(FileSystem fs) {
		if (this.sealed) {
			throw new RuntimeException(this + " is sealed");
		}
		
		this.fs = fs;
		this.sealed = true;
	}
	
	public FileSystem getFileSystem() {
		return this.fs;
	}
	
	@Override
	public FileSystemProvider provider() {
		return this.fs.provider();
	}

	@Override
	public void close() throws IOException {
		fs.close();
	}

	@Override
	public boolean isOpen() {
		return this.fs.isOpen();
	}

	@Override
	public boolean isReadOnly() {
		return this.fs.isReadOnly();
	}

	@Override
	public String getSeparator() {
		return this.fs.getSeparator();
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		return this.fs.getRootDirectories();
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return this.fs.getFileStores();
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		return this.fs.supportedFileAttributeViews();
	}

	@Override
	public Path getPath(String first, String... more) {
		return this.fs.getPath(first, more);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		return this.fs.getPathMatcher(syntaxAndPattern);
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		return this.fs.getUserPrincipalLookupService();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		return this.fs.newWatchService();
	}

}
