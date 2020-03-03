package com.re.paas.internal.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Map;

import com.re.paas.api.runtime.ClassLoaders;

public class ObjectInputStreamImpl extends ObjectInputStream {
	
	private Map<ObjectStreamClass, String> _loaders;
	
	public ObjectInputStreamImpl(InputStream in) throws IOException {
		super(in);
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		
		ObjectStreamClass cls = super.readClassDescriptor();
        String appId = (String) readObject();
        
        _loaders.put(cls, appId);
        
        return cls;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		
		String appId = _loaders.get(desc);
		ClassLoader cl = ClassLoaders.getClassLoader(appId);
		
		return cl.loadClass(desc.getName());
	}
}
