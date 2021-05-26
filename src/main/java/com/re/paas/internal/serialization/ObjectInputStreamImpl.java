package com.re.paas.internal.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.spi.AppProvisioner;

public class ObjectInputStreamImpl extends ObjectInputStream {
	
    /**
     * immutable table mapping primitive type names to corresponding
     * class objects
     */
    private static final Map<String, Class<?>> primClasses =
        Map.of("boolean", boolean.class,
               "byte", byte.class,
               "char", char.class,
               "short", short.class,
               "int", int.class,
               "long", long.class,
               "float", float.class,
               "double", double.class,
               "void", void.class);
    
	private Map<ObjectStreamClass, String> _loaders = new HashMap<>();
	
	public ObjectInputStreamImpl(InputStream in) throws IOException {
		super(in);
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		
		ObjectStreamClass cls = super.readClassDescriptor();
		
        String appId = readUTF();
        
        if (!appId.equals(AppProvisioner.DEFAULT_APP_ID)) {
        	
            assert _loaders.put(cls, appId) == null;
        }
        
        return cls;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		
        String name = desc.getName();
        
        try {
        	
        	// First, try to resolve the binary name using the system classloader
        	// For example: [[[[I, [B, [Ljava.lang.Object, [[Ljava.lang.String
        	
            return Class.forName(name, false, ClassLoader.getSystemClassLoader());
            
        } catch (ClassNotFoundException ex) {
        	
        	// Then, try to get primitive class name, if applicable
        	
            Class<?> cl = primClasses.get(name);
            
            if (cl != null) {
            	
                return cl;
                
            } else {
            	
            	// Note: At this point, we know that <name> is neither a primitive, jvm or platform-internal class
            	
        		String appId = _loaders.remove(desc);
        		
        		ClassLoader acl = ClassLoaders.getClassLoader(appId);
        		
        		assert acl instanceof AppClassLoader;
        		
        		return acl.loadClass(desc.getName());            	
            }
        }
	}
}
