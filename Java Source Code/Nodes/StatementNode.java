package Nodes;


public class StatementNode implements Visitable {

    String nodeType;
    Object statement;

    public StatementNode(String nodeType, Object statement){

        this.nodeType = nodeType;
        this.statement = statement;
    }

    public String getStatementType(){
        return this.nodeType;
    }

    public Object getStatement(){
        return this.statement;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
