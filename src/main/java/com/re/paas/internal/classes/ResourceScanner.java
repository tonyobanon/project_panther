package com.re.paas.internal.classes;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.classes.Exceptions;

public class ResourceScanner {

	private Path path = AppDirectory.getBasePath();
	
    private final String ext;

	public ResourceScanner(String ext) {
		this.ext = ext;
	}

	public List<Path> scan() {

		List<Path> files = new ArrayList<Path>();

		// Scan classpath

		try {

			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					if (file.toString().endsWith(ext)) {
						files.add(file);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path file, IOException e) throws IOException {
					if (e == null) {
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return files;

	}

	public Path getPath() {
		return path;
	}

	public ResourceScanner setPath(Path path) {
		this.path = path;
		return this;
	}

}
