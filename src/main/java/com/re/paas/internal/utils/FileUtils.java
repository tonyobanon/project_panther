package com.re.paas.internal.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
	
}
