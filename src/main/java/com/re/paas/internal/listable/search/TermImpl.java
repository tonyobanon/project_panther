package com.re.paas.internal.listable.search;

import java.nio.ByteBuffer;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import com.re.paas.api.listable.search.AbstractTerm;

public class TermImpl implements AbstractTerm {

	private final String name;
	private final Object value;
	
	public TermImpl(AbstractTerm term) {
		this.name = term.getName();
		this.value = term.getValue();
	}
	
	public TermImpl(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public TermImpl(String name, ByteBuffer value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public Term asLuceneTerm() {
		
		if(this.value == null) {
			return new Term(this.name);
		}
		
		if(value instanceof ByteBuffer) {
			return new Term(this.name, new BytesRef(((ByteBuffer)this.value).array()));
		}
	
		return new Term(this.name, this.value.toString());
	}
}
