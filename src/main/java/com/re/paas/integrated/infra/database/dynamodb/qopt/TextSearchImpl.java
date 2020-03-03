package com.re.paas.integrated.infra.database.dynamodb.qopt;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.re.paas.api.classes.IndexedNameSpec;
import com.re.paas.api.classes.IntegerWrapper;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.infra.database.textsearch.TextSearch;
import com.re.paas.integrated.infra.database.dynamodb.QueryModel;
import com.re.paas.integrated.infra.database.dynamodb.qopt.attributes.MatrixSpec;
import com.re.paas.integrated.infra.database.dynamodb.qopt.classes.SearchGraphId;
import com.re.paas.integrated.infra.database.dynamodb.qopt.classes.TextSearchCheckpoint;
import com.re.paas.integrated.infra.database.dynamodb.qopt.tools.QueryModelImpl;

public class TextSearchImpl implements TextSearch {

	private QueryModel getQueryModel() {
		return new QueryModelImpl();
	}

	/**
	 * 
	 * @param checkpointId
	 * @param consumer
	 * @return <code>true</code> if the limit was reached, otherwise
	 *         <code>false</code>
	 */
	public boolean graphSearch(String checkpointId, Consumer<Item> consumer) {

		if (checkpointId == null || !getQueryModel().getQueryCheckpoints().containsKey(checkpointId)) {
			throw new ResourceException(ResourceException.FAILED_VALIDATION);
		}

		TextSearchCheckpoint checkpoint = getQueryModel().getQueryCheckpoints().get(checkpointId);

		int partitionId = checkpoint.getPartitionId();
		ObjectWrapper<PrimaryKey> lastEvaluatedKey = new ObjectWrapper<PrimaryKey>(checkpoint.getLastEvaluatedKey());

		int partitionCount = getQueryModel()
				.partitionCount(new IndexDescriptor(MatrixSpec.TABLE_NAME, MatrixSpec.MATRIX_INDEX));
		IntegerWrapper accumulatedSoFar = new IntegerWrapper();

		while (partitionId <= partitionCount) {

			ExpressionSpecBuilder specExpr = new ExpressionSpecBuilder().withKeyCondition(N(MatrixSpec.MATRIX_HASHKEY)
					.eq(partitionId).and(S(MatrixSpec.MATRIX).beginsWith(checkpoint.getKeyword())));

			for (int entityType : checkpoint.getEntityType()) {
				specExpr.withCondition(N(MatrixSpec.ENTITY_TYPE).eq(entityType));
			}

			QuerySpec spec = specExpr.buildForQuery();
			spec.setLimit(checkpoint.getLimit() - accumulatedSoFar.get());

			if (lastEvaluatedKey.get() != null) {
				spec.setExclusiveStartKey(lastEvaluatedKey.get());
			}

			Stream<QueryResult> outcome = graphSearch(spec);

			// Due to the manner we partition the entries, 
			// it is expected that the 
			
			assert outcome.toArray().length == 1;
			
			outcome.forEach(r -> {

				accumulatedSoFar.add(r.getCount());
				lastEvaluatedKey.set(r.getLastEvaluatedKey());

				r.getItems().forEach(item -> {
					consumer.accept(item);
				});
			});

			if (accumulatedSoFar.get() >= checkpoint.getLimit()) {

				getQueryModel().getQueryCheckpoints().get(checkpointId).setPartitionId(partitionId)
						.setLastEvaluatedKey(lastEvaluatedKey.get());

				return true;

			} else {
				partitionId += 1;
				lastEvaluatedKey.set(null);
			}
		}

		// All partitions have been scanned
		getQueryModel().getQueryCheckpoints().remove(checkpointId);

		return false;
	}

	private static Stream<QueryResult> graphSearch(QuerySpec spec) {
		return Database.get().getTable(MatrixSpec.TABLE_NAME).getIndex(MatrixSpec.MATRIX_INDEX).query(spec);
	}

	/**
	 * Note to generate a matrix, use this helper function: {@code Utils#}
	 * 
	 * @param id
	 * @param entityType
	 * @param matrix
	 */
	public void putEntry(SearchGraphId id, int entityType, String matrix[]) {

		removeEntry(id);

		BatchWriteItemRequest req = new BatchWriteItemRequest();

		for (int i = 0; i < matrix.length; i++) {

			Item item = new Item().withPrimaryKey(MatrixSpec.ID, id.toString(), MatrixSpec.MATRIX_ID, i + 1)
					.withInt(MatrixSpec.ENTITY_TYPE, entityType).withString(MatrixSpec.MATRIX, matrix[i]);

			req.addRequestItem(MatrixSpec.TABLE_NAME,
					new WriteRequest(new ExpressionSpecBuilder().buildForPut().withItem(item)));
		}

		Database.get().batchWriteItem(req);
	}

	public void removeEntry(SearchGraphId id) {

		Table table = Database.get().getTable(MatrixSpec.TABLE_NAME);

		table.query(new ExpressionSpecBuilder().addProjection(MatrixSpec.MATRIX_ID)
				.withKeyCondition(S(MatrixSpec.ID).eq(id.toString())).buildForQuery()).forEach(r -> {
					r.getItems().forEach(item -> {
						DeleteItemSpec dSpec = new ExpressionSpecBuilder().buildForDeleteItem()
								.setPrimaryKey(new PrimaryKey(MatrixSpec.ID, id.toString(), MatrixSpec.MATRIX_ID,
										item.getInt(MatrixSpec.MATRIX_ID)));
						table.deleteItem(dSpec);
					});
				});
	}

	@Override
	public void add(IndexedNameSpec spec, Integer type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(IndexedNameSpec spec, Integer type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void get(Integer type, String phrase) {
		// TODO Auto-generated method stub

	}

}
