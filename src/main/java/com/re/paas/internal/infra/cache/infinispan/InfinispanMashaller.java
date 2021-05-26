package com.re.paas.internal.infra.cache.infinispan;

import java.io.IOException;
import java.io.Serializable;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferFactory;
import org.infinispan.commons.io.ByteBufferFactoryImpl;
import org.infinispan.commons.marshall.AbstractMarshaller;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ObjectSerializer;

@BlockerTodo("Add algorithm to estimate object sizes, then impl getBufferSizePredictor(Object o)")
public class InfinispanMashaller extends AbstractMarshaller {

	public static final MediaType MEDIA_TYPE = MediaType.fromString("application/x-ri-distributed");
	
	private static final ObjectSerializer serializer = ObjectSerializer.get();
	private static final ByteBufferFactory bufferFactory = new ByteBufferFactoryImpl();
	

	@Override
	public boolean isMarshallable(Object o) throws Exception {
		return o instanceof Serializable;
	}

	@Override
	public MediaType mediaType() {
		return MEDIA_TYPE;
	}

	@Override
	public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
		return serializer.deserialize(java.nio.ByteBuffer.wrap(buf, offset, length));
	}

	@Override
	protected ByteBuffer objectToBuffer(Object o, int estimatedSize) throws IOException, InterruptedException {
		byte[] arr = serializer.serialize(o).array();
		return bufferFactory.newByteBuffer(arr, 0, arr.length);
	}

}
