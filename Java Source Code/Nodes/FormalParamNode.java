package Nodes;

public class FormalParamNode {

    String identifier;
    String type;

    public FormalParamNode(String identifier, String type){
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }
}
