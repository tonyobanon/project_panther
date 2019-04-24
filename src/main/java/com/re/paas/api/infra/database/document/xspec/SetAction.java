
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >SET</a> action in the SET section of an update expression.
 * <p>
 * A SET action is used to add one or more attributes and values to an item. If
 * any of these attribute already exist, they are replaced by the new values.
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying value (of
 * type <code>UnitOfExpression</code>) given during construction.
 */
public final class SetAction extends UpdateAction {
    SetAction(PathOperand attr, UnitOfExpression value) {
        super("SET", attr, value);
    }

    /**
     * Returns the operand for this update expression as a string, substituted
     * if necessary with tokens using the given substitution context.
     */
    @Override
    String asSubstituted(SubstitutionContext context) {
        return new StringBuilder(getPathOperand().asSubstituted(context))
                .append(" = ").append(getValue().asSubstituted(context))
                .toString();
    }
}
