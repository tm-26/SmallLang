package Nodes;

import myScripts.symbolTableInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionCallNode {

    String identifier;
    ActualParamsNode actualParams;

    public FunctionCallNode(String identifier, ActualParamsNode actualParams){

        this.identifier = identifier;
        this.actualParams = actualParams;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ActualParamsNode getActualParams() {
        return actualParams;
    }

    public String[] getValues(){
        ArrayList<String> values = new ArrayList<>();
        for (Object actualParam : actualParams.getValues()) {
            if (actualParam instanceof ExpressionNode) {
                values.addAll(Arrays.asList(((ExpressionNode) actualParam).getLiteralValues()));
            } else {
                values.add((String) actualParam);
            }
        }
        return values.toArray(new String[0]);
    }



    public String[] getParameterTypes() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // Use reflection to create instance of symbolTable
        Class<?> STClass = Class.forName("symbolTable");
        symbolTableInterface STCall = (symbolTableInterface)STClass.newInstance();


        String[] values = getValues();


        ArrayList<String> types = new ArrayList<>();
        for(String value: values){
            if(STCall.checkIfInt(value)){
                types.add("int");
            } else if(STCall.checkIfFloat(value)){
                types.add("float");
            } else{
                switch (value) {
                    case ">=":
                    case "<=":
                    case "==":
                    case "<>":
                    case "<":
                    case ">":
                    case "and":
                    case "or":
                    case "not":
                    case "true":
                    case "false":
                        types.add("bool");
                        break;

                    default:
                        if (value.split(" ").length == 2) {
//                            if (value.contains(" call") && !symbolTable.lookUp(value.split(" ")[0], "F_" + type, myExpression.getActualParams())) {
//                                System.out.println("Error: Function " + value.split(" ")[0] + " not declared in scope");
//                                System.exit(-5);
//                            }
                        } else {
                            types.add(STCall.getValue(value));
                        }
                }
            }
        }

        return types.toArray(new String[0]);
    }
}
