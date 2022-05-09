package com.re.paas.internal.infra.cache.infinispan;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.infinispan.commons.io.ByteBufferFactory;
import org.infinispan.commons.io.ByteBufferFactoryImpl;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.annotations.ProtoTypeId;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ObjectSerializer;

@BlockerTodo("Add algorithm to estimate object sizes, then impl getBufferSizePredictor(Object o)")
public class InfinispanMashaller extends ProtoStreamMarshaller {

	private static final ObjectSerializer serializer = ObjectSerializer.get();
	private static final ByteBufferFactory bufferFactory = new ByteBufferFactoryImpl();

	private static ByteBuffer javaSerializationHeader = ByteBuffer.allocate(2);

	@Override
	public boolean isMarshallable(Object o) {
		return o instanceof Serializable || super.isMarshallable(o);
	}

	private static boolean isJavaSerialized(byte[] b) {
		
		int limit = javaSerializationHeader.limit();
		
		if (b.length < limit) {
			return false;
		}
		
		ByteBuffer buf = ByteBuffer.allocate(limit);
		
		for (int i = 0; i < limit; i++) {
			buf.put(b[i]);
		}
		
		buf.position(0);
		
		return javaSerializationHeader.compareTo(buf) == 0;
	}

	@Override
	public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
		return isJavaSerialized(buf) ? serializer.deserialize(java.nio.ByteBuffer.wrap(buf, offset, length))
				: super.objectFromByteBuffer(buf, offset, length);
	}

	@Override
	protected org.infinispan.commons.io.ByteBuffer objectToBuffer(Object o, int estimatedSize)
			throws IOException {
		
		ProtoTypeId protoType = o.getClass().getAnnotation(ProtoTypeId.class);

		if (protoType != null) {
			return super.objectToBuffer(o, estimatedSize);
		}

		ByteBuffer b1 = serializer.serialize(o);
		ByteBuffer b2 = ByteBuffer.allocate(javaSerializationHeader.limit() + b1.limit());

		b2.put(javaSerializationHeader);
		b2.put(b1);

		byte[] arr = b2.array();

		return bufferFactory.newByteBuffer(arr, 0, arr.length);
	}

	static {	
		
		javaSerializationHeader.put(new byte[] { 97, 123 });
		javaSerializationHeader.position(0);
	}

}
