package com.re.paas.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.TimeUnit;

@BlockerTodo("Generate randoms ahead of time, to increase performance")
@BlockerTodo("Also, scan for uses of SecureRandom, and do same")
public class Utils {

	private static Random random = new Random();
	private static SecureRandom secureRandom = new SecureRandom();

	public static final String newRandom() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static final String newShortRandom() {
		return newRandom().substring(0, 6);
	}
	
	public static final String randomString(int n) {
		char[] chars = new char[n];
		Arrays.fill(chars, 'c');
		return new String(chars);
	}

	public static String join(String arr[], int startIndex) {
		StringBuilder sb = new StringBuilder(24);
		for (int i = startIndex; i < arr.length; i++) {
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static JsonObject getJson(Path p) {
		return getJson(Utils.getString(p));
	}

	public static JsonArray getJsonArray(Path p) {
		return getJsonArray(Utils.getString(p));
	}

	public static JsonObject getJson(InputStream in) {
		return getJson(Utils.getString(in));
	}

	public static JsonObject getJson(String s) {
		return new JsonObject(s);
	}

	public static JsonArray getJsonArray(String s) {
		return new JsonArray(s);
	}

	public static Properties getProperties(InputStream in) {
		Properties o = new Properties();
		try {
			o.load(in);
			return o;
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		return null;
	}

	public static File asFile(URL uri) {

		File tempFile = null;
		try {
			tempFile = File.createTempFile(UUID.randomUUID().toString(), null);

			InputStream in = uri.openStream();
			OutputStream out = Files.newOutputStream(tempFile.toPath());

			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		return tempFile;
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

	public static String[] readLines(Path p) {
		try {
			return readLines(Files.newInputStream(p));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String[] readLines(InputStream in) {
		try {
			List<String> lines = new ArrayList<String>();

			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

			in.close();
			return lines.toArray(new String[lines.size()]);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String getString(Path p) {
		try {
			return getString(Files.newInputStream(p));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String getString(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int c;
			while ((c = in.read()) != -1) {
				baos.write(c);
			}
			in.close();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		return baos.toString();
	}

	public static void saveString(String o, OutputStream out) {
		try {
			StringReader in = new StringReader(o);
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}

			in.close();
			out.close();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public static void saveString(String o, Path path) {
		try {
			saveString(o, Files.newOutputStream(path));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public static void copyTo(InputStream in, OutputStream out) {
		try {
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public static String getArgument(String[] args, String key) {

		for (String arg : args) {
			if (arg.startsWith("-" + key + "=")) {
				return arg.split("=")[1];
			}
		}
		return null;
	}

	public static Map<String, Boolean> getFlags(String[] args) {

		Map<String, Boolean> result = new HashMap<>();

		for (String arg : args) {
			if (arg.startsWith("-") && arg.contains("=")) {
				String[] arr = arg.split("=");
				result.put(arr[0], (arr[1].equals("true") || arr[1].equals("1")));
			}
		}

		return result;
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

	public static final String prettify(String input) {

		input = input.toLowerCase().replace("_", " ");

		if (!input.contains(" ")) {
			return input.substring(0, 1).toUpperCase() + input.substring(1);
		}

		StringBuilder output = new StringBuilder();

		for (String word : input.split("[ ]+")) {
			output.append(word.substring(0, 1).toUpperCase() + word.substring(1)).append(" ");
		}

		return output.toString().trim();
	}

	public static boolean isProgressive(List<Integer> list) {
		return sum(list) == sum(indexes(list.size()));
	}

	public static int sum(List<Integer> list) {
		return sum(list.toArray(new Integer[list.size()]));
	}

	public static int sum(Integer[] list) {
		int i = 0;
		for (int e : list) {
			i += e;
		}
		return i;
	}

	public static Integer[] indexes(int count) {

		List<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			indexes.add(i);
		}

		return indexes.toArray(new Integer[indexes.size()]);
	}

	public static ArrayList<ArrayList<Integer>> permute(Integer[] num) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		// start from an empty list
		result.add(new ArrayList<Integer>());

		for (int i = 0; i < num.length; i++) {
			// list of list in current iteration of the array num
			ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();

			for (ArrayList<Integer> l : result) {
				// # of locations to insert is largest index + 1
				for (int j = 0; j < l.size() + 1; j++) {
					// + add num[i] to different locations
					l.add(j, num[i]);

					ArrayList<Integer> temp = new ArrayList<Integer>(l);
					current.add(temp);

					// System.out.println(temp);

					// - remove num[i] add
					l.remove(j);
				}
			}

			result = new ArrayList<ArrayList<Integer>>(current);
		}

		return result;
	}

	public static String ordinal(Integer i) {
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		switch (i % 100) {
		case 11:
		case 12:
		case 13:
			return i + "th";
		default:
			return i + sufixes[i % 10];
		}
	}

	public static String truncate(String input, int wordCount) {
		StringBuilder output = new StringBuilder();
		String arr[] = input.split("[ ]+");

		if (wordCount > arr.length) {
			wordCount = arr.length;
		}

		for (int i = 0; i < wordCount; i++) {
			String word = arr[i];
			output.append(word).append(" ");
		}
		return output.toString() + " ...";
	}

	public static Long getTimeOffset(TimeUnit unit, Date to) {
		return getTimeOffset(unit, null, to);
	}

	public static Date toDate(LocalDate localDate) {
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return date;
	}

	public static Long getTimeOffset(TimeUnit unit, Date from, Date to) {

		LocalDate now = from == null ? LocalDate.now() : from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate execTime = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return getTimeOffset(unit, now, execTime);
	}

	public static Long getTimeOffset(TimeUnit unit, LocalDate now, LocalDate execTime) {

		Duration duration = Duration.between(now, execTime);
		Long diff = null;

		switch (unit) {
		case DAYS:
			diff = duration.toDays();
			break;
		case HOURS:
			diff = duration.toHours();
			break;
		case MILLIS:
			diff = duration.toMillis();
			break;
		case MINUTES:
			diff = duration.toMinutes();
			break;
		case NANOS:
			diff = duration.toNanos();
			break;
		}

		return diff;
	}

	public static String getUserFragment(String uri) {

		Pattern p = Pattern.compile("/[a-zA-Z0-9]+([-_\\Q.\\E]*[a-zA-Z0-9]+)*\\z");

		Matcher m = p.matcher(uri);
		if (m.find()) {
			return m.group().replaceFirst("\\Q/\\E", "");
		}

		throw new IllegalArgumentException("Unable to parse the user fragment from uri: " + uri);
	}

	public static Short nextRandomShort() {
		return (short) nextRandomInt(Short.MAX_VALUE + 1);
	}

	public static int nextRandomInt() {
		return nextRandomInt(Integer.MAX_VALUE + 1);
	}

	public static int nextRandomInt(Integer exclusiveBound) {
		return random.nextInt(exclusiveBound);
	}
	
	public static Long nextRandomLong() {
		return random.nextLong();
	}
	
	public static int randomInt(Integer lowerBound, Integer upperBound) {
		return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
	}

	public static Integer mergeUnsigned(short s1, short s2) {
		return ((s1 & 0xFFFF) << 16) | (s2 & 0xFFFF);
	}
	
	public static boolean startsWith(String key, String... entries) {
		for (String e : entries) {
			if (key.startsWith(e)) {
				return true;
			}
		}
		return false;
	}

	@SafeVarargs
	public static <T> boolean equals(T key, T... entries) {
		for (T e : entries) {
			if (key.toString().equals(e.toString())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean equals(String key, String... entries) {
		for (String e : entries) {
			if (key.equals(e)) {
				return true;
			}
		}
		return false;
	}

	public static <T> List<Class<?>> fromGenericList(List<Class<T>> list) {
		List<Class<?>> o = new ArrayList<Class<?>>();
		list.forEach(c -> {
			o.add((Class<T>) c);
		});
		return o;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> toGenericList(List<Class<?>> list) {
		List<Class<T>> o = new ArrayList<Class<T>>();
		list.forEach(c -> {
			o.add((Class<T>) c);
		});
		return o;
	}

	public static <T> T call(Callable<T> task) {
		T r = null;
		try {
			r = task.call();
		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}
		return r;
	}
	
	public static <T> T getAnnotation() {
		return null;
	}
	
	public static <T> List<T> asList(Iterable<T> it) {
		List<T> l = new ArrayList<>();
		Iterator<T> i = it.iterator();
		
		while (i.hasNext()) {
			l.add(i.next());
		}
		
		return l;
	}

}
