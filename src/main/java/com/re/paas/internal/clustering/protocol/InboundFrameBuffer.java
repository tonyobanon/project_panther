package com.re.paas.internal.clustering.protocol;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class InboundFrameBuffer extends ByteToMessageDecoder {

	public static InboundFrameBuffer getInstance() {
		return new InboundFrameBuffer();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (in.readableBytes() < Constants.CLIENT_PACKET_FRAME_SIZE) {
			return;
		}
		out.add(in.readBytes(Constants.CLIENT_PACKET_FRAME_SIZE));
	}

}
