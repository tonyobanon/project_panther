
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents the <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >list_append(operand, operand)</a> function in building expression.
 * <p>
 * "list_append (operand, operand) – This function evaluates to a list with a 
 * new element added to it. You can append the new element to the start or the 
 * end of the list by reversing the order of the operands."
 * <p>
 * This object is as immutable (or unmodifiable) as the values in it's operands.
 */
public final class ListAppendFunction extends FunctionOperand {
    private final Operand first;
    private final Operand second;

    ListAppendFunction(Operand first, Operand second) {
        this.first = first;
        this.second = second;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        return "list_append(" + first.asSubstituted(context) + ", "
                + second.asSubstituted(context) + ")";
    }
}
