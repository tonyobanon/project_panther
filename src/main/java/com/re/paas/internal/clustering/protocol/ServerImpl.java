package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.internal.clustering.classes.ServerStartEvent;
import com.re.paas.internal.clustering.classes.ServerStopEvent;
import com.re.paas.internal.errors.ClusteringError;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ServerImpl implements Server {

	private final ServerBootstrap bootstrap;
	private Channel channel;
	
	private final InetAddress host;
	private final Integer port;

	public ServerImpl(InetAddress host, Integer port) {

		this.host = host;
		this.port = port;
		
		this.bootstrap = this.createBootstrap().join();
	}

	@Override
	public void start() {
		
		
		try {
			// Bind and start to accept incoming connections.
			this.bootstrap.bind(host, port).sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
					
					ServerImpl.this.channel = future.channel();

					// Dispatch event
					AbstractEventDelegate.getInstance().dispatch(new ServerStartEvent(ServerImpl.this));
				}
			});
		} catch (InterruptedException e) {
			Exceptions.throwRuntime(
					PlatformException.get(ClusteringError.ERROR_OCCURED_WHILE_STARTING_SERVER_SOCKET, host, port));
		}
	}

	@Override
	public void stop() {
		
		channel.close().addListener(new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> future) throws Exception {
				
				// Dispatch event
				AbstractEventDelegate.getInstance().dispatch(new ServerStopEvent());
			};
		});
	}

	@Override
	public InetAddress host() {
		return host;
	}

	@Override
	public Integer port() {
		return port;
	}

	private final CompletableFuture<ServerBootstrap> createBootstrap() {

		final CompletableFuture<ServerBootstrap> future = new CompletableFuture<ServerBootstrap>();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline()
									.addLast(Constants.INBOUND_FRAME_BUFFER, InboundFrameBuffer.getInstance())
									.addLast(Constants.INBOUND_HEADER_PARSER, InboundHeaderParser.getInstance())
									.addLast(Constants.INBOUND_BODY_PARSER, InboundBodyParser.getInstance())
									.addLast(Constants.INBOUND_BUSINESS_HANDLER, InboundBusinessHandler.getInstance());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			future.complete(b);

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
		return future;
	}
}
