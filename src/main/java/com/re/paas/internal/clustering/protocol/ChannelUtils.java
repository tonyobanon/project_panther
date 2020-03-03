package com.re.paas.internal.clustering.protocol;

import java.nio.ByteBuffer;

import com.re.paas.api.classes.ObjectSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class ChannelUtils {

	public static <R> void sendRequest(ClientOutboundRequestHandler<R> handler, ByteBuf body, Short packetSize,
			Short nodeId, Short clientId) {
		sendRequest(handler, null, body, packetSize, nodeId, clientId);
	}

	public static <R> void sendRequest(ClientOutboundRequestHandler<R> handler, ByteBuf header, ByteBuf body,
			Short packetSize, Short nodeId, Short clientId) {

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
		int segments = length / (packetSize - segmentOffset);

		if (length % (packetSize - segmentOffset) > 0) {
			segments += 1;
		}

		for (int i = 0; i < segments; i++) {

			// Segment N

			int endIndex = (packetSize - segmentOffset) * (i + 1);
			int currentIndex = endIndex - (packetSize - segmentOffset);

			// Segment buffer
			ByteBuf cb = Unpooled.directBuffer(packetSize - segmentOffset);

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

	public static void sendResponse(Channel channel, Object responseBody) {

		if (responseBody != null) {

			ByteBuffer bodyContents = ObjectSerializer.get().serialize(responseBody);
			ByteBuf body = ByteBufAllocator.DEFAULT.directBuffer(bodyContents.position(), bodyContents.position());
			body.writeBytes(bodyContents);

			// content length
			channel.write(Unpooled.copyInt(body.readableBytes()));

			// send bytes
			channel.write((ByteBuf) body.retain());

			// release buffer
			body.release();

		} else {

			// content length
			channel.write(Unpooled.copyInt(0));
		}

		// flush channel
		channel.flush();

		// close channel
		channel.close();
	}
}
