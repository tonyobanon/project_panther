package com.re.paas.api.infra.cache;

import java.nio.ByteBuffer;

public interface AbstractCodec {

	String decodeKey(ByteBuffer bytes);

	Object decodeValue(ByteBuffer bytes);

	ByteBuffer encodeKey(String key);

	ByteBuffer encodeValue(Object value);
}
