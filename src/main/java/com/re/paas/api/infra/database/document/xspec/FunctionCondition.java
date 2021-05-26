
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >Function</a> condition in building condition expression.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 * function ::=
 *    attribute_exists (path)
 *  | attribute_not_exists (path)
 *  | begins_with (path, operand)
 *  | contains (path, operand)
 * </pre>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying operand.
 */
public final class FunctionCondition extends Condition {
    private final String functionId;
    private final PathOperand pathOperand;
    private final Operand operand;

    FunctionCondition(String functionId, PathOperand attribute) {
        this.functionId = functionId;
        this.pathOperand = attribute;
        this.operand = null;
    }

    FunctionCondition(String functionId, PathOperand attribute, Operand operand) {
        this.functionId = functionId;
        this.pathOperand = attribute;
        this.operand = operand;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        StringBuilder sb = new StringBuilder(functionId).append("(").append(
                pathOperand.asSubstituted(context));
        if (operand != null)
            sb.append(", ").append(operand.asSubstituted(context));
        return sb.append(")").toString();
    }

    String getFunctionId() {
        return functionId;
    }

    PathOperand getPathOperand() {
        return pathOperand;
    }

    Operand getOperand() {
        return operand;
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.Function.value();
    }
}
