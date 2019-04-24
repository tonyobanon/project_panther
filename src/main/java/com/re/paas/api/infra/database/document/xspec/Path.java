
 package com.re.paas.api.infra.database.document.xspec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.amazonaws.annotation.Immutable;

/**
 * Internal representation of a path to an attribute.
 */
@Immutable
final class Path extends UnitOfExpression {
    private final List<PathElement> elements;
    
    Path(String path) {
        this.elements = parse(path);
    }

    private List<PathElement> parse(String path) {
        if (path == null) {
            throw new NullPointerException("path");
        }

        final String[] split = path.split(Pattern.quote("."));
        final List<PathElement> elements = new ArrayList<PathElement>();

        for (String element : split) {
            int index = element.indexOf('[');
            if (index == -1) {
                elements.add(new NamedElement(element));
                continue;
            }

            if (index == 0) {
                throw new IllegalArgumentException("Bogus path: " + path);
            }

            elements.add(new NamedElement(element.substring(0, index)));

            do {
                element = element.substring(index + 1);
                index = element.indexOf(']');

                if (index == -1) {
                    throw new IllegalArgumentException("Bogus path: " + path);
                }

                int arrayIndex = Integer.parseInt(element.substring(0, index));
                elements.add(new ArrayIndexElement(arrayIndex));

                element = element.substring(index + 1);
                index = element.indexOf('[');

                if (index > 0)
                    throw new IllegalArgumentException("Bogus path: " + path);
            } while (index != -1);

            if (!element.isEmpty()) {
                throw new IllegalArgumentException("Bogus path: " + path);
            }
        }

        return elements;
    }

    @Override
    String asSubstituted(SubstitutionContext context) {
        StringBuffer sb = new StringBuffer();
        for (PathElement e: elements) {
            if (sb.length() == 0)
                sb.append(e.asToken(context));
            else {
                sb.append(e.asNestedToken(context));
            }
        }
        return sb.toString();
    }

    // Reverse to the original input path for debug purposes.
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (PathElement e: elements) {
            if (sb.length() == 0)
                sb.append(e.toString());
            else {
                sb.append(e.asNestedPath());
            }
        }
        return sb.toString();
    }

    List<PathElement> getElements() {
        return elements;
    }
}
