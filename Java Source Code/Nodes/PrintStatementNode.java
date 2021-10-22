package Nodes;


public class PrintStatementNode implements Visitable {

    ExpressionNode expression;

    public PrintStatementNode(ExpressionNode expression){

        this.expression = expression;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}
