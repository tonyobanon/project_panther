package com.re.paas.internal.fusion;

import java.util.regex.Pattern;

import org.apache.tika.Tika;

import com.re.paas.api.fusion.MimeTypeDetector;

/**
 * This is only a prototype
 * @author anthonyanyanwu
 *
 */
public class MimeTypeDetectorImpl implements MimeTypeDetector {

	private static Tika TIKA_INSTANCE = new Tika();
	
	private static String getExt(String fileName) {
		String[] arr = fileName.split(Pattern.quote("."));
		return arr[arr.length - 1];
	}
	
	@Override
	public String detect(String fileName) {
		
		String ext = getExt(fileName);
		
		switch (ext) {
		
		case "woff":
		case "woff2":
		// case "ttf":
			return "font/" + ext;
			
		default:
			return TIKA_INSTANCE.detect(fileName);
		}
		
	}

}
