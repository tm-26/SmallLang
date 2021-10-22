package Nodes;


public class FunctionDeclNode implements Visitable {

    String identifier;
    FormalParamsNode parameters;
    String type;
    BlockNode block;

    public FunctionDeclNode(String identifier, FormalParamsNode parameters, String type, BlockNode block){

        this.identifier = identifier;
        this.parameters = parameters;
        this.type = type;
        this.block = block;
    }

    public String getIdentifier() {
        return identifier;
    }



    public FormalParamNode[] getParameters() {
        return parameters.getParameters();
    }

    public String getType() {
        return type;
    }

    public BlockNode getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }
}
