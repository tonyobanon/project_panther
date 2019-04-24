package com.re.paas.internal.infra.database.dynamodb.qopt.tools;

import java.util.Collection;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.xspec.DeleteItemExpressionSpec;
import com.amazonaws.services.dynamodbv2.xspec.UpdateItemExpressionSpec;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.internal.infra.database.dynamodb.AttributeModel;

@BlockerTodo("Add configuration to disable consistent reads. It's enabled by default in the SDK")
public class TableAdapter extends Table {

	private AttributeModel attributeModel;
	
	public TableAdapter(AmazonDynamoDB client, String tableName) {
		super(client, tableName);
		this.attributeModel = new AttributeModelImpl();
	}
	
	private AttributeModel getAttributeModel() {
		return attributeModel;
	}

	@Override
	public final DeleteItemOutcome deleteItem(DeleteItemSpec spec) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(spec);
			getAttributeModel().deleteValue(getTableName(), spec.getKeyComponents());

			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(KeyAttribute... primaryKeyComponents) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(primaryKeyComponents);
			getAttributeModel().deleteValue(getTableName(), primaryKeyComponents);

			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(PrimaryKey primaryKey) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(primaryKey);
			getAttributeModel().deleteValue(getTableName(), primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(PrimaryKey primaryKey, DeleteItemExpressionSpec conditionExpressions) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(primaryKey, conditionExpressions);
			getAttributeModel().deleteValue(getTableName(), primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(PrimaryKey primaryKey, Expected... expected) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(primaryKey, expected);
			getAttributeModel().deleteValue(getTableName(), primaryKey.getComponents());

			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(PrimaryKey primaryKey, String conditionExpression,
			Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(primaryKey, conditionExpression, nameMap, valueMap);
			getAttributeModel().deleteValue(getTableName(), primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, Expected... expected) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, expected);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, String conditionExpression,
			Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, conditionExpression, nameMap,
					valueMap);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, DeleteItemExpressionSpec conditionExpressions) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue,
					conditionExpressions);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, Expected... expected) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue,
					expected);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final DeleteItemOutcome deleteItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, String conditionExpression, Map<String, String> nameMap,
			Map<String, Object> valueMap) {
		try {
			DeleteItemOutcome outcome = super.deleteItem(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue,
					conditionExpression, nameMap, valueMap);
			getAttributeModel().deleteValue(getTableName(), hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(UpdateItemSpec updateItemSpec) {
		try {
			UpdateItemOutcome outcome = super.updateItem(updateItemSpec.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
					updateItemSpec.getKeyComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(PrimaryKey primaryKey, AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withAttributeUpdate(attributeUpdates)
					.withPrimaryKey(primaryKey).withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
					primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(PrimaryKey primaryKey, Collection<Expected> expected,
			AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withAttributeUpdate(attributeUpdates)
					.withPrimaryKey(primaryKey).withReturnValues(ReturnValue.ALL_NEW).withExpected(expected));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
					primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(PrimaryKey primaryKey, String updateExpression,
			Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(
					new UpdateItemSpec().withUpdateExpression(updateExpression).withPrimaryKey(primaryKey)
							.withReturnValues(ReturnValue.ALL_NEW).withNameMap(nameMap).withValueMap(valueMap));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
					primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(PrimaryKey primaryKey, String updateExpression,
			String conditionExpression, Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withUpdateExpression(updateExpression)
					.withConditionExpression(conditionExpression).withPrimaryKey(primaryKey)
					.withReturnValues(ReturnValue.ALL_NEW).withNameMap(nameMap).withValueMap(valueMap));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
					primaryKey.getComponents());
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue,
			AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withAttributeUpdate(attributeUpdates)
					.withPrimaryKey(hashKeyName, hashKeyValue).withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, Collection<Expected> expected,
			AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(
					new UpdateItemSpec().withAttributeUpdate(attributeUpdates).withExpected(expected)
							.withPrimaryKey(hashKeyName, hashKeyValue).withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withAttributeUpdate(attributeUpdates)
					.withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
					.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue, rangeKeyName, rangeKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, Collection<Expected> expected, AttributeUpdate... attributeUpdates) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withAttributeUpdate(attributeUpdates)
					.withExpected(expected).withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
					.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue, rangeKeyName, rangeKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, String updateExpression, Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withUpdateExpression(updateExpression)
					.withNameMap(nameMap).withValueMap(valueMap)
					.withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
					.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue, rangeKeyName, rangeKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, String updateExpression, String conditionExpression, Map<String, String> nameMap,
			Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withUpdateExpression(updateExpression)
					.withNameMap(nameMap).withConditionExpression(conditionExpression).withValueMap(valueMap)
					.withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
					.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue, rangeKeyName, rangeKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String rangeKeyName,
			Object rangeKeyValue, UpdateItemExpressionSpec updateExpressions) {
		try {
			UpdateItemOutcome outcome = super.updateItem(
					new UpdateItemSpec().withUpdateExpression(updateExpressions.getUpdateExpression())
							.withNameMap(updateExpressions.getNameMap())
							.withConditionExpression(updateExpressions.getConditionExpression())
							.withValueMap(updateExpressions.getValueMap())
							.withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
							.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue, rangeKeyName, rangeKeyValue);

			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String updateExpression,
			Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withUpdateExpression(updateExpression)
					.withNameMap(nameMap).withValueMap(valueMap).withPrimaryKey(hashKeyName, hashKeyValue)
					.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue, String updateExpression,
			String conditionExpression, Map<String, String> nameMap, Map<String, Object> valueMap) {
		try {
			UpdateItemOutcome outcome = super.updateItem(new UpdateItemSpec().withUpdateExpression(updateExpression)
					.withConditionExpression(conditionExpression).withNameMap(nameMap).withValueMap(valueMap)
					.withPrimaryKey(hashKeyName, hashKeyValue).withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final UpdateItemOutcome updateItem(String hashKeyName, Object hashKeyValue,
			UpdateItemExpressionSpec updateExpressions) {
		try {
			UpdateItemOutcome outcome = super.updateItem(
					new UpdateItemSpec().withUpdateExpression(updateExpressions.getUpdateExpression())
							.withNameMap(updateExpressions.getNameMap())
							.withConditionExpression(updateExpressions.getConditionExpression())
							.withValueMap(updateExpressions.getValueMap()).withPrimaryKey(hashKeyName, hashKeyValue)
							.withReturnValues(ReturnValue.ALL_NEW));

			getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(), hashKeyName,
					hashKeyValue);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public final PutItemOutcome putItem(Item item) {
		try {
			PutItemOutcome outcome = super.putItem(item);

			getAttributeModel().putValue(getTableName(), item);
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}

}
