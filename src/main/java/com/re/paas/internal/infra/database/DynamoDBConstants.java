package com.re.paas.internal.infra.database;

public class DynamoDBConstants {
	
	static final Integer MAX_NESTED_COLLECTION_DEPTH = 32;
	public static final int OVERHEAD_SIZE_FOR_COLLECTIONS = 3;
	
	public static class AttributeTypes {
		public static final String S = "S";
		public static final String N = "N";
		public static final String B = "B";
		public static final String BOOL = "BOOL";
		public static final String M = "M";
		public static final String L = "L";
		public static final String SS = "SS";
		public static final String NS = "NS";
		public static final String BS = "BS";
		public static final String NULL = "NULL";
	}
	
	public static class TableStatuses {
		public static final String CREATING = "CREATING";
		public static final String UPDATING = "UPDATING";
		public static final String DELETING = "DELETING";
		public static final String ACTIVE = "ACTIVE";
		public static final String INACCESSIBLE_ENCRYPTION_CREDENTIALS = "INACCESSIBLE_ENCRYPTION_CREDENTIALS";
		public static final String ARCHIVING = "ARCHIVING";
		public static final String ARCHIVED = "ARCHIVED";
	}
}
