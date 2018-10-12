package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.re.paas.api.classes.Exceptions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TransactionContext {

	private ByteBuf serverAddressBuf;
	
	private InetAddress serverAddress;
	
	private int totalLength;
	private short functionId;

	private byte[] bytes;
	private int currentIndex;

	protected TransactionContext() {
		serverAddressBuf = ByteBufAllocator.DEFAULT.directBuffer(4);
	}

	public void addHeaders(int totalLength, short functionId) {

		this.totalLength = totalLength;
		this.functionId = functionId;

		bytes = new byte[totalLength];
	}

	public boolean add(ByteBuf in) {

		try {

			while (currentIndex < totalLength) {
				byte b = in.readByte();
				bytes[currentIndex] = b;
				currentIndex++;
			}

		} catch (IndexOutOfBoundsException e) {
			in.release();
			return false;
		}

		in.release();
		
		return true;
	}
	
	public ByteBuf getServerAddressBuf() {
		return serverAddressBuf;
	}

	public InetAddress getServerAddress() {
		return serverAddress;
	}

	public TransactionContext setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
		this.serverAddressBuf.release();
		this.serverAddressBuf = null;
		return this;
	}
	
	public TransactionContext setServerAddress(ByteBuf serverAddress) {
		
		assert serverAddress.readableBytes() == 4;
		byte[] b = new byte[4];
		
		for(int i = 0; i < 4; i++) {
			b[i] = serverAddress.readByte();
		}
		
		try {
			setServerAddress(InetAddress.getByAddress(b));
		} catch (UnknownHostException e) {
			Exceptions.throwRuntime(e);
		}
		
		return this;
	}

	public int getTotalLength() {
		return totalLength;
	}

	public short getFunctionId() {
		return functionId;
	}

	public byte[] getBytes() {
		return bytes;
	}

}
