
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a plus binary operation in building expressions that involve
 * number attributes.
 * 
 * <pre>
 * operand '+' operand
 * </pre>
 */
public final class PlusOperation extends BinaryOperation {
    PlusOperation(Operand lhs, Operand rhs) {
        super(lhs, "+", rhs);
    }
}
