package Nodes;

public class BlockNode implements Visitable {

    StatementNode[] statements;
    public BlockNode(StatementNode[] statements){

        this.statements = statements;
    }

    public StatementNode[] getStatements() {
        return statements;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
