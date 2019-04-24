
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;


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
@Beta
public abstract class FunctionOperand extends Operand {
}
