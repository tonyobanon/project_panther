
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a minus binary operation in building expressions that involve
 * number attributes.
 * 
 * <pre>
 * operand '-' operand
 * </pre>
 */
public final class MinusOperation extends BinaryOperation {
    MinusOperation(Operand lhs, Operand rhs) {
        super(lhs, "-", rhs);
    }
}
