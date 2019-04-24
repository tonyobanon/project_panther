package com.re.paas.api.infra.database.model;

public class BaseTableDefinition {

	/**
	 * <p>
	 * An array of attributes that describe the key schema for the table and
	 * indexes.
	 * </p>
	 */
	private java.util.List<AttributeDefinition> attributeDefinitions;
	/**
	 * <p>
	 * The name of the table to create.
	 * </p>
	 */
	private String tableName;
	/**
	 * <p>
	 * Specifies the attributes that make up the primary key for a table or an
	 * index. The attributes in <code>KeySchema</code> must also be defined in the
	 * <code>AttributeDefinitions</code> array.
	 * </p>
	 * <p>
	 * Each <code>KeySchemaElement</code> in the array is composed of:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>AttributeName</code> - The name of this key attribute.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>KeyType</code> - The role that the key attribute will assume:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>HASH</code> - partition key
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>RANGE</code> - sort key
	 * </p>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <note>
	 * <p>
	 * The partition key of an item is also known as its <i>hash attribute</i>. The
	 * term "hash attribute" derives from DynamoDB' usage of an internal hash
	 * function to evenly distribute data items across partitions, based on their
	 * partition key values.
	 * </p>
	 * <p>
	 * The sort key of an item is also known as its <i>range attribute</i>. The term
	 * "range attribute" derives from the way DynamoDB stores items with the same
	 * partition key physically close together, in sorted order by the sort key
	 * value.
	 * </p>
	 * </note>
	 * <p>
	 * For a simple primary key (partition key), you must provide exactly one
	 * element with a <code>KeyType</code> of <code>HASH</code>.
	 * </p>
	 * <p>
	 * For a composite primary key (partition key and sort key), you must provide
	 * exactly two elements, in this order: The first element must have a
	 * <code>KeyType</code> of <code>HASH</code>, and the second element must have a
	 * <code>KeyType</code> of <code>RANGE</code>.
	 * </p>
	 * <p>
	 * For more information, see <a href=
	 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
	 * >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer
	 * Guide</i>.
	 * </p>
	 */
	private java.util.List<KeySchemaElement> keySchema;
	
	private boolean ttlEnabled;
	

	/**
	 * <p>
	 * An array of attributes that describe the key schema for the table and
	 * indexes.
	 * </p>
	 * 
	 * @return An array of attributes that describe the key schema for the table and
	 *         indexes.
	 */

	public java.util.List<AttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	/**
	 * <p>
	 * An array of attributes that describe the key schema for the table and
	 * indexes.
	 * </p>
	 * 
	 * @param attributeDefinitions An array of attributes that describe the key
	 *                             schema for the table and indexes.
	 */

	public void setAttributeDefinitions(java.util.Collection<AttributeDefinition> attributeDefinitions) {
		if (attributeDefinitions == null) {
			this.attributeDefinitions = null;
			return;
		}

		this.attributeDefinitions = new java.util.ArrayList<AttributeDefinition>(attributeDefinitions);
	}

	/**
	 * <p>
	 * An array of attributes that describe the key schema for the table and
	 * indexes.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> This method appends the values to the existing list (if any).
	 * Use {@link #setAttributeDefinitions(java.util.Collection)} or
	 * {@link #withAttributeDefinitions(java.util.Collection)} if you want to
	 * override the existing values.
	 * </p>
	 * 
	 * @param attributeDefinitions An array of attributes that describe the key
	 *                             schema for the table and indexes.
	 * @return Returns a reference to this object so that method calls can be
	 *         chained together.
	 */

	public void setAttributeDefinitions(AttributeDefinition... attributeDefinitions) {
		if (this.attributeDefinitions == null) {
			setAttributeDefinitions(new java.util.ArrayList<AttributeDefinition>(attributeDefinitions.length));
		}
		for (AttributeDefinition ele : attributeDefinitions) {
			this.attributeDefinitions.add(ele);
		}
	}

	/**
	 * <p>
	 * The name of the table to create.
	 * </p>
	 * 
	 * @param tableName The name of the table to create.
	 */

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * <p>
	 * The name of the table to create.
	 * </p>
	 * 
	 * @return The name of the table to create.
	 */

	public String getTableName() {
		return this.tableName;
	}

	/**
	 * <p>
	 * Specifies the attributes that make up the primary key for a table or an
	 * index. The attributes in <code>KeySchema</code> must also be defined in the
	 * <code>AttributeDefinitions</code> array. For more information, see <a href=
	 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data
	 * Model</a> in the <i>Amazon DynamoDB Developer Guide</i>.
	 * </p>
	 * <p>
	 * Each <code>KeySchemaElement</code> in the array is composed of:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>AttributeName</code> - The name of this key attribute.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>KeyType</code> - The role that the key attribute will assume:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>HASH</code> - partition key
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>RANGE</code> - sort key
	 * </p>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <note>
	 * <p>
	 * The partition key of an item is also known as its <i>hash attribute</i>. The
	 * term "hash attribute" derives from DynamoDB' usage of an internal hash
	 * function to evenly distribute data items across partitions, based on their
	 * partition key values.
	 * </p>
	 * <p>
	 * The sort key of an item is also known as its <i>range attribute</i>. The term
	 * "range attribute" derives from the way DynamoDB stores items with the same
	 * partition key physically close together, in sorted order by the sort key
	 * value.
	 * </p>
	 * </note>
	 * <p>
	 * For a simple primary key (partition key), you must provide exactly one
	 * element with a <code>KeyType</code> of <code>HASH</code>.
	 * </p>
	 * <p>
	 * For a composite primary key (partition key and sort key), you must provide
	 * exactly two elements, in this order: The first element must have a
	 * <code>KeyType</code> of <code>HASH</code>, and the second element must have a
	 * <code>KeyType</code> of <code>RANGE</code>.
	 * </p>
	 * <p>
	 * For more information, see <a href=
	 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
	 * >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer
	 * Guide</i>.
	 * </p>
	 * 
	 * @return Specifies the attributes that make up the primary key for a table or
	 *         an index. The attributes in <code>KeySchema</code> must also be
	 *         defined in the <code>AttributeDefinitions</code> array. For more
	 *         information, see <a href=
	 *         "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data
	 *         Model</a> in the <i>Amazon DynamoDB Developer Guide</i>.
	 *         </p>
	 *         <p>
	 *         Each <code>KeySchemaElement</code> in the array is composed of:
	 *         </p>
	 *         <ul>
	 *         <li>
	 *         <p>
	 *         <code>AttributeName</code> - The name of this key attribute.
	 *         </p>
	 *         </li>
	 *         <li>
	 *         <p>
	 *         <code>KeyType</code> - The role that the key attribute will assume:
	 *         </p>
	 *         <ul>
	 *         <li>
	 *         <p>
	 *         <code>HASH</code> - partition key
	 *         </p>
	 *         </li>
	 *         <li>
	 *         <p>
	 *         <code>RANGE</code> - sort key
	 *         </p>
	 *         </li>
	 *         </ul>
	 *         </li>
	 *         </ul>
	 *         <note>
	 *         <p>
	 *         The partition key of an item is also known as its <i>hash
	 *         attribute</i>. The term "hash attribute" derives from DynamoDB' usage
	 *         of an internal hash function to evenly distribute data items across
	 *         partitions, based on their partition key values.
	 *         </p>
	 *         <p>
	 *         The sort key of an item is also known as its <i>range attribute</i>.
	 *         The term "range attribute" derives from the way DynamoDB stores items
	 *         with the same partition key physically close together, in sorted
	 *         order by the sort key value.
	 *         </p>
	 *         </note>
	 *         <p>
	 *         For a simple primary key (partition key), you must provide exactly
	 *         one element with a <code>KeyType</code> of <code>HASH</code>.
	 *         </p>
	 *         <p>
	 *         For a composite primary key (partition key and sort key), you must
	 *         provide exactly two elements, in this order: The first element must
	 *         have a <code>KeyType</code> of <code>HASH</code>, and the second
	 *         element must have a <code>KeyType</code> of <code>RANGE</code>.
	 *         </p>
	 *         <p>
	 *         For more information, see <a href=
	 *         "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
	 *         >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer
	 *         Guide</i>.
	 */

	public java.util.List<KeySchemaElement> getKeySchema() {
		return keySchema;
	}

	/**
	 * <p>
	 * Specifies the attributes that make up the primary key for a table or an
	 * index. The attributes in <code>KeySchema</code> must also be defined in the
	 * <code>AttributeDefinitions</code> array. For more information, see <a href=
	 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data
	 * Model</a> in the <i>Amazon DynamoDB Developer Guide</i>.
	 * </p>
	 * <p>
	 * Each <code>KeySchemaElement</code> in the array is composed of:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>AttributeName</code> - The name of this key attribute.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>KeyType</code> - The role that the key attribute will assume:
	 * </p>
	 * <ul>
	 * <li>
	 * <p>
	 * <code>HASH</code> - partition key
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <code>RANGE</code> - sort key
	 * </p>
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <note>
	 * <p>
	 * The partition key of an item is also known as its <i>hash attribute</i>. The
	 * term "hash attribute" derives from DynamoDB' usage of an internal hash
	 * function to evenly distribute data items across partitions, based on their
	 * partition key values.
	 * </p>
	 * <p>
	 * The sort key of an item is also known as its <i>range attribute</i>. The term
	 * "range attribute" derives from the way DynamoDB stores items with the same
	 * partition key physically close together, in sorted order by the sort key
	 * value.
	 * </p>
	 * </note>
	 * <p>
	 * For a simple primary key (partition key), you must provide exactly one
	 * element with a <code>KeyType</code> of <code>HASH</code>.
	 * </p>
	 * <p>
	 * For a composite primary key (partition key and sort key), you must provide
	 * exactly two elements, in this order: The first element must have a
	 * <code>KeyType</code> of <code>HASH</code>, and the second element must have a
	 * <code>KeyType</code> of <code>RANGE</code>.
	 * </p>
	 * <p>
	 * For more information, see <a href=
	 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
	 * >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer
	 * Guide</i>.
	 * </p>
	 * 
	 * @param keySchema Specifies the attributes that make up the primary key for a
	 *                  table or an index. The attributes in <code>KeySchema</code>
	 *                  must also be defined in the
	 *                  <code>AttributeDefinitions</code> array. For more
	 *                  information, see <a href=
	 *                  "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data
	 *                  Model</a> in the <i>Amazon DynamoDB Developer Guide</i>.
	 *                  </p>
	 *                  <p>
	 *                  Each <code>KeySchemaElement</code> in the array is composed
	 *                  of:
	 *                  </p>
	 *                  <ul>
	 *                  <li>
	 *                  <p>
	 *                  <code>AttributeName</code> - The name of this key attribute.
	 *                  </p>
	 *                  </li>
	 *                  <li>
	 *                  <p>
	 *                  <code>KeyType</code> - The role that the key attribute will
	 *                  assume:
	 *                  </p>
	 *                  <ul>
	 *                  <li>
	 *                  <p>
	 *                  <code>HASH</code> - partition key
	 *                  </p>
	 *                  </li>
	 *                  <li>
	 *                  <p>
	 *                  <code>RANGE</code> - sort key
	 *                  </p>
	 *                  </li>
	 *                  </ul>
	 *                  </li>
	 *                  </ul>
	 *                  <note>
	 *                  <p>
	 *                  The partition key of an item is also known as its <i>hash
	 *                  attribute</i>. The term "hash attribute" derives from
	 *                  DynamoDB' usage of an internal hash function to evenly
	 *                  distribute data items across partitions, based on their
	 *                  partition key values.
	 *                  </p>
	 *                  <p>
	 *                  The sort key of an item is also known as its <i>range
	 *                  attribute</i>. The term "range attribute" derives from the
	 *                  way DynamoDB stores items with the same partition key
	 *                  physically close together, in sorted order by the sort key
	 *                  value.
	 *                  </p>
	 *                  </note>
	 *                  <p>
	 *                  For a simple primary key (partition key), you must provide
	 *                  exactly one element with a <code>KeyType</code> of
	 *                  <code>HASH</code>.
	 *                  </p>
	 *                  <p>
	 *                  For a composite primary key (partition key and sort key),
	 *                  you must provide exactly two elements, in this order: The
	 *                  first element must have a <code>KeyType</code> of
	 *                  <code>HASH</code>, and the second element must have a
	 *                  <code>KeyType</code> of <code>RANGE</code>.
	 *                  </p>
	 *                  <p>
	 *                  For more information, see <a href=
	 *                  "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
	 *                  >Specifying the Primary Key</a> in the <i>Amazon DynamoDB
	 *                  Developer Guide</i>.
	 */

	public void setKeySchema(java.util.Collection<KeySchemaElement> keySchema) {
		if (keySchema == null) {
			this.keySchema = null;
			return;
		}

		this.keySchema = new java.util.ArrayList<KeySchemaElement>(keySchema);
	}

	public boolean isTtlEnabled() {
		return ttlEnabled;
	}

	public BaseTableDefinition setTtlEnabled(boolean ttlEnabled) {
		this.ttlEnabled = ttlEnabled;
		return this;
	}
}
