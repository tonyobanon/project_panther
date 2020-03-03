package com.re.paas.internal.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.integrated.models.RBModel;

public class RBUtils {

	public static List<String> getParagraphs(String locale, String key, Map<String, Object> variables) {

		List<String> o = new ArrayList<String>();
		int i = 1;

		while (true) {
			String v = RBModel.get(locale, key + "_p" + i, variables);
			if (v == null) {
				break;
			} else {
				o.add(v);
				i++;
			}
		}

		if (o.isEmpty()) {
			throw new NullPointerException("No paragraphs were found for: " + key + " using locale: " + locale);
		}

		return o;
	}

}
