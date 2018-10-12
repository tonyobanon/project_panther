package com.re.paas.internal.clustering;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import com.re.paas.api.clustering.NodeRole;

public class NodeRoleHelper {

	public static Collection<NodeRole> fromString(String stringVal) {

		Map<String, NodeRole> allRoles = NodeRole.getDelegate().getAllRoles();
		List<NodeRole> result = new ArrayList<>();

		Json.createReader(new StringReader(stringVal)).readArray().forEach(v -> {
			result.add(allRoles.get(v.toString()));
		});

		return result;
	}

	public static String toString(Collection<NodeRole> roles) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		roles.forEach(r -> {
			builder.add(r.name());
		});

		return builder.build().toString();
	}

}
