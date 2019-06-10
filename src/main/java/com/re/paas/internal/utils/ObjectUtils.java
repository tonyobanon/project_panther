package com.re.paas.internal.utils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;

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
		return cloneMultiArrayReference(src, length, false, (short)0);
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
				lock.wait();
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

	/**
	 * 
	 * <b>Implementation Notes </b> <br>
	 * 1. It is imperative to note that in a distributed platform such as this, the
	 * same class names may actually represent different classes. So this should be
	 * taken into consideration
	 * 
	 * 2. Serialization should be used 3. It is advised to reserve the set of first
	 * bytes to know the object type. Possibly there should be a large arrays, that
	 * maps apps to a list of classes
	 * 
	 * 4. If object is a String, use .getBytes(), If it is a number, use
	 * Unpooled.(...)
	 * 
	 * @param o
	 * @return
	 */

	@BlockerTodo()
	public static byte[] serialize(Object o) {

		AppClassLoader cl = (AppClassLoader) Thread.currentThread().getContextClassLoader();
		String appId = cl.getAppId();

		// appId should be included in the byte data

		/**
		 * The following is for reference purpose
		 * 
		 * 
		 * ObjectOutputStream stream = new ObjectOutputStream(new OutputStream() {
		 * 
		 * @Override public void write(int b) throws IOException { buf.writeByte(b); }
		 *           });
		 * 
		 */

		return null;
	}

	@BlockerTodo()
	public static Object deserialize(byte[] data) {

		// get appId from the first set of reserved bytes

		// then get classloader

		/**
		 * The following is for reference purpose
		 * 
		 * 
		 * try {
		 * 
		 * ObjectInputStream in = new ObjectInputStream(new InputStream() {
		 * 
		 * int start = -1; int end = data.length - 1;
		 * 
		 * @Override public int read() throws IOException {
		 * 
		 *           if (start < end) { start++; return data[start]; }
		 * 
		 *           return -1; } });
		 * 
		 *           Object o = in.readObject();
		 * 
		 *           in.close();
		 * 
		 *           return o;
		 * 
		 *           } catch (Exception e) { return Exceptions.throwRuntime(e); }
		 * 
		 */

		return null;
	}

	public static Object deserialize(InputStream in) {
		return deserialize(IOUtils.toByteArray(in));
	}

}
