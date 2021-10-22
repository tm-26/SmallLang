package Nodes;

public class AssignmentNode implements Visitable {
    String identifier;
    ExpressionNode expression;

    public AssignmentNode(String identifier, ExpressionNode expression){
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
