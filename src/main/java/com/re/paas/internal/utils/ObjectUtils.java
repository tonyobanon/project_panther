package com.re.paas.internal.utils;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectUtils {

	public static boolean isEmpty(Object obj) {

		switch (obj.getClass().getName()) {
		case "java.lang.String":
			return isStringEmpty((String) obj);
		default:
			return false;
		}
	}

	private static boolean isStringEmpty(String s) {
		return s.equals("") || s == null;
	}

	/**
	 * This parses the entire string
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNumber(String s) {
		return isNumber(s, true);
	}

	/**
	 * This parses the entire sub-string at index 0 and 1, without having to process
	 * the entire string
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNumber(String s, boolean scanAll) {
		try {
			NumberFormat.getInstance().parse(scanAll ? s : s.substring(0, 2));
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static byte[] toByteArray(Integer i) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		byte[] bytes = buf.putInt(i).array();
		buf.clear();
		return bytes;
	}

	public static <T> AtomicReferenceArray<T> cloneArrayReference(AtomicReferenceArray<T> src, int length) {
		AtomicReferenceArray<T> dest = new AtomicReferenceArray<T>(length);
		for (int i = 0; i < src.length(); i++) {
			dest.set(i, src.get(i));
		}
		return dest;
	}

	public static <T> AtomicReferenceArray<AtomicReferenceArray<T>> cloneMultiArrayReference(
			AtomicReferenceArray<AtomicReferenceArray<T>> src, int length) {
		return cloneMultiArrayReference(src, length, false, (short) 0);
	}

	public static <T> AtomicReferenceArray<AtomicReferenceArray<T>> cloneMultiArrayReference(
			AtomicReferenceArray<AtomicReferenceArray<T>> src, int length, boolean populateNulls, short innerLength) {
		AtomicReferenceArray<AtomicReferenceArray<T>> dest = new AtomicReferenceArray<AtomicReferenceArray<T>>(length);
		int i;
		for (i = 0; i < src.length(); i++) {
			AtomicReferenceArray<T> array = src.get(i);
			dest.set(i, array);
		}

		if (populateNulls && i < length) {
			for (; i < length; i++) {
				dest.set(i, new AtomicReferenceArray<T>(innerLength));
			}
		}
		return dest;
	}

	public static void awaitLock(ReentrantLock lock, boolean force) {
		if (force || lock.isLocked()) {
			try {
				synchronized (lock) {
				lock.wait();
			}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public static void awaitLock(ReentrantLock lock) {
		awaitLock(lock, false);
	}

	public static byte[] toByteArray(Short s) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		byte[] bytes = buf.putShort(s).array();
		buf.clear();
		return bytes;
	}

	public static Map<String, String> toStringMap(Map<String, Object> o) {
		Map<String, String> result = new HashMap<>();
		o.forEach((k, v) -> {
			result.put(k, v.toString());
		});
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(T[]... array) {
		List<T> o = new ArrayList<>();

		for (T[] a : array) {
			for (T t : a) {
				o.add(t);
			}
		}
		return (T[]) o.toArray(new Object[o.size()]);
	}

}
