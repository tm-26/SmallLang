package Nodes;


public class RtrnStatementNode implements Visitable {

    ExpressionNode expression;
    public RtrnStatementNode(ExpressionNode expression){

        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
