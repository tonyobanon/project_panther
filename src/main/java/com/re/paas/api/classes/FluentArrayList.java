package com.re.paas.api.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class FluentArrayList<E> extends ArrayList<E> implements Set<E> {

	private static final long serialVersionUID = 1L;

	public FluentArrayList() {
		
	}
	
	public static <T> FluentArrayList<T> asList(T item) {
		 return new FluentArrayList<T>().with(item);
	}
	
	public FluentArrayList<E> addIfNotNull(E item){
		if(item != null) {
			super.add(item);
		}
		return this;
	}	
	public FluentArrayList<E> with(E item){
		super.add(item);
		return this;
	}
	
	public FluentArrayList<E> withAll(Collection<E> item){
		super.addAll(item);
		return this;
	}
	
}
