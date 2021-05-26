
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a function call in building expression.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 * function
 *      : ID '(' operand (',' operand)* ')'    # FunctionCall
 *      ;
 * </pre>
 */
public abstract class FunctionOperand extends Operand {
}
