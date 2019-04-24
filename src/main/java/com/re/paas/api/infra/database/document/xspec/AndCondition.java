 
package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Beta;

/**
 * Represents an <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >AND</a> condition in a condition expression.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying conditions.
 */
@Beta
public final class AndCondition extends Condition {
    private final Condition lhs;
    private final Condition rhs;

    AndCondition(Condition lhs, Condition rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return lhs.asSubstituted(context) + " AND "
                + rhs.asSubstituted(context);
    }

    @Override
    boolean atomic() {
        return lhs.atomic() && rhs.atomic();
    }

    @Override
    int precedence() {
        return Precedence.AND.value();
    }
}
