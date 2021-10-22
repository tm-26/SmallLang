package Nodes;



public class LiteralNode {

    String type;
    Object value;

    public LiteralNode(String type, Object value){

        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
