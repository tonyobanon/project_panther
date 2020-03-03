package com.re.paas.internal.clustering.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectSerializer;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.classes.Conditional;
import com.re.paas.api.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * This class instruments all outgoing connections leaving this node, to another
 * node in the cluster.
 * 
 * @author Tony
 */
public class ClientOutboundRequestHandler<R> extends ChannelInboundHandlerAdapter {

	private Short clientId;

	private Channel channel;

	private ByteBuf responseLengthBuffer;
	private int responseLength = -1;

	private ResponseReader responseReader;
	private Thread responseReaderThread;

	private final Short functionId;
	private final Object requestBody;

	private final CompletableFuture<R> future;

	// @DEV
	long startTime;
	long endTime;

	protected ClientOutboundRequestHandler(Short clientId, Short functionId, Object requestBody,
			CompletableFuture<R> future) {

		this.clientId = clientId;

		this.functionId = functionId;
		this.requestBody = requestBody;

		this.future = future;
	}

	public Short getClientId() {
		return clientId;
	}

	Channel getChannel() {
		return channel;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

		responseLengthBuffer = ByteBufAllocator.DEFAULT.directBuffer(4);
		this.channel = ctx.channel();

		sendRequest();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

		startTime = System.nanoTime();

		ByteBuf buf = (ByteBuf) msg;

		Conditional hasContentLength = responseLength != -1 ? Conditional.TRUE : Conditional.FALSE;

		switch (hasContentLength) {

		case FALSE:

			int i = responseLengthBuffer.readableBytes();

			while (i < 4 && buf.readableBytes() > 0) {
				responseLengthBuffer.writeByte(buf.readByte());
				i++;
			}

			if (i < 4) {
				// No more bytes in buf to read
				buf.release();
				return;
			}

			// There are still more bytes to read
			responseLength = responseLengthBuffer.readInt();
			responseLengthBuffer.release();

			if (responseLength == 0) {
				done(ctx, null);
				return;
			}

			// Setup our object stream
			setupStream(ctx);

			// Fall-through, to the next case

		case TRUE:

			// Add more bytes
			responseReader.update(buf);

			// Release buffer
			buf.clear();

			// Notify our ResponseReader
			responseReaderThread.interrupt();

			break;
		}
	}

	/**
	 * 
	 * This sets up the stream used to transform raw bytes into a POJO of Type T.
	 * Internally, this creates a nexus to the ResponseReader class that returns
	 * bytes of data as they become available from the server
	 * 
	 * After all bytes have been read, the future successfully completes.
	 * 
	 **/
	private void setupStream(ChannelHandlerContext ctx) {
		responseReader = this.new ResponseReader(responseLength);

		responseReaderThread = new Thread(() -> {

			Object o = null;

			try {
				o = ObjectSerializer.get().deserialize(ByteBuffer.wrap(responseReader.readAllBytes()));
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}

			// @DEV
			endTime = System.nanoTime();
			long timeElapsed = (endTime - startTime) / 1000;

			Logger.get().debug("Time elapsed for request: " + timeElapsed + " microseconds");

			done(ctx, o);
		});
		responseReaderThread.start();
	}

	private void done(ChannelHandlerContext ctx, Object response) {
		@SuppressWarnings("unchecked")
		R resp = (R) response;
		future.complete(resp);
	}

	class ResponseReader extends InputStream {

		private final ByteBuf buf;

		private final int totalSize;
		private int currentIndex;

		public ResponseReader(int bufSize) {
			totalSize = bufSize;
			buf = ByteBufAllocator.DEFAULT.directBuffer(bufSize);
		}

		@Override
		public int read() throws IOException {

			if (buf.isReadable()) {

				byte b = buf.readByte();
				currentIndex++;
				return b;

			} else {

				if (currentIndex >= totalSize) {

					// All bytes have been read
					buf.release();
					return -1;
				} else {

					try {
						// Wait for more data
						synchronized (this) {
							while (true)
								wait();
						}

					} catch (InterruptedException e) {
						// Yeah!, more data is now available
						return read();
					}

				}

			}

		}

		public void update(ByteBuf buf) {
			this.buf.writeBytes(buf);
		}

	}

	private void sendRequest() throws IOException {

		ByteBuffer bodyContents = ObjectSerializer.get().serialize(this.requestBody);
		
		ByteBuf body = ByteBufAllocator.DEFAULT.directBuffer(bodyContents.position(), bodyContents.position());
		body.writeBytes(bodyContents);

		// Create header bytes
		ByteBuf header = ByteBufAllocator.DEFAULT.directBuffer(Constants.CLIENT_PACKET_FRAME_SIZE);

		Short memberId = ClusteringServices.get().getMember().getMemberId();

		header.writeBytes(Unpooled.copyShort(Constants.HS1));
		header.writeBytes(Unpooled.copyShort(Constants.HS2));
		header.writeBytes(Unpooled.copyShort(Constants.HS3));
		header.writeBytes(Unpooled.copyShort(memberId));
		header.writeBytes(Unpooled.copyShort(getClientId()));
		header.writeBytes(Unpooled.copyInt(body.readableBytes()));
		header.writeBytes(Unpooled.copyShort(functionId));
		header.writeBytes(Unpooled.copyShort(Constants.HE1));
		header.writeBytes(Unpooled.copyShort(Constants.HE2));
		header.writeBytes(Unpooled.copyShort(Constants.HE3));

		// Write to remote host
		ChannelUtils.sendRequest(this, header, body, Constants.CLIENT_PACKET_FRAME_SIZE, memberId, getClientId());
	}
}
