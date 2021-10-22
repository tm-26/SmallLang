package Nodes;

public class WhileStatementNode implements Visitable {

    ExpressionNode expression;
    BlockNode block;

    public WhileStatementNode(ExpressionNode expression, BlockNode block){
        this.expression = expression;
        this.block = block;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public BlockNode getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
