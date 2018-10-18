package com.re.paas.internal.clustering.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundBodyParser extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		// len = 22 --> [0] - [21]
		ByteBuf in = (ByteBuf) msg;
	
		short nodeId = in.readShort();
		short clientId = in.readShort();
		
		TransactionContext transaction = Constants.ServerTransactionsRT[nodeId][clientId];
		
		// Update body content
		boolean done = transaction.add(in);
		
		if (done) {

			Constants.ServerTransactionsRT[nodeId][clientId] = null;
			
			//Fire the business handler
			ctx.fireChannelRead(transaction);

		} else {
			// Do nothing, no channel handler is fired
		}

	}

	protected static InboundBodyParser getInstance() {
		return new InboundBodyParser();
	}

}
