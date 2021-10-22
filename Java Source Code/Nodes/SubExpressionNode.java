package Nodes;

public class SubExpressionNode {

    ExpressionNode expression;

    public SubExpressionNode(ExpressionNode expression){

        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

}
