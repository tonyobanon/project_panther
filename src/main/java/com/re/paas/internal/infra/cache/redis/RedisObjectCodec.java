package com.re.paas.internal.infra.cache.redis;

import java.nio.ByteBuffer;

import com.re.paas.internal.utils.ObjectUtils;

import io.lettuce.core.codec.RedisCodec;

public class RedisObjectCodec implements RedisCodec<String, Object> {

	@Override
	public String decodeKey(ByteBuffer bytes) {
		return new String(bytes.array());
	}

	@Override
	public Object decodeValue(ByteBuffer bytes) {
		return ObjectUtils.deserialize(bytes.array());
	}

	@Override
	public ByteBuffer encodeKey(String key) {
		return ByteBuffer.wrap(key.getBytes());
	}

	@Override
	public ByteBuffer encodeValue(Object value) {
		return ByteBuffer.wrap(ObjectUtils.serialize(value));
	}

}
