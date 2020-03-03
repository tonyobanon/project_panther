package com.re.paas.api.infra.database.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;

public interface Index {

	Table getTable();

	IndexDescriptor getDescriptor();

	Stream<QueryResult> query(QuerySpec spec);

	Stream<ScanResult> scan(ScanSpec spec);
	
	default Collection<Item> all(ScanSpec spec) {
		Stream<ScanResult> stream = scan(spec);
		if (stream.count() == 0) {
			return Collections.emptyList();
		}

		Collection<Item> result = new ArrayList<>();

		stream.forEach(r -> {
			result.addAll(r.getItems());
		});

		return result;
	}

	default Item first(QuerySpec spec) {
		Stream<QueryResult> stream = query(spec.setLimit(1));
		if (stream.count() == 0) {
			return null;
		}

		List<Item> items = stream.findFirst().get().getItems();

		// Do we need to check the size if items?
		return items.get(0);
	}

	default Collection<Item> all(QuerySpec spec) {
		Stream<QueryResult> stream = query(spec);
		if (stream.count() == 0) {
			return Collections.emptyList();
		}

		Collection<Item> result = new ArrayList<>();

		stream.forEach(r -> {
			result.addAll(r.getItems());
		});

		return result;
	}
	
}
