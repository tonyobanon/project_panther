package com.re.paas.internal.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectSerializer;

public class DefaultObjectSerializer implements ObjectSerializer {

	@Override
	public ByteBuffer serialize(Object o) {
		
		// If this is primitive, then convert to String

		if (o != null && Primitives.isWrapperType(o.getClass())) {
			o = Primitives.toString(o);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteBuffer r = null;

		try {

			ObjectOutputStream stream = new ObjectOutputStreamImpl(out);
			stream.writeObject(o);

			stream.close();

			r = ByteBuffer.wrap(out.toByteArray());
			out.close();

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return r;
	}

	@Override
	public Object deserialize(ByteBuffer data) {

		ByteArrayInputStream in = new ByteArrayInputStream(data.array());
		Object r = null;

		try {

			ObjectInputStream stream = new ObjectInputStreamImpl(in);

			r = stream.readObject();

			stream.close();
			in.close();

		} catch (IOException | ClassNotFoundException e) {
			Exceptions.throwRuntime(e);
		}

		// If this is primitive string, then convert back to primitive

		if (r != null && r instanceof String) {

			Object primitive = Primitives.fromString((String) r);

			if (primitive != null) {
				r = primitive;
			}
		}
		
		return r;
	}

}
