package com.re.paas.internal.databases.dynamodb.rules;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalProject;

import com.re.paas.internal.databases.dynamodb.rel.DynamoDBProject;
import com.re.paas.internal.databases.dynamodb.rel.DynamoDBRel;

public class DynamoDBProjectRule extends ConverterRule {
    public static final DynamoDBProjectRule INSTANCE =
            new DynamoDBProjectRule();

    private DynamoDBProjectRule() {
        super(LogicalProject.class, Convention.NONE, DynamoDBRel.CONVENTION, "DynamoDBProjectRule");
    }

    @Override
    public RelNode convert(RelNode rel) {
        final LogicalProject project = (LogicalProject) rel;
        final RelTraitSet traitSet = project.getTraitSet().replace(DynamoDBRel.CONVENTION);
        return new DynamoDBProject(project.getCluster(), traitSet,
                convert(project.getInput(), DynamoDBRel.CONVENTION), project.getProjects(),
                project.getRowType());
    }
}