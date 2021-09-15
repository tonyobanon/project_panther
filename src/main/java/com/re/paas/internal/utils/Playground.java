package com.re.paas.internal.utils;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

public class Playground {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		
	
		System.out.println(ClassLoader.getSystemClassLoader());
		
		System.out.println(System.getProperty("java.ext.dirs"));
		System.out.println(System.getProperty("sun.boot.class.path"));
		
		
		
	}

}
