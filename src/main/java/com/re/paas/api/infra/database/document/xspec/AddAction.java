 
package com.re.paas.api.infra.database.document.xspec;


/**
 * Represents an <a href=
 * "http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.Modifying.html"
 * >ADD</a> action in the ADD section of an update expression.
 * <p>
 * <h3>Important</h3>
 * The ADD action only supports Number and set data types. In general, DynamoDB
 * recommends using SET rather than ADD.
 * <p>
 * Use the ADD action in an update expression to do either of the following:
 * <ul>
 * <li>If the attribute does not already exist, add the new attribute and its
 * value(s) to the item.</li>
 * 
 * <li>If the attribute already exists, then the behavior of ADD depends on the
 * attribute's data type:
 * <ul>
 * <li>If the attribute is a number, and the value you are adding is also a number,
 * then the value is mathematically added to the existing attribute. (If the
 * value is a negative number, then it is subtracted from the existing
 * attribute.)</li>
 * 
 * <li>If the attribute is a set, and the value you are adding is also a set, then
 * the value is appended to the existing set.</li>
 * <ul>
 * </ul>
 * <p>
 * This object is as immutable (or unmodifiable) as the underlying value (of
 * type <code>UnitOfExpression</code>) given during construction.
 */
public final class AddAction extends UpdateAction {
    AddAction(PathOperand attr, UnitOfExpression value) {
        super("ADD", attr, value);
    }
}
