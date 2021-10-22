package Nodes;


public class VariableDeclNode implements Visitable {

    String identifier;
    String type;
    ExpressionNode expression;

    public VariableDeclNode(String identifier, String type, ExpressionNode expression){

        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
    }

    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setType(String type){
        this.type = type;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
