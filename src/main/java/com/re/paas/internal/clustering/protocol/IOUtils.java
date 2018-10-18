package com.re.paas.internal.clustering.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.re.paas.api.classes.Exceptions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class IOUtils {

	public static <R> void writeAndFlush(ClientOutboundRequestHandler<R> handler, ByteBuf body, Short packetSize, Short nodeId, Short clientId) {
		writeAndFlush(handler, null, body, packetSize, nodeId, clientId);
	}

	public static <R> void writeAndFlush(ClientOutboundRequestHandler<R> handler, ByteBuf header, ByteBuf body, Short packetSize, Short nodeId, Short clientId) {

		Channel channel = handler.getChannel();
		
		assert header.readableBytes() == packetSize.intValue();

		// Write header bytes
		channel.write(header);

		// Write body bytes

		// content length
		int length = body.readableBytes();
		
		// @nodeId + @clientId = 4
		final int segmentOffset = 4;

		// segment bytes
		int segments = length / (packetSize - segmentOffset );

		if (length % (packetSize - segmentOffset ) > 0) {
			segments += 1;
		}

		for (int i = 0; i < segments; i++) {

			// Segment N

			int endIndex = (packetSize - segmentOffset ) * (i + 1);
			int currentIndex = endIndex - (packetSize - segmentOffset );

			// Segment buffer
			ByteBuf cb = Unpooled.directBuffer(packetSize - segmentOffset );

			// write nodeId
			cb.writeShort(nodeId);
			
			// write clientId
			cb.writeShort(clientId);
			
			if (i <= segments - 2) {

				// i is a proper segment index

				while (currentIndex < endIndex) {
					byte b = body.readByte();
					cb.writeByte(b);
					currentIndex++;
				}

			} else {

				boolean isBufferReadable = true;

				while (currentIndex < endIndex) {

					// i is a supplementary segment index

					if (!isBufferReadable) {
						// write random byte to complete segment
						cb.writeByte(0);
					} else {

						if (body.isReadable()) {
							byte b = body.readByte();
							cb.writeByte(b);
						} else {
							// write random byte to complete segment
							cb.writeByte(0);
							isBufferReadable = false;
						}
					}

					currentIndex++;
				}

			}

			synchronized (channel) {
				// Write segment contents
				channel.write(cb.retain());
			}

			cb.release();
		}

		// flush all messages
		channel.flush();

		// release buffer
		body.release();
	}

	public static void writeAndFlush(Channel channel, Object responseBody) {
		writeAndFlush(channel, responseBody, -1);
	}

	public static Object readObject(final byte[] data) {

		try {

			ObjectInputStream in = new ObjectInputStream(new InputStream() {

				int start = -1;
				int end = data.length - 1;

				@Override
				public int read() throws IOException {

					if (start < end) {
						start++;
						return data[start];
					}

					return -1;
				}
			});

			Object o = in.readObject();

			in.close();

			return o;

		} catch (Exception e) {
			return Exceptions.throwRuntime(e);
		}
	}

	public static void writeAndFlush(Channel channel, Object responseBody, Integer responseBodyThreshold) {

		try {

			if (responseBody != null) {

				ByteBuf buf = responseBodyThreshold == -1 ? ByteBufAllocator.DEFAULT.directBuffer()
						: ByteBufAllocator.DEFAULT.directBuffer(responseBodyThreshold);

				// Buffer responseBody
				ObjectOutputStream stream = new ObjectOutputStream(new OutputStream() {
					@Override
					public void write(int b) throws IOException {
						buf.writeByte(b);
					}
				});

				// Write to buf
				stream.writeObject(responseBody);

				// close object stream
				stream.close();

				// content length
				channel.write(Unpooled.copyInt(buf.readableBytes()));

				// send bytes
				channel.write((ByteBuf) buf.retain());

				// release buffer
				buf.release();

			} else {

				// content length
				channel.write(Unpooled.copyInt(0));
			}

			// flush channel
			channel.flush();

			// close channel
			channel.close();

		} catch (IOException e) {
			Exceptions.throwRuntime(e);

		}
	}
}
