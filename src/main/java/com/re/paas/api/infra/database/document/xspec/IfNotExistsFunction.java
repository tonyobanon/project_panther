
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;

/**
 * Represents an <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >if_not_exists(path, operand)</a> function in building expressions. If the
 * item does not contain an attribute at the specified path, then if_not_exists
 * evaluates to operand; otherwise, it evaluates to path. You can use this
 * function to avoid overwriting an attribute already present in the item.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying operand given
 * during construction.
 */
@Beta
public final class IfNotExistsFunction<T> extends FunctionOperand {
    private final PathOperand attr;
    private final Operand operand;

    IfNotExistsFunction(PathOperand attr, Operand operand) {
        this.attr = attr;
        this.operand = operand;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return "if_not_exists(" + attr.asSubstituted(context) + ","
                + operand.asSubstituted(context) + ")";
    }
}
