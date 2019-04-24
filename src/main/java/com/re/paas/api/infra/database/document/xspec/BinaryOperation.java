
 package com.re.paas.api.infra.database.document.xspec;

/**
 * Represents a binary operation in building expressions.
 */
abstract class BinaryOperation extends UnitOfExpression {
    private final Operand leftOperand;
    private final String operator;
    private final Operand rightOperand;

    BinaryOperation(Operand lhs, String operator, Operand rhs) {
        this.leftOperand = lhs;
        this.operator = operator;
        this.rightOperand = rhs;
    }

    final String asSubstituted(SubstitutionContext context) {
        return leftOperand.asSubstituted(context) + " " + operator + " "
                + rightOperand.asSubstituted(context);
    }

    Operand getLhs() {
        return leftOperand;
    }

    String getOperator() {
        return operator;
    }

    Operand getRhs() {
        return rightOperand;
    }
}
