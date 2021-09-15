package com.re.paas.api.infra.database.document.utils;


import com.re.paas.internal.infra.database.dynamodb.classes.Schemas;
import com.re.paas.internal.infra.database.dynamodb.tables.definitions.IndexEntryTable;

public class Playground {
	
	
	public static void main(String[] args) {
		
//		System.out.println("aaa".replace("a", "bbbbaabbbb~"));

		Schemas.generate(IndexEntryTable.class).forEach((k, v) -> {
			// System.out.println(k + ": " + v);
		});
		
	}
}
