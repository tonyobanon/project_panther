package com.re.paas.internal.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	/**
	 * Reads the input stream and returns its contents as a byte array.
	 * 
	 * @param in the input stream to read from.
	 * @return the byte array
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		copy(in, baout);
		return baout.toByteArray();
	}

	/**
	 * Copies all the contents from the given input stream to the given output
	 * stream.
	 * 
	 * @param input  the input stream
	 * @param output the output stream
	 * @return the number of bytes that have been copied
	 * @throws IOException if an I/O error occurs
	 */
	public static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	
	

	/**
	 * Null safe close of the given {@link Closeable} suppressing any exception.
	 *
	 * @param closeable to be closed
	 */
	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

}
