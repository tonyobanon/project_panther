package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.protocol.AbstractNodeRequest;
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
		byte[] data = context.getBytes();

		
		try {

			// Run function
			
			Object parameter = IOUtils.readObject(data);
			Function function = Function.fromId(context.getFunctionId());
			InetAddress serverAddress = context.getServerAddress();
			
			if (parameter instanceof AbstractNodeRequest && serverAddress != null) {
				((AbstractNodeRequest) parameter).setRemoteAddress(serverAddress.getHostAddress());
			}

			Functions.execute(function, parameter, channel);
			
		} catch (Exception e) {
			channel.newFailedFuture(e);
			Exceptions.throwRuntime(e);
		}
	
	}

}
