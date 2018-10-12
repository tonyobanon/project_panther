package com.re.paas.internal.databases.dynamodb.tools;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.re.paas.internal.databases.dynamodb.DynamoDBSchema;
import com.re.paas.internal.databases.dynamodb.utils.DynamoDBClientUtil;

import java.util.Arrays;

public class CreateMetaTable {
	
    public static void main(String[] args) {
        String regionName = "us-west-2";
        String metaTableName = "META";

        AmazonDynamoDBClient client = DynamoDBClientUtil.createAmazonDynamoDBClient(regionName, false);

        createTable(client, metaTableName);
    }

    public static void createTable(AmazonDynamoDBClient client, String tableName) {
        CreateTableRequest createTableRequest = new CreateTableRequest();
        createTableRequest.setTableName(tableName);
        createTableRequest.setKeySchema(Arrays.asList(new KeySchemaElement(DynamoDBSchema.META_TABLE_NAME_ATTRIBUTE, KeyType.HASH)));
        createTableRequest.setAttributeDefinitions(Arrays.asList(
                new AttributeDefinition(DynamoDBSchema.META_TABLE_NAME_ATTRIBUTE, ScalarAttributeType.S)
        ));
        createTableRequest.setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        client.createTable(createTableRequest);
    }
}
