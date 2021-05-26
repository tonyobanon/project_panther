
 package com.re.paas.api.infra.database.document.xspec;

/**
 * <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >Precedence</a> of various Conditions.
 */
enum Precedence {
    Comparator(700),    // = <> < <= > >=
    IN(600),
    BETWEEN(500),
    Function(400),  // attribute_exists attribute_not_exists begins_with contains
    Parentheses(300),
    NOT(200),
    AND(100),
    OR(0),
    ;
    /**
     * The higher this value, the higher the precedence.
     */
    private final int value;

    private Precedence(int value) { this.value = value; }

    int value() {
        return value;
    }
}
