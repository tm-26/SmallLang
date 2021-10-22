package Nodes;


public class SimpleExpressionNode implements Visitable {

    Object[] values;

    public SimpleExpressionNode(Object[] values){

        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }

}
