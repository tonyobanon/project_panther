package com.re.paas.internal.infra.cache.redis;

import java.nio.ByteBuffer;

import com.re.paas.api.infra.cache.AbstractCodec;

import io.lettuce.core.codec.RedisCodec;

class RedisObjectCodec implements RedisCodec<String, Object> {

	private final AbstractCodec codec;
	
	public RedisObjectCodec(AbstractCodec codec) {
		this.codec = codec;
	}
	
	@Override
	public String decodeKey(ByteBuffer bytes) {
		return this.codec.decodeKey(bytes);
	}

	@Override
	public Object decodeValue(ByteBuffer bytes) {
		return this.codec.decodeValue(bytes);
	}

	@Override
	public ByteBuffer encodeKey(String key) {
		return this.codec.encodeKey(key);
	}

	@Override
	public ByteBuffer encodeValue(Object value) {
		return this.codec.encodeValue(value);
	}

}