
 package com.re.paas.api.infra.database.document.xspec;

import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.__;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference">negation</a>
 * condition in building condition expressions.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 *    NOT condition
 * </pre>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying condition.
 */
public final class NegationCondition extends Condition {
    private final Condition condition;

    NegationCondition(Condition condition) { 
        this.condition = condition;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        if (this.precedence() > condition.precedence())
            return "NOT " + __(condition).asSubstituted(context);
        else
            return "NOT " + condition.asSubstituted(context);
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.NOT.value();
    }
}
