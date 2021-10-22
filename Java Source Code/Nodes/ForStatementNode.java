package Nodes;


public class ForStatementNode implements Visitable {

    VariableDeclNode variableDeclaration;
    ExpressionNode expression;
    AssignmentNode assignment;
    BlockNode block;

    public ForStatementNode(VariableDeclNode variableDeclaration, ExpressionNode expression, AssignmentNode assignment, BlockNode block){

        this.variableDeclaration = variableDeclaration;
        this.expression = expression;
        this.assignment = assignment;
        this.block = block;
    }

    public VariableDeclNode getVariableDeclaration() {
        return variableDeclaration;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public AssignmentNode getAssignment() {
        return assignment;
    }

    public BlockNode getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
