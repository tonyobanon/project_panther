 
package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference">BETWEEN</a>
 * condition in a condition expression.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 *    operand BETWEEN operand AND operand
 * </pre>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying operands.
 */
@Beta
public final class BetweenCondition extends Condition {
    private final PathOperand attribute;
    private final Operand min;
    private final Operand max;

    BetweenCondition(PathOperand attribute, Operand min, Operand max) {
        this.attribute = attribute;
        this.min = min;
        this.max = max;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return attribute.asSubstituted(context)
                + " BETWEEN "
                + min.asSubstituted(context)
                + " AND "
                + max.asSubstituted(context)
                ;
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.BETWEEN.value();
    }
}
