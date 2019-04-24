
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Unit of expression. A unit of expression is a building block for composing an
 * expression, such as update expression, condition (aka filter) expression, and
 * projection Expression.
 */
abstract class UnitOfExpression {
    /**
     * Returns this unit of expression as a string substituted if necessary with
     * tokens using the given substitution context.
     * 
     * @param context the substitution context which may get mutated as a side
     * effect upon completion of this method
     */
    abstract String asSubstituted(SubstitutionContext context);
}
