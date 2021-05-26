package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.clustering.protocol.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
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
	public Boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public CompletableFuture<Void> start() {
		
		CompletableFuture<Void> future = new CompletableFuture<>();
		
		ChannelFuture f = null;
		
		try {
			// Bind and start to accept incoming connections.
			f = this.bootstrap.bind(host, port);
			
			f.sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture f) {
					
					ServerImpl.this.channel = f.channel();
					
					future.complete(null);
				}
			});
		} catch (InterruptedException e) {
			
			future.completeExceptionally(f.cause());
			
//			Exceptions.throwRuntime(
//					PlatformException.get(ClusteringError.ERROR_OCCURED_WHILE_STARTING_SERVER_SOCKET, host, port));
		}
		
		return future;
	}

	@Override
	public CompletableFuture<Void> stop() {
		
		CompletableFuture<Void> future = new CompletableFuture<>();
		
		channel.close().addListener(new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> f) throws Exception {
				future.complete(null);
			};
		});
		
		return future;
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

			ServerBootstrap b = new ServerBootstrap();
			b.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class)
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

		return future;
	}
}
