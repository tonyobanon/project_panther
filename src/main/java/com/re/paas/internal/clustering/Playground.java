package com.re.paas.internal.clustering;

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
		
	}
	
	
}
