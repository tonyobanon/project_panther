 
package com.re.paas.api.infra.database.document.xspec;

final class ArrayIndexElement extends PathElement {

    private final int index;

    public ArrayIndexElement(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "Invalid array index: " + index);
        }
        this.index = index;
    }

    @Override
    public String toString() {
        return "[" + index + "]";
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArrayIndexElement)) {
            return false;
        }

        return (index == ((ArrayIndexElement) obj).index);
    }

    @Override
    String asNestedPath() {
        return "[" + index + "]";
   }
   
    @Override
    String asToken(SubstitutionContext context) {
        throw new IllegalStateException();
    }

    @Override
    String asNestedToken(SubstitutionContext context) {
        return asNestedPath();
    }
}
