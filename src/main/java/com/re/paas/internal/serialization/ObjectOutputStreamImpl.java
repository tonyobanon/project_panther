package com.re.paas.internal.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.OutputStream;

import com.re.paas.api.runtime.ClassLoaders;

public class ObjectOutputStreamImpl extends ObjectOutputStream {

	public ObjectOutputStreamImpl(OutputStream out) throws IOException {
		super(out);
		this.useProtocolVersion(ObjectStreamConstants.PROTOCOL_VERSION_2);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {

		super.writeClassDescriptor(desc);
		
		String appId = ClassLoaders.getId(desc.forClass().getClassLoader());

		writeObject(appId);
	}

}
