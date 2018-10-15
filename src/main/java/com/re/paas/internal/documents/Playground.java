package com.re.paas.internal.documents;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Playground {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(MessageDigest.getInstance("SHA256"));
	}

}
