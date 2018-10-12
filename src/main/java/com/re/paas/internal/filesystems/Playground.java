package com.re.paas.internal.filesystems;

import java.io.IOException;

public class Playground {

	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		System.out.println(Playground.class.getClassLoader().getClass().getName());
	}

}
