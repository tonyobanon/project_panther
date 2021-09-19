package com.re.paas.internal.clustering;

import static java.util.regex.Pattern.quote;

import java.util.function.Consumer;

import com.re.paas.api.events.BaseEvent;

public class Playground {

	public Playground() {
		// TODO Auto-generated constructor stub
	}
	
	public static <T extends BaseEvent> void one(Class<T> type, Consumer<T> consumer) {
		System.out.println(type.getClass());
	}
	
	public static void main(String[] args) {
		System.out.printf("hello %s", "tony");
	}
	
	
}
