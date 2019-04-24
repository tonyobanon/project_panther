
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;


/**
 * Represents a plus binary operation in building expressions that involve
 * number attributes.
 * 
 * <pre>
 * operand '+' operand
 * </pre>
 */
@Beta
public final class PlusOperation extends BinaryOperation {
    PlusOperation(Operand lhs, Operand rhs) {
        super(lhs, "+", rhs);
    }
}
