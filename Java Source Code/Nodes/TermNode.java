package Nodes;


public class TermNode implements Visitable {

    Object[] values;

    public TermNode(Object[] values){

        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }


    public String getType(){
        return ((FactorNode) values[0]).getLiteralType();
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
