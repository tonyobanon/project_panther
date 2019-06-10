
 package com.re.paas.api.infra.database.document.xspec;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.re.paas.api.annotations.develop.Todo;

@Todo("Add support for minus and plus operations, using arbitrary time unit")
public final class D extends PathOperand {
    D(String path) { super(path); }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is equal to that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition eq(D that) {
        return new ComparatorCondition("=", this, that);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is not equal to that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition ne(D that) {
        return new ComparatorCondition("<>", this, that);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is less than or equal to the specified value) for building
     * condition expression.
     */
    public ComparatorCondition le(Date value) {
        return new ComparatorCondition("<=", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is less than or equal to that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition le(D that) {
        return new ComparatorCondition("<=", this, that);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is less than the specified value) for building
     * condition expression.
     */
    public ComparatorCondition lt(Date value) {
        return new ComparatorCondition("<", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is less than that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition lt(D that) {
        return new ComparatorCondition("<", this, that);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is greater than or equal to the specified value) for building
     * condition expression.
     */
    public ComparatorCondition ge(Date value) {
        return new ComparatorCondition(">=", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is greater than or equal to that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition ge(D that) {
        return new ComparatorCondition(">=", this, that);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is greater than the specified value) for building
     * condition expression.
     */
    public ComparatorCondition gt(Date value) {
        return new ComparatorCondition(">", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >comparator condition</a> (that evaluates to true if the value of the current
     * attribute is greater than that of the specified attribute) for building
     * condition expression.
     */
    public ComparatorCondition gt(D that) {
        return new ComparatorCondition(">", this, that);
    }

    /**
     * Returns a <code>BetweenCondition</code> that represents a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Comparators"
     * >BETWEEN comparison</a> (that evaluates to true if the value of the
     * current attribute is greater than or equal to the given low value, and
     * less than or equal to the given high value) for building condition
     * expression.
     */
    public BetweenCondition between(Date low, Date high) {
        return new BetweenCondition(this, 
                new LiteralOperand(low.getTime()),
                new LiteralOperand(high.getTime()));
    }


    /**
     * Returns an <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Functions"
     * >InCondition</a> (that evaluates to true if the value of the current
     * attribute is equal to any of the specified values) for building condition
     * expression.
     * 
     * @param values
     *            specified values. The number of values must be at least one
     *            and at most 100.
     */
    public final InCondition in(Date... values) {
        List<LiteralOperand> list = new LinkedList<LiteralOperand>();
        for (Date v: values)
            list.add(new LiteralOperand(v.getTime()));
        return new InCondition(this, list);
    }

    /**
     * Returns an <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Functions"
     * >InCondition</a> (that evaluates to true if the value of the current
     * attribute is equal to any of the values in the specified list) for
     * building condition expression.
     * 
     * @param values
     *            specified list of values. The number of values must be at
     *            least one and at most 100.
     */
    public final <T extends Date> InCondition in(List<T> values) {
        List<LiteralOperand> list = new LinkedList<LiteralOperand>();
        for (Date v: values)
            list.add(new LiteralOperand(v.getTime()));
        return new InCondition(this, list);
    }

    /**
     * Returns a <code>SetAction</code> object used for building update
     * expression. If the attribute referred to by this path operand doesn't
     * exist, the returned object represents adding the attribute value of the
     * specified source path operand to an item. If the current attribute
     * already exists, the returned object represents the value replacement of
     * the current attribute by the attribute value of the specified source path
     * operand.
     */
    public SetAction set(D source) {
        return new SetAction(this, source);
    }

    /**
     * Returns a <code>SetAction</code> object used for building update
     * expression. If the attribute referred to by this path operand doesn't
     * exist, the returned object represents adding the specified value as an
     * attribute to an item. If the attribute referred to by this path operand
     * already exists, the returned object represents the value replacement of
     * the current attribute by the specified value.
     */
    public SetAction set(Date value) {
        return new SetAction(this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <code>SetAction</code> object used for building update
     * expression. If the attribute referred to by this path operand doesn't
     * exist, the returned object represents adding the value of evaluating the
     * specified <code>IfNotExists</code> function as an attribute to an item.
     * If the attribute referred to by this path operand already exists, the
     * returned object represents the value replacement of the current attribute
     * by the value of evaluating the specified <code>IfNotExists</code>
     * function.
     */
    public SetAction set(IfNotExistsFunction<D> ifNotExistsFunction) {
        return new SetAction(this, ifNotExistsFunction);
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Functions"
     * >comparator condition</a> (that evaluates to true if the attribute value
     * referred to by this path operand is equal to the specified value) for
     * building condition expression.
     */
    public ComparatorCondition eq(Date value) {
        return new ComparatorCondition("=", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns a <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.SpecifyingConditions.html#ConditionExpressionReference.Functions"
     * >comparator condition</a> (that evaluates to true if the attribute value
     * referred to by this path operand is not equal to that of the specified
     * path operand) for building condition expression.
     */
    public ComparatorCondition ne(Date value) {
        return new ComparatorCondition("<>", this, new LiteralOperand(value.getTime()));
    }

    /**
     * Returns an <code>IfNotExists</code> object which represents an <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
     * >if_not_exists(path, operand)</a> function call where path refers to that
     * of the current path operand; used for building expressions.
     * 
     * <pre>
     * "if_not_exists (path, operand) – If the item does not contain an attribute 
     * at the specified path, then if_not_exists evaluates to operand; otherwise, 
     * it evaluates to path. You can use this function to avoid overwriting an 
     * attribute already present in the item."
     * </pre>
     * 
     * @param defaultValue
     *            the default value that will be used as the operand to the
     *            if_not_exists function call.
     */
    public IfNotExistsFunction<D> ifNotExists(Date defaultValue) {
        return new IfNotExistsFunction<D>(this, new LiteralOperand(defaultValue.getTime()));
    }

    /**
     * Returns an <code>IfNotExists</code> object which represents an <a href=
     * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
     * >if_not_exists(path, operand)</a> function call where path refers to that
     * of the current attribute; used for building expressions.
     * 
     * <pre>
     * "if_not_exists (path, operand) – If the item does not contain an attribute 
     * at the specified path, then if_not_exists evaluates to operand; otherwise, 
     * it evaluates to path. You can use this function to avoid overwriting an 
     * attribute already present in the item."
     * </pre>
     * 
     * @param defaultValue
     *            the default value that will be used as the operand to the
     *            if_not_exists function call.
     */
    public IfNotExistsFunction<D> ifNotExists(D defaultValue) {
        return ExpressionSpecBuilder.if_not_exists(this, defaultValue);
    }
}
