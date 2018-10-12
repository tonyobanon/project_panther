package com.re.paas.internal.clustering.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundHeaderParser extends ChannelInboundHandlerAdapter {

	protected static InboundHeaderParser getInstance() {
		return new InboundHeaderParser();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		//len = 18 --> [0] - [17]
        ByteBuf in = (ByteBuf) msg;
		
		// For predictable performance, we need a fail-fast behavior, so we test
		// in segments

		
		// Check start segment 1
		if (Constants.HS1 != in.getShort(0)) {
			ctx.fireChannelRead(in);
			return;
		}

		
		// Check start segment 2
		if (Constants.HS2 != in.getShort(2)) {
			ctx.fireChannelRead(in);
			return;
		}

		// Check start segment 3
		if (Constants.HS3 != in.getShort(4)) {
			ctx.fireChannelRead(in);
			return;
		}

		// Check end segment 1
		if (Constants.HE1 != in.getShort(12)) {
			ctx.fireChannelRead(in);
			return;
		}

		// Check end segment 2
		if (Constants.HE2 != in.getShort(14)) {
			ctx.fireChannelRead(in);
			return;
		}

		// Check end segment 3 
		if (Constants.HE3 != in.getShort(16)) {
			ctx.fireChannelRead(in);
			return;
		}

		// At this point, we are sure this packet is a header packet
		String remoteAddress = ctx.channel().remoteAddress().toString();
		
		// Update headers
		Constants.ServerTransactionsRT.get(remoteAddress)
				.addHeaders(in.getInt(6), in.getShort(10));

		in.release();
	}

}
