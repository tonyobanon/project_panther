package com.re.paas.internal.clustering;

import java.io.IOException;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.BufferSizePredictor;
import org.infinispan.commons.marshall.Marshaller;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ObjectSerializer;

@BlockerTodo("Add algorithm to estimate object sizes, then impl getBufferSizePredictor(Object o)")
public class InfinispanMashaller implements Marshaller {

	public static final MediaType MEDIA_TYPE = MediaType.fromString("application/x-ri-distributed");
	private static final ObjectSerializer serializer = ObjectSerializer.get();
	
	@Override
	public byte[] objectToByteBuffer(Object obj, int estimatedSize) throws IOException, InterruptedException {
		return serializer.serialize(obj).array();
	}

	@Override
	public byte[] objectToByteBuffer(Object obj) throws IOException, InterruptedException {
		return serializer.serialize(obj).array();
	}

	@Override
	public Object objectFromByteBuffer(byte[] buf) throws IOException, ClassNotFoundException {
		return serializer.deserialize(java.nio.ByteBuffer.wrap(buf));
	}

	@Override
	public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
		return serializer.deserialize(java.nio.ByteBuffer.wrap(buf, offset, length));
	}

	@Override
	public ByteBuffer objectToBuffer(Object o) throws IOException, InterruptedException {
		return new ByteBufferImpl(objectToByteBuffer(o));
	}

	@Override
	public boolean isMarshallable(Object o) throws Exception {
		return true;
	}

	@Override
	public BufferSizePredictor getBufferSizePredictor(Object o) {
		return null;
	}

	@Override
	public MediaType mediaType() {
		return MEDIA_TYPE;
	}

}
