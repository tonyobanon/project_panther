package com.re.paas.internal.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.re.paas.api.classes.Exceptions;

public class FileUtils {

	public static byte[] decompressBytes(final byte[] array) {
		try {
			final Inflater inflater = new Inflater();
			inflater.setInput(array, 0, array.length);
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(array.length);
			final byte[] array2 = new byte[1024];
			while (!inflater.finished()) {
				byteArrayOutputStream.write(array2, 0, inflater.inflate(array2));
			}
			byteArrayOutputStream.close();
			final byte[] byteArray = byteArrayOutputStream.toByteArray();
			inflater.end();
			return byteArray;
		} catch (IOException | DataFormatException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] compressBytes(final byte[] bytes) {
		try {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
			final Deflater deflater = new Deflater();
			deflater.setInput(bytes);
			deflater.finish();
			final byte[] array = new byte[1024];
			while (!deflater.finished()) {
				byteArrayOutputStream.write(array, 0, deflater.deflate(array));
			}
			byteArrayOutputStream.close();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void extractZipped(File zipped, Path outputFolder) {

		try {

			// Open the zip file
			ZipFile zipFile = new ZipFile(zipped);
			Enumeration<?> enu = zipFile.entries();

			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();

				// long size = zipEntry.getSize();
				// long compressedSize = zipEntry.getCompressedSize();

				// Do we need to create a directory ?
				Path file = outputFolder.resolve(name);
				if (name.endsWith("/")) {
					Files.createDirectories(file);
					continue;
				}

				Path parent = file.getParent();
				if (parent != null) {
					Files.createDirectories(parent);
				}

				// Extract the file
				InputStream is = zipFile.getInputStream(zipEntry);
				OutputStream fos = Files.newOutputStream(file);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = is.read(bytes)) >= 0) {
					fos.write(bytes, 0, length);
				}

				is.close();
				fos.close();

			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteDirectory(Path path) throws IOException {

		try (Stream<Path> walk = Files.walk(path)) {
			walk.sorted(Comparator.reverseOrder()).forEach((p) -> {
				try {
					Files.delete(p);
				} catch (IOException e) {
					Exceptions.throwRuntime(e);
				}
			});
		}
	}

}
