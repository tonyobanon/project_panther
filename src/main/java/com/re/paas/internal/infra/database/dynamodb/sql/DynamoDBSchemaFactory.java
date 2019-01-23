package com.re.paas.internal.infra.database.dynamodb.sql;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import com.re.paas.internal.infra.database.dynamodb.sql.utils.DynamoDBClientUtil;

import java.util.Map;

public class DynamoDBSchemaFactory implements SchemaFactory {
	
	static final String DEFAULT_META_TABLE_NAME = "META";
	
    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        
    	String regionName = (String) operand.get("dynamo_db_region");
    	
        String accesskey = (String) operand.get("dynamo_db_access_key");
        String secretKey = (String) operand.get("dynamo_db_secret_key");
        
        String metaTableName = DEFAULT_META_TABLE_NAME;
        
        Boolean local = (Boolean) operand.get("local");
        if (local == null) {
            local = false;
        }
        
        
        
        

        return new DynamoDBSchema(DynamoDBClientUtil.createAmazonDynamoDBClient(regionName, local), metaTableName);
    }
}
