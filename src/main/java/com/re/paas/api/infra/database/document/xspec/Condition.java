
 package com.re.paas.api.infra.database.document.xspec;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.__;

import com.amazonaws.annotation.Beta;

/**
 * Represents a condition for building condition expression.
 */
@Beta
public abstract class Condition extends UnitOfExpression {
    /**
     * Returns a new condition based on the negation of the current condition.
     */
    public final NegationCondition negate() {
        return new NegationCondition(this);
    }

    /**
     * Returns a new condition based on the conjunction of the current condition
     * and the given condition.
     * 
     * @param that given condition.
     */
    public AndCondition and(Condition that) {
        return new AndCondition(this, that.atomic() ? that : __(that));
    }

    /**
     * Returns a new condition based on the disjunction of the current condition
     * and the given condition. 
     * 
     * @param that given condition.
     */
    public OrCondition or(Condition that) {
        return new OrCondition(this, that.atomic() ? that : __(that));
    }

    /**
     * A condition is considered "atomic" if appending an additional AND condition
     * would not alter the evaluation order of the original condition;
     * false otherwise.  For example, "a == b AND c == d" is atomic, but
     * "a == b OR c == d" is not.
     */
    abstract boolean atomic();
    
    /**
     * Returns the precedence of this condition.
     * See http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference
     */
    abstract int precedence();
}
