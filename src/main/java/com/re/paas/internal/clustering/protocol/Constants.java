package com.re.paas.internal.clustering.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	public static final short CLIENT_PACKET_FRAME_SIZE = 20;
	
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

	// K: Remote Address, V: TransactionContexts
	protected static final Map<String, TransactionContext> ServerTransactionsRT = Collections
			.synchronizedMap(new HashMap<String, TransactionContext>());
}
