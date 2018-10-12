package com.re.paas.internal.clustering.protocol;

import com.re.paas.api.utils.Utils;

public class Playground {

	public static void main(String[] args) {
		System.out.println("Start");
		long startTime = System.nanoTime();
		for (int i = 0; i < Short.MAX_VALUE; i++) {

		}
		long estimatedTime = System.nanoTime() - startTime;

		System.out.println("End: " + estimatedTime / 1000);
	}

}
