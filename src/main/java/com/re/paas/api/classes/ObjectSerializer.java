package com.re.paas.api.classes;

import java.nio.ByteBuffer;

import com.re.paas.api.designpatterns.Singleton;


public interface ObjectSerializer {

	public static ObjectSerializer get() {
		return Singleton.get(ObjectSerializer.class);
	}
	
	ByteBuffer serialize(Object o);

	Object deserialize(ByteBuffer data);

}
