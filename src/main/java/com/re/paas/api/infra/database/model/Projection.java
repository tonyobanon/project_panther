package com.re.paas.api.infra.database.model;


public class Projection {

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     */
    private String projectionType;
    /**
     * <p>
     * Represents the non-key attribute names which will be projected into the index.
     * </p>
     * <p>
     * For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the local
     * secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this counts
     * as two distinct attributes when determining the total.
     * </p>
     */
    private java.util.List<String> nonKeyAttributes;

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     * 
     * @param projectionType
     *        The set of attributes that are projected into the index:</p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
     *        projected attributes are in <code>NonKeyAttributes</code>.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ALL</code> - All of the table attributes are projected into the index.
     *        </p>
     *        </li>
     * @see ProjectionType
     */

    public void setProjectionType(String projectionType) {
        this.projectionType = projectionType;
    }

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     * 
     * @return The set of attributes that are projected into the index:</p>
     *         <ul>
     *         <li>
     *         <p>
     *         <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
     *         projected attributes are in <code>NonKeyAttributes</code>.
     *         </p>
     *         </li>
     *         <li>
     *         <p>
     *         <code>ALL</code> - All of the table attributes are projected into the index.
     *         </p>
     *         </li>
     * @see ProjectionType
     */

    public String getProjectionType() {
        return this.projectionType;
    }

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     * 
     * @param projectionType
     *        The set of attributes that are projected into the index:</p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
     *        projected attributes are in <code>NonKeyAttributes</code>.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ALL</code> - All of the table attributes are projected into the index.
     *        </p>
     *        </li>
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see ProjectionType
     */

    public Projection withProjectionType(String projectionType) {
        setProjectionType(projectionType);
        return this;
    }

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     * 
     * @param projectionType
     *        The set of attributes that are projected into the index:</p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
     *        projected attributes are in <code>NonKeyAttributes</code>.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ALL</code> - All of the table attributes are projected into the index.
     *        </p>
     *        </li>
     * @see ProjectionType
     */

    public void setProjectionType(ProjectionType projectionType) {
        withProjectionType(projectionType);
    }

    /**
     * <p>
     * The set of attributes that are projected into the index:
     * </p>
     * <ul>
     * <li>
     * <p>
     * <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of projected
     * attributes are in <code>NonKeyAttributes</code>.
     * </p>
     * </li>
     * <li>
     * <p>
     * <code>ALL</code> - All of the table attributes are projected into the index.
     * </p>
     * </li>
     * </ul>
     * 
     * @param projectionType
     *        The set of attributes that are projected into the index:</p>
     *        <ul>
     *        <li>
     *        <p>
     *        <code>KEYS_ONLY</code> - Only the index and primary keys are projected into the index.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>INCLUDE</code> - Only the specified table attributes are projected into the index. The list of
     *        projected attributes are in <code>NonKeyAttributes</code>.
     *        </p>
     *        </li>
     *        <li>
     *        <p>
     *        <code>ALL</code> - All of the table attributes are projected into the index.
     *        </p>
     *        </li>
     * @return Returns a reference to this object so that method calls can be chained together.
     * @see ProjectionType
     */

    public Projection withProjectionType(ProjectionType projectionType) {
        this.projectionType = projectionType.toString();
        return this;
    }

    /**
     * <p>
     * Represents the non-key attribute names which will be projected into the index.
     * </p>
     * <p>
     * For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the local
     * secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this counts
     * as two distinct attributes when determining the total.
     * </p>
     * 
     * @return Represents the non-key attribute names which will be projected into the index.</p>
     *         <p>
     *         For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the
     *         local secondary indexes, must not exceed 20. If you project the same attribute into two different
     *         indexes, this counts as two distinct attributes when determining the total.
     */

    public java.util.List<String> getNonKeyAttributes() {
        return nonKeyAttributes;
    }

    /**
     * <p>
     * Represents the non-key attribute names which will be projected into the index.
     * </p>
     * <p>
     * For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the local
     * secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this counts
     * as two distinct attributes when determining the total.
     * </p>
     * 
     * @param nonKeyAttributes
     *        Represents the non-key attribute names which will be projected into the index.</p>
     *        <p>
     *        For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the
     *        local secondary indexes, must not exceed 20. If you project the same attribute into two different indexes,
     *        this counts as two distinct attributes when determining the total.
     */

    public void setNonKeyAttributes(java.util.Collection<String> nonKeyAttributes) {
        if (nonKeyAttributes == null) {
            this.nonKeyAttributes = null;
            return;
        }

        this.nonKeyAttributes = new java.util.ArrayList<String>(nonKeyAttributes);
    }

    /**
     * <p>
     * Represents the non-key attribute names which will be projected into the index.
     * </p>
     * <p>
     * For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the local
     * secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this counts
     * as two distinct attributes when determining the total.
     * </p>
     * <p>
     * <b>NOTE:</b> This method appends the values to the existing list (if any). Use
     * {@link #setNonKeyAttributes(java.util.Collection)} or {@link #withNonKeyAttributes(java.util.Collection)} if you
     * want to override the existing values.
     * </p>
     * 
     * @param nonKeyAttributes
     *        Represents the non-key attribute names which will be projected into the index.</p>
     *        <p>
     *        For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the
     *        local secondary indexes, must not exceed 20. If you project the same attribute into two different indexes,
     *        this counts as two distinct attributes when determining the total.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public Projection withNonKeyAttributes(String... nonKeyAttributes) {
        if (this.nonKeyAttributes == null) {
            setNonKeyAttributes(new java.util.ArrayList<String>(nonKeyAttributes.length));
        }
        for (String ele : nonKeyAttributes) {
            this.nonKeyAttributes.add(ele);
        }
        return this;
    }

    /**
     * <p>
     * Represents the non-key attribute names which will be projected into the index.
     * </p>
     * <p>
     * For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the local
     * secondary indexes, must not exceed 20. If you project the same attribute into two different indexes, this counts
     * as two distinct attributes when determining the total.
     * </p>
     * 
     * @param nonKeyAttributes
     *        Represents the non-key attribute names which will be projected into the index.</p>
     *        <p>
     *        For local secondary indexes, the total count of <code>NonKeyAttributes</code> summed across all of the
     *        local secondary indexes, must not exceed 20. If you project the same attribute into two different indexes,
     *        this counts as two distinct attributes when determining the total.
     * @return Returns a reference to this object so that method calls can be chained together.
     */

    public Projection withNonKeyAttributes(java.util.Collection<String> nonKeyAttributes) {
        setNonKeyAttributes(nonKeyAttributes);
        return this;
    }

    /**
     * Returns a string representation of this object; useful for testing and debugging.
     *
     * @return A string representation of this object.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getProjectionType() != null)
            sb.append("ProjectionType: ").append(getProjectionType()).append(",");
        if (getNonKeyAttributes() != null)
            sb.append("NonKeyAttributes: ").append(getNonKeyAttributes());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof Projection == false)
            return false;
        Projection other = (Projection) obj;
        if (other.getProjectionType() == null ^ this.getProjectionType() == null)
            return false;
        if (other.getProjectionType() != null && other.getProjectionType().equals(this.getProjectionType()) == false)
            return false;
        if (other.getNonKeyAttributes() == null ^ this.getNonKeyAttributes() == null)
            return false;
        if (other.getNonKeyAttributes() != null && other.getNonKeyAttributes().equals(this.getNonKeyAttributes()) == false)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;

        hashCode = prime * hashCode + ((getProjectionType() == null) ? 0 : getProjectionType().hashCode());
        hashCode = prime * hashCode + ((getNonKeyAttributes() == null) ? 0 : getNonKeyAttributes().hashCode());
        return hashCode;
    }

}