package com.re.paas.internal.clustering.protocol;

import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

public class Constants {
	
	private static final Logger LOG = LoggerFactory.get().getLog(Constants.class);
	
	public static final short CLIENT_PACKET_FRAME_SIZE = 22;
	
	protected static final short HS1 = 32;
	protected static final short HS2 = 19;
	protected static final short HS3 = 11;
	
	protected static final short HE1 = 90;
	protected static final short HE2 = 28;
	protected static final short HE3 = 15;

	// Server Channel Handlers
	protected static final String INBOUND_FRAME_BUFFER = "INBOUND_FRAME_BUFFER";
	protected static final String INBOUND_HEADER_PARSER = "INBOUND_HEADER_PARSER";
	protected static final String INBOUND_BODY_PARSER = "INBOUND_BODY_PARSER";
	protected static final String INBOUND_BUSINESS_HANDLER = "INBOUND_BUSINESS_HANDLER";

	// Client Channel Handlers
	protected static final String OUTBOUND_REQUEST_HANDLER = "OUTBOUND_REQUEST_HANDLER";
	
	public static final short MAX_TRANSACTION_COUNT = Short.MAX_VALUE;
	public static final short MAX_NODE_COUNT = 5000;

	protected static final TransactionContext[][] ServerTransactionsRT = new TransactionContext[MAX_NODE_COUNT][];
	
	static {
		
		LOG.info("Creating data store for storing server transactions");
		
		for(int i = 0; i < ServerTransactionsRT.length; i++) {
			ServerTransactionsRT[i] = new TransactionContext[MAX_TRANSACTION_COUNT];
		}
		
	}
	
}
