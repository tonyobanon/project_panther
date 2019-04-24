
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;
import com.amazonaws.annotation.Immutable;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >REMOVE</a> action in the REMOVE section of an update expression.
 * <p>
 * A REMOVE action is used to remove one or more attributes from an item.
 */
@Beta
@Immutable
public final class RemoveAction extends UpdateAction {
    RemoveAction(PathOperand attr) {
        super("REMOVE", attr, null);
    }
}
