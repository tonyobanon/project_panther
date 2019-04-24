
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents an update action for building update expression.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying value (of
 * type <code>UnitOfExpression</code>) given during construction.
 */
public abstract class UpdateAction extends UnitOfExpression {
    private final String operator;

    private final PathOperand attribute;
    private final UnitOfExpression value;

    UpdateAction(String operator, PathOperand attribute, UnitOfExpression value) {
        this.operator = operator;
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * Returns the attribute as a string, substituted if necessary with tokens
     * using the given substitution context.
     */
    @Override
    String asSubstituted(SubstitutionContext context) {
        return value == null 
             ? attribute.asSubstituted(context)
             : attribute.asSubstituted(context) + " " + value.asSubstituted(context);
    }

    final String getOperator() {
        return operator;
    }

    final PathOperand getPathOperand() {
        return attribute;
    }

    final UnitOfExpression getValue() {
        return value;
    }
}
