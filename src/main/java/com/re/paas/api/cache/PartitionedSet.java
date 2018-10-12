package com.re.paas.api.cache;

import java.util.List;

public class PartitionedSet<V> {

	private List<V> data;
	private Checkpoint next;

	public List<V> getData() {
		return data;
	}

	public PartitionedSet<V> setData(List<V> data) {
		this.data = data;
		return this;
	}

	public Checkpoint getNext() {
		return next;
	}

	public PartitionedSet<V> setNext(Checkpoint next) {
		this.next = next;
		return this;
	}
}
