
 package com.re.paas.api.infra.database.document.xspec;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.__;

import com.amazonaws.annotation.Beta;

/**
 * Represents an <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >OR</a> condition in building condition expressions.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying conditions.
 */
@Beta
public final class OrCondition extends Condition {
    private final Condition lhs;
    private final Condition rhs;

    OrCondition(Condition lhs, Condition rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return lhs.asSubstituted(context) + " OR " + rhs.asSubstituted(context);
    }

    @Override
    public AndCondition and(Condition that) {
        return new AndCondition(__(this), that.atomic() ? that : __(that));
    }

    @Override
    boolean atomic() {
        return false;
    }

    @Override
    int precedence() {
        return Precedence.OR.value();
    }
}
