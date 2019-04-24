
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;


/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >DELETE</a> action in the DELETE section of an update expression.
 * <p>
 * A DELETE action is used to delete an element from a set. The attribute
 * involved must be a set data type.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying value (of
 * type <code>ValueOperand</code>) given during construction.
 */
 @Beta
public final class DeleteAction extends UpdateAction {
    DeleteAction(PathOperand attr, LiteralOperand value) {
        super("DELETE", attr, value);
    }
}
