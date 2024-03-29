package com.re.paas.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.re.paas.api.classes.Exceptions;

public class IOUtils {

	public static byte[] toByteArray(File f) {
		try {

			FileInputStream in = new FileInputStream(f);
			byte[] bytes = toByteArray(in);
			in.close();
			return bytes;
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	/**
	 * Reads the input stream and returns its contents as a byte array.
	 * 
	 * @param in the input stream to read from.
	 * @return the byte array
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream in) {
		try {
			
			ByteArrayOutputStream baout = new ByteArrayOutputStream();
			
			copy(in, baout);
			in.close();
			
			return baout.toByteArray();
			
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
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
	public static long copy(InputStream input, OutputStream output) {
		try {
			byte[] buffer = new byte[4096];
			long count = 0;
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
				count += n;
			}
			return count;
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return 0l;
		}
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
