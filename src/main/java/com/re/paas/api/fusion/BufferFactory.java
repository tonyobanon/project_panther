package com.re.paas.api.fusion;

import io.netty.buffer.ByteBuf;

public interface BufferFactory {

	  Buffer buffer(int initialSizeHint);

	  Buffer buffer();

	  Buffer buffer(String str);

	  Buffer buffer(String str, String enc);

	  Buffer buffer(byte[] bytes);

	  Buffer buffer(ByteBuf byteBuffer);
	}

