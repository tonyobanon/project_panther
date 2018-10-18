package com.re.paas.internal.clustering.protocol;

import io.netty.buffer.ByteBuf;

public class TransactionContext {
	
	private final Short nodeId;
	private final Short clientId;
	
	private int totalLength;
	private final short functionId;

	private final byte[] bytes;
	private int currentIndex;


	public TransactionContext(int totalLength, short functionId, short nodeId, short clientId) {

		this.nodeId = nodeId;
		this.clientId = clientId;
		
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


	public int getTotalLength() {
		return totalLength;
	}

	public short getFunctionId() {
		return functionId;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public Short getNodeId() {
		return nodeId;
	}

	public Short getClientId() {
		return clientId;
	}
}
