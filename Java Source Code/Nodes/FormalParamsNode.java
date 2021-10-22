package Nodes;

public class FormalParamsNode {

    FormalParamNode[] parameters;

    public FormalParamsNode(FormalParamNode[] parameters){

        this.parameters = parameters;
    }

    public FormalParamNode[] getParameters() {
        return parameters;
    }

    public String[] getParameterTypes(){
        FormalParamNode[] parameters = this.parameters;
        String[] types = new String[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            types[i] = parameters[i].getType();
        }
        return types;
    }
}
