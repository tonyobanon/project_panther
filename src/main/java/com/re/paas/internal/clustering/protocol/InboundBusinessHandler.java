package com.re.paas.internal.clustering.protocol;

import java.nio.ByteBuffer;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectSerializer;
import com.re.paas.api.clustering.AbstractRequest;
import com.re.paas.api.clustering.Function;
import com.re.paas.internal.clustering.Functions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundBusinessHandler extends ChannelInboundHandlerAdapter {

	public static InboundBusinessHandler getInstance() {
		return new InboundBusinessHandler();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		Channel channel = ctx.channel();

		TransactionContext context = (TransactionContext) msg;

		try {

			Object parameter = ObjectSerializer.get().deserialize(ByteBuffer.wrap(context.getBytes()));
			Function function = Function.fromId(context.getFunctionId());

			if (parameter instanceof AbstractRequest) {
				((AbstractRequest) parameter).setMemberId(context.getNodeId()).setClientId(context.getClientId());
			}

			// Run function
			Functions.execute(function, parameter, channel);

		} catch (Exception e) {
			channel.newFailedFuture(e);
			Exceptions.throwRuntime(e);
		}

	}

}
