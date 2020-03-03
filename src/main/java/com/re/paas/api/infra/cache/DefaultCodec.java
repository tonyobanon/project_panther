package com.re.paas.api.infra.cache;

import java.nio.ByteBuffer;

import com.re.paas.api.classes.ObjectSerializer;

public class DefaultCodec implements AbstractCodec {

	@Override
	public String decodeKey(ByteBuffer bytes) {
		return new String(bytes.array());
	}

	@Override
	public Object decodeValue(ByteBuffer bytes) {
		return ObjectSerializer.get().deserialize(bytes);
	}

	@Override
	public ByteBuffer encodeKey(String key) {
		return ByteBuffer.wrap(key.getBytes());
	}

	@Override
	public ByteBuffer encodeValue(Object value) {
		return ObjectSerializer.get().serialize(value);
	}

}
