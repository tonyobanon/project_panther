
 package com.re.paas.api.infra.database.document.xspec;

import com.amazonaws.annotation.Immutable;

@Immutable
final class NamedElement extends PathElement {
    private final String name;

    NamedElement(String name) {
        if (name == null) {
            throw new NullPointerException("element");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("element cannot be empty");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NamedElement)) {
            return false;
        }

        return name.equals(((NamedElement) obj).name);
    }

    @Override
    String asNestedPath() {
        return "." + name;
    }

    @Override
    String asToken(SubstitutionContext context) {
        return context.nameTokenFor(name);
    }

    @Override
    String asNestedToken(SubstitutionContext context) {
        return "." + context.nameTokenFor(name);
    }
}
