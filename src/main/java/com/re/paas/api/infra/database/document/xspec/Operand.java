
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents an operand for building expressions.
 * <p>
 * Underlying grammar:
 * <pre>
 * operand
 *      : path          # PathOperand
 *      | literal       # LiteralOperand
 *      | function      # FunctionCall
 *      | '(' operand ')'
 *      ;
 * </pre>
 * 
 * @see PathOperand
 * @see FunctionOperand
 * @see LiteralOperand
 */
public abstract class Operand extends UnitOfExpression {
}
