package com.re.paas.integrated.infra.database.dynamodb.qopt.classes;

public enum QueryType {
	LT_GT, EQ;

	public static QueryType parse(String queryType) {
		switch (queryType) {
		case "0":
			return LT_GT;
		case "1":
			return EQ;
		default:
			return EQ;
		}
	}

	@Override
	public String toString() {
		if (this == LT_GT) {
			return "0";
		} else {
			return "1";
		}
	}
}
