package com.re.paas.internal.clustering.classes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.re.paas.api.clustering.classes.OsPlatform;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.Application;

public class Utils {

	private static SecureRandom secureRandom = new SecureRandom();
	private static OsPlatform platform = null;

	public static String newRandom() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String newSecureRandom() {
		return new BigInteger(130, secureRandom).toString(32);
	}

	/**
	 * This returns a set of randomly generated bytes
	 */
	public static byte[] randomBytes(int length) {
		byte[] b = new byte[length];
		SecureRandom rand = secureRandom;
		rand.nextBytes(b);
		return b;
	}

	public static String[] readLines(File o) throws IOException {

		List<String> lines = new ArrayList<String>();

		Charset charset = Charset.forName("UTF-8");
		BufferedReader reader = Files.newBufferedReader(Paths.get(o.toURI()), charset);
		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}

		reader.close();
		return lines.toArray(new String[lines.size()]);
	}

	public static InputStream getInputStream(URI uri, Map<String, String> headers) {

		HttpGet req = new HttpGet(uri);

		headers.forEach((k, v) -> {
			req.addHeader(k, v);
		});

		try {

			CloseableHttpResponse resp = HttpClients.createMinimal().execute(req);

			return resp.getEntity().getContent();

		} catch (IOException e) {
			com.re.paas.api.classes.Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String getString(URI uri, Map<String, String> headers) {

		HttpGet req = new HttpGet(uri);

		headers.forEach((k, v) -> {
			req.addHeader(k, v);
		});

		try {

			CloseableHttpResponse resp = HttpClients.createMinimal().execute(req);

			InputStream in = resp.getEntity().getContent();
			return getString(in);

		} catch (IOException e) {
			com.re.paas.api.classes.Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String[] getLines(URI uri, Map<String, String> headers) throws IOException {

		InputStream in = getInputStream(uri, headers);
		List<String> lines = new ArrayList<String>();

		Charset charset = Charset.forName("UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}

		in.close();
		return lines.toArray(new String[lines.size()]);
	}

	public static String getString(InputStream in) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		try {
			while ((c = in.read()) != -1) {
				baos.write(c);
			}

			in.close();
		} catch (IOException e) {
			com.re.paas.api.classes.Exceptions.throwRuntime(e);
		}

		return baos.toString();
	}

	public static String getString(String fileName) throws IOException {

		InputStream in = new FileInputStream(new File(fileName));
		return getString(in);
	}

	public static void saveString(String o, OutputStream out) throws IOException {

		StringReader in = new StringReader(o);
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}

		in.close();
		out.close();
	}

	public static void saveString(String o, String fileName) throws IOException {
		OutputStream out = new FileOutputStream(new File(fileName));
		saveString(o, out);
	}

	public static void copyTo(InputStream in, OutputStream out) throws IOException {
		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		in.close();
		out.close();
	}

	public static String getArgument(String[] args, String key) {

		for (String arg : args) {
			if (arg.startsWith("-" + key + "=")) {
				return arg.split("=")[1];
			}
		}
		return null;
	}

	public static Boolean hasFlag(String[] args, String key) {

		for (String arg : args) {
			if (arg.equals("-" + key) || arg.equals("--" + key)) {
				return true;
			}
		}
		return false;
	}

	public static String toMACAddress(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
		}
		return sb.toString().toLowerCase();
	}

	public static OsPlatform getPlatform() {
		return platform;
	}

	public static String getAppBaseDir() {
		
		File base = null;
		try {
			base = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		String filePath = null;

		if (base.isDirectory()) {
			filePath = base.getPath() + File.separator;
		} else if (base.isFile()) {
			// Probably a Jar file
			filePath = base.getParentFile().getPath() + File.separator;
		}
		
		return filePath;
	}
	
	static {

		Logger.get().info("Getting OS platform");
		
		String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();

		if (OPERATING_SYSTEM.indexOf("win") >= 0) {

			// Windows
			platform = OsPlatform.WINDOWS;

		} else if (OPERATING_SYSTEM.indexOf("mac") >= 0) {

			// Mac
			platform = OsPlatform.MAC;

		} else if (OPERATING_SYSTEM.indexOf("nix") >= 0 || OPERATING_SYSTEM.indexOf("nux") >= 0
				|| OPERATING_SYSTEM.indexOf("aix") > 0) {

			// Unix / Linux
			platform = OsPlatform.LINUX;

		} else if (OPERATING_SYSTEM.indexOf("sunos") >= 0) {

			// Solaris
			platform = OsPlatform.SOLARIS;
		} 

	}

}
