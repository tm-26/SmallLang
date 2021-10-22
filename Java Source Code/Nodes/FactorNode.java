package Nodes;

import java.util.ArrayList;
import java.util.Arrays;

public class FactorNode implements Visitable {
    String type;
    Object value;


    public FactorNode(String type, Object value){
        this.type = type;
        this.value = value;
    }

    public String getLiteralType() {
        return ((LiteralNode) value).getType();
    }

    public Object getValue() {
        return value;
    }

    public Object getLiteralValue() {
        if(value instanceof LiteralNode){
            return ((LiteralNode) value).getValue();
        } else if(value instanceof UnaryNode) {
            return ((UnaryNode) value).getValues();
        } else if(value instanceof SubExpressionNode){
            return ((SubExpressionNode) value).getExpression().getLiteralValues();
        } else if(value instanceof FunctionCallNode){
            ArrayList<Object> values = new ArrayList<>();
            values.add(((FunctionCallNode) value).getIdentifier() + " call");
            values.addAll(Arrays.asList(((FunctionCallNode) value).getValues()));
            return values;
        }
            return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
