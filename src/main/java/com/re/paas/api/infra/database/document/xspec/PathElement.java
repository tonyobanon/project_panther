
 package com.re.paas.api.infra.database.document.xspec;

abstract class PathElement {
    abstract String asNestedPath();
    abstract String asToken(SubstitutionContext context);
    abstract String asNestedToken(SubstitutionContext context);
}
