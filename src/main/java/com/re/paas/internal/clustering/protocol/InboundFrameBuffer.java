package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class InboundFrameBuffer extends ByteToMessageDecoder {

	public static InboundFrameBuffer getInstance() {
		return new InboundFrameBuffer();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

		String remoteAddress = ctx.channel().remoteAddress().toString();

		// Create new Transaction Context
		Constants.ServerTransactionsRT.put(remoteAddress, new TransactionContext());
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		String remoteAddress = ctx.channel().remoteAddress().toString();

		TransactionContext tCtx = Constants.ServerTransactionsRT.get(remoteAddress);

		ByteBuf serverAddressBuf = tCtx.getServerAddressBuf();
		
		if (serverAddressBuf != null && tCtx.getServerAddress() == null) {

			while (in.readableBytes() > 0 && serverAddressBuf.readableBytes() < 4) {
				byte b = in.readByte();
				serverAddressBuf.writeByte(b);
			}

			if (serverAddressBuf.readableBytes() == 4) {

				if (serverAddressBuf.getInt(0) == -1) {
					serverAddressBuf.readBytes(4);
					tCtx.setServerAddress((InetAddress) null);
				} else {
					tCtx.setServerAddress(serverAddressBuf.readBytes(4));
				}
			}

		} else {

			if (in.readableBytes() < Constants.CLIENT_PACKET_FRAME_SIZE) {
				return;
			}
			out.add(in.readBytes(Constants.CLIENT_PACKET_FRAME_SIZE));
		}

	}

}
