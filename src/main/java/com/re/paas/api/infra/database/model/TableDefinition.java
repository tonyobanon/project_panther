package com.re.paas.api.infra.database.model;

/**
* <p>
* Represents the input of a <code>CreateTable</code> operation.
* </p>
* 
*/
public class TableDefinition extends BaseTableDefinition {

   /**
    * <p>
    * One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped to a
    * given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size of a local
    * secondary index is unconstrained.
    * </p>
    * <p>
    * Each local secondary index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin with
    * the same partition key as the table.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * </ul>
    */
   private java.util.List<IndexDefinition> localSecondaryIndexes;
   /**
    * <p>
    * One or more global secondary indexes (the maximum is five) to be created on the table. Each global secondary
    * index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    * consisting of read and write capacity units.
    * </p>
    * </li>
    * </ul>
    */
   private java.util.List<IndexDefinition> globalSecondaryIndexes;

   /**
    * Default constructor for CreateTableRequest object. Callers should use the setter or fluent setter (with...)
    * methods to initialize the object after creating it.
    */
   public TableDefinition() {
   }

   /**
    * Constructs a new CreateTableRequest object. Callers should use the setter or fluent setter (with...) methods to
    * initialize any additional object members.
    * 
    * @param tableName
    *        The name of the table to create.
    * @param keySchema
    *        Specifies the attributes that make up the primary key for a table or an index. The attributes in
    *        <code>KeySchema</code> must also be defined in the <code>AttributeDefinitions</code> array. For more
    *        information, see <a
    *        href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data Model</a> in
    *        the <i>Amazon DynamoDB Developer Guide</i>.</p>
    *        <p>
    *        Each <code>KeySchemaElement</code> in the array is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>AttributeName</code> - The name of this key attribute.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>KeyType</code> - The role that the key attribute will assume:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>HASH</code> - partition key
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>RANGE</code> - sort key
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        </ul>
    *        <note>
    *        <p>
    *        The partition key of an item is also known as its <i>hash attribute</i>. The term "hash attribute" derives
    *        from DynamoDB' usage of an internal hash function to evenly distribute data items across partitions, based
    *        on their partition key values.
    *        </p>
    *        <p>
    *        The sort key of an item is also known as its <i>range attribute</i>. The term "range attribute" derives
    *        from the way DynamoDB stores items with the same partition key physically close together, in sorted order
    *        by the sort key value.
    *        </p>
    *        </note>
    *        <p>
    *        For a simple primary key (partition key), you must provide exactly one element with a <code>KeyType</code>
    *        of <code>HASH</code>.
    *        </p>
    *        <p>
    *        For a composite primary key (partition key and sort key), you must provide exactly two elements, in this
    *        order: The first element must have a <code>KeyType</code> of <code>HASH</code>, and the second element
    *        must have a <code>KeyType</code> of <code>RANGE</code>.
    *        </p>
    *        <p>
    *        For more information, see <a href=
    *        "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
    *        >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer Guide</i>.
    */
   public TableDefinition(String tableName, java.util.List<KeySchemaElement> keySchema) {
       setTableName(tableName);
       setKeySchema(keySchema);
   }

   /**
    * Constructs a new CreateTableRequest object. Callers should use the setter or fluent setter (with...) methods to
    * initialize any additional object members.
    * 
    * @param attributeDefinitions
    *        An array of attributes that describe the key schema for the table and indexes.
    * @param tableName
    *        The name of the table to create.
    * @param keySchema
    *        Specifies the attributes that make up the primary key for a table or an index. The attributes in
    *        <code>KeySchema</code> must also be defined in the <code>AttributeDefinitions</code> array. For more
    *        information, see <a
    *        href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data Model</a> in
    *        the <i>Amazon DynamoDB Developer Guide</i>.</p>
    *        <p>
    *        Each <code>KeySchemaElement</code> in the array is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>AttributeName</code> - The name of this key attribute.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>KeyType</code> - The role that the key attribute will assume:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>HASH</code> - partition key
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>RANGE</code> - sort key
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        </ul>
    *        <note>
    *        <p>
    *        The partition key of an item is also known as its <i>hash attribute</i>. The term "hash attribute" derives
    *        from DynamoDB' usage of an internal hash function to evenly distribute data items across partitions, based
    *        on their partition key values.
    *        </p>
    *        <p>
    *        The sort key of an item is also known as its <i>range attribute</i>. The term "range attribute" derives
    *        from the way DynamoDB stores items with the same partition key physically close together, in sorted order
    *        by the sort key value.
    *        </p>
    *        </note>
    *        <p>
    *        For a simple primary key (partition key), you must provide exactly one element with a <code>KeyType</code>
    *        of <code>HASH</code>.
    *        </p>
    *        <p>
    *        For a composite primary key (partition key and sort key), you must provide exactly two elements, in this
    *        order: The first element must have a <code>KeyType</code> of <code>HASH</code>, and the second element
    *        must have a <code>KeyType</code> of <code>RANGE</code>.
    *        </p>
    *        <p>
    *        For more information, see <a href=
    *        "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
    *        >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer Guide</i>.
    * @param provisionedThroughput
    *        Represents the provisioned throughput settings for a specified table or index. The settings can be
    *        modified using the <code>UpdateTable</code> operation.
    *        </p>
    *        <p>
    *        For current minimum and maximum provisioned throughput values, see <a
    *        href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Limits.html">Limits</a> in the
    *        <i>Amazon DynamoDB Developer Guide</i>.
    */
   public TableDefinition(java.util.List<AttributeDefinition> attributeDefinitions, String tableName, java.util.List<KeySchemaElement> keySchema) {
       setAttributeDefinitions(attributeDefinitions);
       setTableName(tableName);
       setKeySchema(keySchema);
   }


   /**
    * <p>
    * Specifies the attributes that make up the primary key for a table or an index. The attributes in
    * <code>KeySchema</code> must also be defined in the <code>AttributeDefinitions</code> array. For more information,
    * see <a href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data Model</a> in
    * the <i>Amazon DynamoDB Developer Guide</i>.
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
    * The partition key of an item is also known as its <i>hash attribute</i>. The term "hash attribute" derives from
    * DynamoDB' usage of an internal hash function to evenly distribute data items across partitions, based on their
    * partition key values.
    * </p>
    * <p>
    * The sort key of an item is also known as its <i>range attribute</i>. The term "range attribute" derives from the
    * way DynamoDB stores items with the same partition key physically close together, in sorted order by the sort key
    * value.
    * </p>
    * </note>
    * <p>
    * For a simple primary key (partition key), you must provide exactly one element with a <code>KeyType</code> of
    * <code>HASH</code>.
    * </p>
    * <p>
    * For a composite primary key (partition key and sort key), you must provide exactly two elements, in this order:
    * The first element must have a <code>KeyType</code> of <code>HASH</code>, and the second element must have a
    * <code>KeyType</code> of <code>RANGE</code>.
    * </p>
    * <p>
    * For more information, see <a href=
    * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
    * >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer Guide</i>.
    * </p>
    * 
    * @param keySchema
    *        Specifies the attributes that make up the primary key for a table or an index. The attributes in
    *        <code>KeySchema</code> must also be defined in the <code>AttributeDefinitions</code> array. For more
    *        information, see <a
    *        href="http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DataModel.html">Data Model</a> in
    *        the <i>Amazon DynamoDB Developer Guide</i>.</p>
    *        <p>
    *        Each <code>KeySchemaElement</code> in the array is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>AttributeName</code> - The name of this key attribute.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>KeyType</code> - The role that the key attribute will assume:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>HASH</code> - partition key
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>RANGE</code> - sort key
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        </ul>
    *        <note>
    *        <p>
    *        The partition key of an item is also known as its <i>hash attribute</i>. The term "hash attribute" derives
    *        from DynamoDB' usage of an internal hash function to evenly distribute data items across partitions, based
    *        on their partition key values.
    *        </p>
    *        <p>
    *        The sort key of an item is also known as its <i>range attribute</i>. The term "range attribute" derives
    *        from the way DynamoDB stores items with the same partition key physically close together, in sorted order
    *        by the sort key value.
    *        </p>
    *        </note>
    *        <p>
    *        For a simple primary key (partition key), you must provide exactly one element with a <code>KeyType</code>
    *        of <code>HASH</code>.
    *        </p>
    *        <p>
    *        For a composite primary key (partition key and sort key), you must provide exactly two elements, in this
    *        order: The first element must have a <code>KeyType</code> of <code>HASH</code>, and the second element
    *        must have a <code>KeyType</code> of <code>RANGE</code>.
    *        </p>
    *        <p>
    *        For more information, see <a href=
    *        "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html#WorkingWithTables.primary.key"
    *        >Specifying the Primary Key</a> in the <i>Amazon DynamoDB Developer Guide</i>.
    * @return Returns a reference to this object so that method calls can be chained together.
    */

   public TableDefinition withKeySchema(java.util.Collection<KeySchemaElement> keySchema) {
       setKeySchema(keySchema);
       return this;
   }

   /**
    * <p>
    * One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped to a
    * given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size of a local
    * secondary index is unconstrained.
    * </p>
    * <p>
    * Each local secondary index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin with
    * the same partition key as the table.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * </ul>
    * 
    * @return One or more local secondary indexes (the maximum is five) to be created on the table. Each index is
    *         scoped to a given partition key value. There is a 10 GB size limit per partition key value; otherwise,
    *         the size of a local secondary index is unconstrained.</p>
    *         <p>
    *         Each local secondary index in the array includes the following:
    *         </p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    *         </p>
    *         <p/></li>
    *         <li>
    *         <p>
    *         <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must
    *         begin with the same partition key as the table.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *         These are in addition to the primary key attributes and index key attributes, which are automatically
    *         projected. Each attribute specification is composed of:
    *         </p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>ProjectionType</code> - One of the following:
    *         </p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *         projected attributes are in <code>NonKeyAttributes</code>.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>ALL</code> - All of the table attributes are projected into the index.
    *         </p>
    *         </li>
    *         </ul>
    *         </li>
    *         <li>
    *         <p>
    *         <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *         secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *         all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *         indexes, this counts as two distinct attributes when determining the total.
    *         </p>
    *         </li>
    *         </ul>
    *         </li>
    */

   public java.util.List<IndexDefinition> getLocalSecondaryIndexes() {
       return localSecondaryIndexes;
   }

   /**
    * <p>
    * One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped to a
    * given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size of a local
    * secondary index is unconstrained.
    * </p>
    * <p>
    * Each local secondary index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin with
    * the same partition key as the table.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * </ul>
    * 
    * @param localSecondaryIndexes
    *        One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped
    *        to a given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size
    *        of a local secondary index is unconstrained.</p>
    *        <p>
    *        Each local secondary index in the array includes the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin
    *        with the same partition key as the table.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    */

   public void setLocalSecondaryIndexes(java.util.Collection<IndexDefinition> localSecondaryIndexes) {
       if (localSecondaryIndexes == null) {
           this.localSecondaryIndexes = null;
           return;
       }

       this.localSecondaryIndexes = new java.util.ArrayList<IndexDefinition>(localSecondaryIndexes);
   }

   /**
    * <p>
    * One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped to a
    * given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size of a local
    * secondary index is unconstrained.
    * </p>
    * <p>
    * Each local secondary index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin with
    * the same partition key as the table.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * </ul>
    * <p>
    * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
    * {@link #setLocalSecondaryIndexes(java.util.Collection)} or
    * {@link #withLocalSecondaryIndexes(java.util.Collection)} if you want to override the existing values.
    * </p>
    * 
    * @param localSecondaryIndexes
    *        One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped
    *        to a given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size
    *        of a local secondary index is unconstrained.</p>
    *        <p>
    *        Each local secondary index in the array includes the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin
    *        with the same partition key as the table.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    * @return Returns a reference to this object so that method calls can be chained together.
    */

   public TableDefinition withLocalSecondaryIndexes(IndexDefinition... localSecondaryIndexes) {
       if (this.localSecondaryIndexes == null) {
           setLocalSecondaryIndexes(new java.util.ArrayList<IndexDefinition>(localSecondaryIndexes.length));
       }
       for (IndexDefinition ele : localSecondaryIndexes) {
           this.localSecondaryIndexes.add(ele);
       }
       return this;
   }

   /**
    * <p>
    * One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped to a
    * given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size of a local
    * secondary index is unconstrained.
    * </p>
    * <p>
    * Each local secondary index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin with
    * the same partition key as the table.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * </ul>
    * 
    * @param localSecondaryIndexes
    *        One or more local secondary indexes (the maximum is five) to be created on the table. Each index is scoped
    *        to a given partition key value. There is a 10 GB size limit per partition key value; otherwise, the size
    *        of a local secondary index is unconstrained.</p>
    *        <p>
    *        Each local secondary index in the array includes the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the local secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the local secondary index. The key schema must begin
    *        with the same partition key as the table.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    * @return Returns a reference to this object so that method calls can be chained together.
    */

   public TableDefinition withLocalSecondaryIndexes(java.util.Collection<IndexDefinition> localSecondaryIndexes) {
       setLocalSecondaryIndexes(localSecondaryIndexes);
       return this;
   }

   /**
    * <p>
    * One or more global secondary indexes (the maximum is five) to be created on the table. Each global secondary
    * index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    * consisting of read and write capacity units.
    * </p>
    * </li>
    * </ul>
    * 
    * @return One or more global secondary indexes (the maximum is five) to be created on the table. Each global
    *         secondary index in the array includes the following:</p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    *         </p>
    *         <p/></li>
    *         <li>
    *         <p>
    *         <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *         These are in addition to the primary key attributes and index key attributes, which are automatically
    *         projected. Each attribute specification is composed of:
    *         </p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>ProjectionType</code> - One of the following:
    *         </p>
    *         <ul>
    *         <li>
    *         <p>
    *         <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *         projected attributes are in <code>NonKeyAttributes</code>.
    *         </p>
    *         </li>
    *         <li>
    *         <p>
    *         <code>ALL</code> - All of the table attributes are projected into the index.
    *         </p>
    *         </li>
    *         </ul>
    *         </li>
    *         <li>
    *         <p>
    *         <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *         secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *         all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *         indexes, this counts as two distinct attributes when determining the total.
    *         </p>
    *         </li>
    *         </ul>
    *         </li>
    *         <li>
    *         <p>
    *         <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    *         consisting of read and write capacity units.
    *         </p>
    *         </li>
    */

   public java.util.List<IndexDefinition> getGlobalSecondaryIndexes() {
       return globalSecondaryIndexes;
   }

   /**
    * <p>
    * One or more global secondary indexes (the maximum is five) to be created on the table. Each global secondary
    * index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    * consisting of read and write capacity units.
    * </p>
    * </li>
    * </ul>
    * 
    * @param globalSecondaryIndexes
    *        One or more global secondary indexes (the maximum is five) to be created on the table. Each global
    *        secondary index in the array includes the following:</p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    *        consisting of read and write capacity units.
    *        </p>
    *        </li>
    */

   public void setGlobalSecondaryIndexes(java.util.Collection<IndexDefinition> globalSecondaryIndexes) {
       if (globalSecondaryIndexes == null) {
           this.globalSecondaryIndexes = null;
           return;
       }

       this.globalSecondaryIndexes = new java.util.ArrayList<IndexDefinition>(globalSecondaryIndexes);
   }

   /**
    * <p>
    * One or more global secondary indexes (the maximum is five) to be created on the table. Each global secondary
    * index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    * consisting of read and write capacity units.
    * </p>
    * </li>
    * </ul>
    * <p>
    * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
    * {@link #setGlobalSecondaryIndexes(java.util.Collection)} or
    * {@link #withGlobalSecondaryIndexes(java.util.Collection)} if you want to override the existing values.
    * </p>
    * 
    * @param globalSecondaryIndexes
    *        One or more global secondary indexes (the maximum is five) to be created on the table. Each global
    *        secondary index in the array includes the following:</p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    *        consisting of read and write capacity units.
    *        </p>
    *        </li>
    * @return Returns a reference to this object so that method calls can be chained together.
    */

   public TableDefinition withGlobalSecondaryIndexes(IndexDefinition... globalSecondaryIndexes) {
       if (this.globalSecondaryIndexes == null) {
           setGlobalSecondaryIndexes(new java.util.ArrayList<IndexDefinition>(globalSecondaryIndexes.length));
       }
       for (IndexDefinition ele : globalSecondaryIndexes) {
           this.globalSecondaryIndexes.add(ele);
       }
       return this;
   }

   /**
    * <p>
    * One or more global secondary indexes (the maximum is five) to be created on the table. Each global secondary
    * index in the array includes the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    * </p>
    * <p/></li>
    * <li>
    * <p>
    * <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index. These
    * are in addition to the primary key attributes and index key attributes, which are automatically projected. Each
    * attribute specification is composed of:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>ProjectionType</code> - One of the following:
    * </p>
    * <ul>
    * <li>
    * <p>
    * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
    * attributes are in <code>NonKeyAttributes</code>.
    * </p>
    * </li>
    * <li>
    * <p>
    * <code>ALL</code> - All of the table attributes are projected into the index.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    * secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across all of
    * the secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this
    * counts as two distinct attributes when determining the total.
    * </p>
    * </li>
    * </ul>
    * </li>
    * <li>
    * <p>
    * <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    * consisting of read and write capacity units.
    * </p>
    * </li>
    * </ul>
    * 
    * @param globalSecondaryIndexes
    *        One or more global secondary indexes (the maximum is five) to be created on the table. Each global
    *        secondary index in the array includes the following:</p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>IndexName</code> - The name of the global secondary index. Must be unique only for this table.
    *        </p>
    *        <p/></li>
    *        <li>
    *        <p>
    *        <code>KeySchema</code> - Specifies the key schema for the global secondary index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>Projection</code> - Specifies attributes that are copied (projected) from the table into the index.
    *        These are in addition to the primary key attributes and index key attributes, which are automatically
    *        projected. Each attribute specification is composed of:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>ProjectionType</code> - One of the following:
    *        </p>
    *        <ul>
    *        <li>
    *        <p>
    *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
    *        projected attributes are in <code>NonKeyAttributes</code>.
    *        </p>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ALL</code> - All of the table attributes are projected into the index.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>NonKeyAttributes</code> - A list of one or more non-key attribute names that are projected into the
    *        secondary index. The total count of attributes provided in <code>NonKeyAttributes</code>, summed across
    *        all of the secondary indexes, must not exceed 20. If you project the same attribute into two different
    *        indexes, this counts as two distinct attributes when determining the total.
    *        </p>
    *        </li>
    *        </ul>
    *        </li>
    *        <li>
    *        <p>
    *        <code>ProvisionedThroughput</code> - The provisioned throughput settings for the global secondary index,
    *        consisting of read and write capacity units.
    *        </p>
    *        </li>
    * @return Returns a reference to this object so that method calls can be chained together.
    */

   public TableDefinition withGlobalSecondaryIndexes(java.util.Collection<IndexDefinition> globalSecondaryIndexes) {
       setGlobalSecondaryIndexes(globalSecondaryIndexes);
       return this;
   }


   /**
    * Returns a string representation of this object; useful for testing and debugging.
    *
    * @return A string representation of this object.
    *
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("{");
       if (getAttributeDefinitions() != null)
           sb.append("AttributeDefinitions: ").append(getAttributeDefinitions()).append(",");
       if (getTableName() != null)
           sb.append("TableName: ").append(getTableName()).append(",");
       if (getKeySchema() != null)
           sb.append("KeySchema: ").append(getKeySchema()).append(",");
       if (getLocalSecondaryIndexes() != null)
           sb.append("LocalSecondaryIndexes: ").append(getLocalSecondaryIndexes()).append(",");
       if (getGlobalSecondaryIndexes() != null)
           sb.append("GlobalSecondaryIndexes: ").append(getGlobalSecondaryIndexes()).append(",");
       sb.append("}");
       return sb.toString();
   }

   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;

       if (obj instanceof TableDefinition == false)
           return false;
       TableDefinition other = (TableDefinition) obj;
       if (other.getAttributeDefinitions() == null ^ this.getAttributeDefinitions() == null)
           return false;
       if (other.getAttributeDefinitions() != null && other.getAttributeDefinitions().equals(this.getAttributeDefinitions()) == false)
           return false;
       if (other.getTableName() == null ^ this.getTableName() == null)
           return false;
       if (other.getTableName() != null && other.getTableName().equals(this.getTableName()) == false)
           return false;
       if (other.getKeySchema() == null ^ this.getKeySchema() == null)
           return false;
       if (other.getKeySchema() != null && other.getKeySchema().equals(this.getKeySchema()) == false)
           return false;
       if (other.getLocalSecondaryIndexes() == null ^ this.getLocalSecondaryIndexes() == null)
           return false;
       if (other.getLocalSecondaryIndexes() != null && other.getLocalSecondaryIndexes().equals(this.getLocalSecondaryIndexes()) == false)
           return false;
       if (other.getGlobalSecondaryIndexes() == null ^ this.getGlobalSecondaryIndexes() == null)
           return false;
       if (other.getGlobalSecondaryIndexes() != null && other.getGlobalSecondaryIndexes().equals(this.getGlobalSecondaryIndexes()) == false)
           return false;
       return true;
   }

   @Override
   public int hashCode() {
       final int prime = 31;
       int hashCode = 1;

       hashCode = prime * hashCode + ((getAttributeDefinitions() == null) ? 0 : getAttributeDefinitions().hashCode());
       hashCode = prime * hashCode + ((getTableName() == null) ? 0 : getTableName().hashCode());
       hashCode = prime * hashCode + ((getKeySchema() == null) ? 0 : getKeySchema().hashCode());
       hashCode = prime * hashCode + ((getLocalSecondaryIndexes() == null) ? 0 : getLocalSecondaryIndexes().hashCode());
       hashCode = prime * hashCode + ((getGlobalSecondaryIndexes() == null) ? 0 : getGlobalSecondaryIndexes().hashCode());
       return hashCode;
   }

}
