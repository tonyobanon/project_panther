
 package com.re.paas.api.infra.database.document.xspec;

/**
 * A path operand that refers to a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html"
 * >NULL</a> attribute in DynamoDB; used for building expressions.
 * <p>
 * Use {@link QueryBuilder#NULL(String)} to instantiate this class.
 */
public class NULL extends PathOperand {

    NULL(String path) {
        super(path);
    }

    /**
     * Returns a <code>SetAction</code> object (used for building update
     * expression) of setting an attribute to null.
     */
    public final SetAction set() {
        return new SetAction(this, new LiteralOperand((Object)null));
    }
}
