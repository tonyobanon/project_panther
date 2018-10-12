package com.re.paas.internal.clustering.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.clustering.classes.Conditional;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.ObjectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * This class instruments all outgoing connections leaving this node, to another
 * node in the cluster. This is the reference client-side implementation of
 * Odyssey TCP protocol
 * 
 * @author Tony
 */
public class ClientOutboundRequestHandler<R> extends ChannelInboundHandlerAdapter {

	private Short clientId;
	private ClientImpl client;

	private Channel channel;

	private ByteBuf responseLengthBuffer;
	private int responseLength = -1;

	private ResponseReader responseReader;
	private Thread responseReaderThread;

	private final Short functionId;
	private final Object requestBody;
	private final Integer requestBodyThreshold;

	private final CompletableFuture<R> future;
	private final InetAddress serverAddress;

	// @DEV
	long startTime;
	long endTime;

	protected ClientOutboundRequestHandler(ClientImpl client, Short clientId, InetAddress serverAddress,
			Short functionId, Object requestBody, Integer requestBodyThreshold, CompletableFuture<R> future) {

		this.client = client;
		this.clientId = clientId;

		this.serverAddress = serverAddress;
		this.functionId = functionId;
		this.requestBody = requestBody;
		this.requestBodyThreshold = requestBodyThreshold;

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
			try {
				ObjectInputStream responseStream = new ObjectInputStream(responseReader);
				Object o = responseStream.readObject();
				responseStream.close();

				// @DEV
				endTime = System.nanoTime();
				long timeElapsed = (endTime - startTime) / 1000;

				Logger.get().debug("Time elapsed for request: " + timeElapsed + " microseconds");

				done(ctx, o);

			} catch (IOException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
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

		ByteBuf buf = requestBodyThreshold == -1 ? ByteBufAllocator.DEFAULT.directBuffer()
				: ByteBufAllocator.DEFAULT.directBuffer(requestBodyThreshold);

		// Buffer requestBody
		ObjectOutputStream stream = new ObjectOutputStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				buf.writeByte(b);
			}
		});

		// Write to buf
		stream.writeObject(requestBody);

		// close object stream
		stream.close();

		// Create frame bytes
		ByteBuf frame = ByteBufAllocator.DEFAULT.directBuffer(4);
		byte[] serverAddress = this.serverAddress != null ? this.serverAddress.getAddress()
				: ObjectUtils.toByteArray(-1);
		frame.writeBytes(Unpooled.copiedBuffer(serverAddress));

		// Create header bytes
		ByteBuf header = ByteBufAllocator.DEFAULT.directBuffer(Constants.CLIENT_PACKET_FRAME_SIZE);
		header.writeBytes(Unpooled.copyShort(Constants.HS1));
		header.writeBytes(Unpooled.copyShort(Constants.HS2));
		header.writeBytes(Unpooled.copyShort(Constants.HS3));
		header.writeBytes(Unpooled.copyShort(getClientId()));
		header.writeBytes(Unpooled.copyInt(buf.readableBytes()));
		header.writeBytes(Unpooled.copyShort(functionId));
		header.writeBytes(Unpooled.copyShort(Constants.HE1));
		header.writeBytes(Unpooled.copyShort(Constants.HE2));
		header.writeBytes(Unpooled.copyShort(Constants.HE3));

		// Write to remote host
		IOUtils.writeAndFlush(this, frame, header, buf, Constants.CLIENT_PACKET_FRAME_SIZE);
	}
}
