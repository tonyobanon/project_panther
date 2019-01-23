package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.clustering.Functions;
import com.re.paas.internal.errors.ClusteringError;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientImpl implements Client {

	private Short nodeId;
	private Short clientId;

	private SocketChannel channel;

	private InetAddress host;
	private Integer port;

	private boolean ownsChannel = true;
	private boolean managed;
	private ClientImpl provisional;
	
	ClientImpl(Short nodeId) {
		this.nodeId = nodeId;
	}
	
	ClientImpl(Short nodeId, Short clientId, ClientImpl nexus) {
		
		this.nodeId = nodeId;
		this.clientId = clientId;
		
		this.managed = true;
		
		this.host = nexus.host();
		this.port = nexus.port();

		this.ownsChannel = false;
		this.channel = nexus.getChannel();
	}

	ClientImpl(Short nodeId, Short clientId) {

		BaseNodeSpec spec = NodeRegistry.get().getNodes().get(nodeId);

		this.nodeId = nodeId;
		this.clientId = clientId;
		
		this.managed = true;

		init(spec.getRemoteAddress(), spec.getInboundPort());
	}

	ClientImpl(InetAddress host, Integer port) {
		init(host, port);
	}

	private void init(InetAddress host, Integer port) {

		this.host = host;
		this.port = port;

		this.channel = this.createChannel().join();
	}

	@Override
	public InetAddress host() {
		return this.host;
	}

	@Override
	public Integer port() {
		return this.port;
	}
	
	boolean managed() {
		return managed;
	}

	private final CompletableFuture<SocketChannel> createChannel() {

		final CompletableFuture<SocketChannel> future = new CompletableFuture<SocketChannel>();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					future.complete(ch);
				}

				@Override
				public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
					Exceptions.throwRuntime(cause);
				}
			});

			b.connect(new InetSocketAddress(host, port)).sync();

		} catch (InterruptedException e) {

			Exceptions.throwRuntime(
					PlatformException.get(ClusteringError.ERROR_OCCURED_WHILE_STARTING_CLIENT_SOCKET, host, port));
		} finally {
			workerGroup.shutdownGracefully();
		}

		return future;
	}
	
	@Override
	public <P, R> CompletableFuture<R> execute(Function function, P parameter, Class<R> R) {

		if (getNodeId() != null && NodeRegistry.get().getNodeId().equals(getNodeId())) {
			return Functions.execute(function, parameter);
		}

		Logger.get().debug("Making request to: " + host().getHostAddress());

		final CompletableFuture<R> completableFuture = new CompletableFuture<R>();

		Short clientId = getClientId();
		ObjectWrapper<SocketChannel> channel = new ObjectWrapper<SocketChannel>(getChannel());

		if (managed()) {
			
			ClientFactoryImpl.clientInUse(this);
			
			if (getProvisional() != null) {
				clientId = getProvisional().getClientId();
				channel.set(getProvisional().getChannel());
			}
		}

		//final String handlerName = Utils.mergeUnsigned(nodeId, clientId).toString();
		final String handlerName = managed() ? clientId.toString() : Utils.newShortRandom();
				
		ClientOutboundRequestHandler<R> handler = new ClientOutboundRequestHandler<R>(clientId,
				Function.getId(function), parameter, completableFuture);

		// Add handler to channel pipeline
		channel.get().pipeline().addLast(handlerName, handler);

		return completableFuture.whenCompleteAsync((r, t) -> {

			// remove handler from channel pipeline
			channel.get().pipeline().remove(handlerName);

			if (managed()) {
				ClientFactoryImpl.clientFree(this);	
			}
		});
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public Short getNodeId() {
		return nodeId;
	}

	@Override
	public Short getClientId() {
		return clientId;
	}

	void setClientId(Short clientId) {
		this.clientId = clientId;
	}

	SocketChannel getChannel() {
		return channel;
	}

	ClientImpl setChannel(SocketChannel channel) {
		this.channel = channel;
		return this;
	}

	ClientImpl getProvisional() {
		return provisional;
	}

	ClientImpl setProvisional(ClientImpl provisional) {
		this.provisional = provisional;
		return this;
	}

	boolean ownsChannel() {
		return ownsChannel;
	}

}
