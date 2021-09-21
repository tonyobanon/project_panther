package com.re.paas.internal.utils;

import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class Playground {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		
	
//		System.out.println(ClassLoader.getSystemClassLoader());
//		
//		System.out.println(System.getProperty("java.ext.dirs"));
//		System.out.println(System.getProperty("sun.boot.class.path"));
//		
		ByteBuffer aBuf = ByteBuffer.allocate(2);
		aBuf.put(new byte[] {56, 67});
//		
		
		ByteBuffer bBuf = ByteBuffer.allocate(2);
		bBuf.put(new byte[] {56, 67});
		
//		
//		Object o = 5l;
//		Object o1 = 5;
//		Object o2 = 1.3d;
//		Object o3 = 2.5f;
//		Object o4 = true;
//		Object o5 = new byte[3];
//		Object o6 = (short) 56;
//		
		
		System.out.println(aBuf.limit());
		System.out.println(aBuf.capacity());
		
		
		aBuf.position(0);
		bBuf.position(0);

		
		System.out.println(aBuf.compareTo(bBuf));
		
		System.out.println(aBuf.position());
		
	}

}
