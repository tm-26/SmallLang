package Nodes;


public class IfStatementNode implements Visitable {

    ExpressionNode expression;
    BlockNode firstBlock;
    BlockNode secondBlock;

    public IfStatementNode(ExpressionNode expression, BlockNode firstBlock, BlockNode secondBlock){

        this.expression = expression;
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public BlockNode getFirstBlock() {
        return firstBlock;
    }

    public BlockNode getSecondBlock() {
        return secondBlock;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
