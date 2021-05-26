
 package com.re.paas.api.infra.database.document.xspec;

/**
 * An explicitly parenthesized condition, ie '(' condition ')', used in building
 * condition expressions.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying condition.
 */
public final class ParenthesizedCondition extends Condition {
    private final Condition condition;

    /**
     * Returns a parenthesized condition for the given condition if the given
     * condition is not already a parenthesized condition; or the original
     * condition otherwise.
     */
    public static ParenthesizedCondition getInstance(Condition condition) {
        return condition instanceof ParenthesizedCondition ? (ParenthesizedCondition) condition
                : new ParenthesizedCondition(condition);
    }

    private ParenthesizedCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return "(" + condition.asSubstituted(context) + ")";
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.Parentheses.value();
    }
}
