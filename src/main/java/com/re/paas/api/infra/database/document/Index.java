package com.re.paas.api.infra.database.document;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.parenthesize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.re.paas.api.infra.database.document.xspec.Condition;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

public interface Index {

	Table getTable();

	IndexDescriptor getDescriptor();

	Stream<QueryResult> result(QuerySpec spec);

	Stream<ScanResult> scan(ScanSpec spec);

	default Item first(QuerySpec spec) {
		Stream<QueryResult> stream = result(spec);
		if (stream.count() == 0) {
			return null;
		}

		List<Item> items = stream.findFirst().get().getItems();

		// Do we need to check the size if items?
		return items.get(0);
	}

	default Collection<Item> all(QuerySpec spec) {
		Stream<QueryResult> stream = result(spec);
		if (stream.count() == 0) {
			return Collections.emptyList();
		}

		Collection<Item> result = new ArrayList<>();

		stream.forEach(r -> {
			result.addAll(r.getItems());
		});

		return result;
	}

	default Collection<Item> all(String hashKey, String hashValue, String... projections) {
		return all(hashKey, hashValue, null, null, projections);
	}

	default Collection<Item> all(String hashKey, String hashValue, String rangeKey, String[] rangeValues,
			String... projections) {

		Condition condition = S(hashKey).eq(hashValue);

		if (rangeKey != null) {
			Condition rangeCondtion = null;
			for (String rangeValue : rangeValues) {

				Condition c = S(rangeKey).eq(rangeValue);

				if (rangeCondtion == null) {
					rangeCondtion = c;
				} else {
					rangeCondtion = rangeCondtion.or(c);
				}
			}
			condition = condition.and(parenthesize(rangeCondtion));
		}

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().withKeyCondition(condition).addProjection(projections);

		return all(expr.buildForQuery());
	}

	default Collection<Item> all(String hashKey, String hashValues[], String... projections) {

		Condition hashCondition = null;

		for (String hashValue : hashValues) {

			Condition c = S(hashKey).eq(hashValue);

			if (hashCondition == null) {
				hashCondition = c;
			} else {
				hashCondition = hashCondition.or(c);
			}
		}

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().withKeyCondition(hashCondition);

		for (String projection : projections) {
			expr.addProjection(projection);
		}

		return all(expr.buildForQuery());
	}

}
