
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >Comparator</a> condition in building condition expression.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 *    operand comparator_symbol operand
 * 
 *    comparator_symbol ::=
 *        =
 *      | <>
 *      | <
 *      | <=
 *      | >
 *      | >=
 * </pre>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying operands.
 */
@Beta
public final class ComparatorCondition extends Condition {
    private final String comparator;
    private final Operand lhs;
    private final Operand rhs;

    ComparatorCondition(String comparator, Operand lhs, Operand rhs) {
        this.comparator = comparator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return lhs.asSubstituted(context) + " " + comparator + " "
                + rhs.asSubstituted(context);
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.Comparator.value();
    }
}
