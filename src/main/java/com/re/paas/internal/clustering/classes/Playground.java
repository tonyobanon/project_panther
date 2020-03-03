package com.re.paas.internal.clustering.classes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class Playground {
	
	private static PlaygroundInstance o;

	public static void main(String[] args)
			throws InterruptedException, URISyntaxException, NoSuchAlgorithmException, IOException {

		System.out.println(new InetSocketAddress("127.0.0.1", 8080));
		
		
		
		
	}

}
