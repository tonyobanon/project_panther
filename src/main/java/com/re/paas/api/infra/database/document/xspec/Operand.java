
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;


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
@Beta
public abstract class Operand extends UnitOfExpression {
}
