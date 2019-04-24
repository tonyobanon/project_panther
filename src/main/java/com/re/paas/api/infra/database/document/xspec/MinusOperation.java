
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;


/**
 * Represents a minus binary operation in building expressions that involve
 * number attributes.
 * 
 * <pre>
 * operand '-' operand
 * </pre>
 */
@Beta
public final class MinusOperation extends BinaryOperation {
    MinusOperation(Operand lhs, Operand rhs) {
        super(lhs, "-", rhs);
    }
}
