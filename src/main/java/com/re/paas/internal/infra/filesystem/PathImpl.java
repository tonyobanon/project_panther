package com.re.paas.internal.infra.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class PathImpl implements Path {

	private final FileSystemImpl fs;
	private final Path path;

	PathImpl(FileSystemImpl fs, Path path) {
		this.fs = fs;
		this.path = path;
	}

	@Override
	public FileSystem getFileSystem() {
		return fs;
	}

	@Override
	public boolean isAbsolute() {
		return path.isAbsolute();
	}

	@Override
	public Path getRoot() {
		return new PathImpl(this.fs, path.getRoot());
	}

	@Override
	public Path getFileName() {
		return new PathImpl(this.fs, path.getFileName());
	}

	@Override
	public Path getParent() {
		return new PathImpl(this.fs, path.getParent());
	}

	@Override
	public int getNameCount() {
		return path.getNameCount();
	}

	@Override
	public Path getName(int index) {
		return new PathImpl(this.fs, path.getName(index));
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		return new PathImpl(this.fs, path.subpath(beginIndex, endIndex));
	}

	@Override
	public boolean startsWith(Path other) {
		return path.startsWith(other);
	}

	@Override
	public boolean endsWith(Path other) {
		return path.endsWith(other);
	}

	@Override
	public Path normalize() {
		return new PathImpl(this.fs, path.normalize());
	}

	@Override
	public Path resolve(Path other) {
		return new PathImpl(this.fs, path.resolve(other));
	}

	@Override
	public Path relativize(Path other) {
		return new PathImpl(this.fs, path.relativize(other));
	}

	@Override
	public URI toUri() {
		return path.toUri();
	}

	@Override
	public Path toAbsolutePath() {
		return new PathImpl(this.fs, path.toAbsolutePath());
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return new PathImpl(this.fs, path.toRealPath(options));
	}

	@Override
	public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers)
			throws IOException {
		return path.register(watcher, events, modifiers);
	}

	@Override
	public int compareTo(Path other) {
		return path.compareTo(other);
	}
	
	@Override
	public String toString() {
		return path.toString();
	}

}
