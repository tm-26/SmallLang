package Nodes;

import java.util.ArrayList;
import java.util.Arrays;

public class UnaryNode {

    String value;
    ExpressionNode expression;

    public UnaryNode(String value, ExpressionNode expression){

        this.value = value;
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public String[] getValues(){
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        values.add(Arrays.toString(expression.getLiteralValues()));
        return values.toArray(new String[0]);
    }
}
