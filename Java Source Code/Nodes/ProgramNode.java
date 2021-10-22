package Nodes;


public class ProgramNode implements Visitable {

    StatementNode [] values;

    public ProgramNode(StatementNode[] values){

        this.values = values;
    }

    public StatementNode[] getStatements() {
        return values;
    }

    @Override
    public void accept(Visitor v)  {
         v.visit(this);
    }
}
