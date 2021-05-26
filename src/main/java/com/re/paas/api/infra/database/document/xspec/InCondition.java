
 package com.re.paas.api.infra.database.document.xspec;

import java.util.List;

/**
 * Represents a <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference"
 * >IN</a> condition in building condition expression.
 * <p>
 * Underlying grammar:
 * 
 * <pre>
 *    operand IN ( operand (',' operand (, ...) ))
 * </pre>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying set of
 * operands.
 */
public final class InCondition extends Condition {
    private final PathOperand attribute;
    private final List<? extends Operand> operands;

    /**
     * @param operands assumed to be allocated on the stack so it will remain
     * externally unmodifiable
     */
    InCondition(PathOperand attribute, List<? extends Operand> operands) {
        this.attribute = attribute;
        this.operands = operands;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        StringBuilder sb = new StringBuilder(attribute.asSubstituted(context))
                .append(" IN (");
        boolean first = true;
        for (Operand operand : operands) {
            if (first) {
                sb.append(operand.asSubstituted(context));
                first = false;
            } else {
                sb.append(", ").append(operand.asSubstituted(context));
            }
        }
        return sb.append(")").toString();
    }

    @Override
    boolean atomic() {
        return true;
    }

    @Override
    int precedence() {
        return Precedence.IN.value();
    }
}
