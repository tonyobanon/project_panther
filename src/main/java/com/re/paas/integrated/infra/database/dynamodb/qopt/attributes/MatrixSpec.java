package com.re.paas.integrated.infra.database.dynamodb.qopt.attributes;

import com.kylantis.eaa.core.attributes.BaseEntitySpec;
import com.re.paas.internal.infra.database.TableScanner;

public class MatrixSpec {

public static final String TABLE_NAME = BaseEntitySpec.getPrefix() + TableScanner.getTableNameDelimiter() + "SearchGraphTable";
	
	public static final String ID = "id";
	public static final String MATRIX_ID = "matrixId";
	
	public static final String ENTITY_TYPE = "entityType";
	public static final String MATRIX_HASHKEY = "matrixHashKey";
	public static final String MATRIX = "matrix";
	
	
	public static final String MATRIX_INDEX = "matrixIndex";
	
	
}
