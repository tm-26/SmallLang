import Nodes.BlockNode;
import Nodes.ExpressionNode;
import Nodes.FunctionCallNode;

import java.util.ArrayList;
import java.util.Stack;

public class symbolTable implements myScripts.symbolTableInterface{
    static Stack<ArrayList<String[]>> programStack = new Stack<>();
    static ArrayList<Object[]> listOfFunctions = new ArrayList<>();
    final interpreterExecutionVisitor IEV = new interpreterExecutionVisitor();

    // Used to check if entered string is a float
    public boolean checkIfFloat(String input) {
        try {
            Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Used to check if entered string is an int
    public boolean checkIfInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Used to handle variables of type auto
    public String handleAuto(ExpressionNode expression) {
        return getType(expression.getLiteralValues());
    }

    public String getType(String[] values) {
        boolean isFloat = false;
        for (String value : values) {
            if (value.equals("true") || value.equals("false") || value.equals("<=") || value.equals(">=") || value.equals("==") || value.equals("and") || value.equals("or")) {
                return "bool";
            } else if (value.contains(".")) {
                isFloat = true;
            }
        }

        if (isFloat) {
            return "float";
        }

        return "int";
    }

    public void validateExpression(String[] values){
        boolean isBool = false;
        boolean containsNum = false;
        ArrayList<String> paramTypes = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            paramTypes.clear();
            switch (values[i]) {
                case ">=":
                case "<=":
                case "==":
                case "<>":
                case "<":
                case ">":
                case "(":
                case ")":
                    break;
                case "and":
                case "or":
                case "not":
                case "true":
                case "false":
                case "[true]":
                case "[false]":
                    isBool = true;
                    break;
                case "+":
                case "/":
                case "*":
                case "-":
                    containsNum = true;
                    break;

                default:

                    if (checkIfFloat(values[i]) || checkIfInt(values[i])) {
                        containsNum = true;
                    } else {
                        if (values[i].split(" ").length == 2) {
                            String identifier = values[i];
                            i++;
                            while (!values[i].equals("<EOP>")) {
                                validateExpression(new String[]{values[i]});
                                paramTypes.add(getType(new String[]{values[i]}));
                                i++;
                            }
                            if (identifier.contains(" call") && !lookUp(identifier.split(" ")[0], /*"F_" + type*/ "F_", /*myExpression.getActualParams()*/ paramTypes.toArray())) {
                                System.out.println("Error: Function " + identifier.split(" ")[0] + " not declared in scope");
                                System.exit(-5);
                            }
                        } else {
                            if (!lookUp(values[i], "", new ExpressionNode(new Object[]{}))) {
                                System.out.println("Error: Identifier " + values[i] + " not declared in scope");
                                System.exit(-5);
                            }
                        }
                    }
                    break;
            }
        }
        if (isBool && containsNum) {
            System.out.println("Error: Different data types in one expression");
            System.exit(-6);
        }
    }

    // Creates and enters a new scope
    public void push() {
        programStack.push(new ArrayList<>());
    }

    // Leave the current scope
    public void pop() {
        programStack.pop();
    }

    // Insert a variable to the current scope
    public void insert(String name, String value) {
        programStack.peek().add(new String[]{name, value});

    }

    public String getValue(String value){
        boolean isFunctionCall = false;
        String[] split = value.split(" ");
        if(split.length != 1){
            isFunctionCall = true;
        }
        value = split[0];
        for (int i = programStack.size() - 1; i >= 0; i--) {
            ArrayList<String[]> currentStack = programStack.get(i);
            for (int j = currentStack.size() - 1; j >= 0; j--) {
                String[] variable = currentStack.get(j);
                if (variable[0].equals(value)) {
                    if(isFunctionCall){
                            for(int k = 0; k <= listOfFunctions.size(); k++){
                                if(listOfFunctions.get(k)[0].equals(variable[0])){
                                    ((BlockNode) listOfFunctions.get(k)[1]).accept(IEV);
                                    return interpreterExecutionVisitor.rtrnValue;
                                }
                            }
                    } else {
                        return variable[1];
                    }

                }
            }
        }
        System.out.println("Error: Identifier " + value + " not declared in scope");
        System.exit(-5);
        return "";
    }

    // Returns True if variable already declared in current scope or variable type not equal to expression type
    public boolean lookUp(String name, String type, Object myValues) {

        boolean found = false;
        boolean hasExpression = true;
        String[] values = new String[0];
        ExpressionNode myExpression;
        boolean functionCall = false;

        if (myValues instanceof ExpressionNode) {
            myExpression = (ExpressionNode) myValues;
            if (myExpression.getLiteralValues().length != 0) {
                values = myExpression.getLiteralValues();
            } else {
                values = new String[]{};
                hasExpression = false;
            }
        } else {
            if (myValues instanceof FunctionCallNode) {
                try {
                    values = ((FunctionCallNode) myValues).getParameterTypes();
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            } else if (myValues instanceof Object[]) {
                values = new String[((Object[]) myValues).length];
                for (int i = 0; i < ((Object[]) myValues).length; i++) {
                    values[i] = (String) ((Object[]) myValues)[i];
                }
            } else {
                values = null;
            }

            myExpression = new ExpressionNode(new Object[]{});
            hasExpression = false;
        }


        // Check if variable is already declared in scope
        if (!name.equals("")) {
            outerLoop:
            for (ArrayList<String[]> variables : programStack) {
                for (String[] variable : variables) {
                    if (variable[0].equals(name)) {
                        found = true;
                        if (type.equals("")) {
                            type = variable[1];
                        } else if (!(variable[1].equals(type))) {

                            if (type.equals("F_")) {
                                if (variable[1].contains("F_")) {
                                    type = variable[1];
                                    functionCall = true;
                                }

                            } else if (!((variable[1].contains("F_") && !type.contains("F_")) || (!variable[1].contains("F_") && type.contains("F_")))) {
                                System.out.println("Error: Expected expression of type " + variable[1] + " but received expresion of type " + type + ".");
                                System.exit(-2);
                            } else {
                                found = false;
                            }
                        }
                        break outerLoop;
                    }
                }
            }
        }

        if (functionCall) {

            for (Object[] listOfFunction : listOfFunctions) {
                if (listOfFunction[0].equals(name)) {
                    // Check if function has the same amount of parameters as function call
                    String[] parameters = (String[]) listOfFunction[1];
                    if (parameters.length != values.length) {
                        System.out.println("Error: Expected " + parameters.length + " parameters but received " + values.length);
                        System.exit(-7);
                    }
                    // Check if all parameters are of the same type
                    for (int j = 0; j < parameters.length; j++) {
                        if (!parameters[j].equals(values[j])) {
                            System.out.println("Error: Expected parameter of type " + parameters[j] + " but received parameter of type " + values[j]);
                            System.exit(-2);
                        }
                    }
                    break;
                }
            }
        }
        // Check if variable type equal expression type
        if (hasExpression) {
            switch (type) {
                case "float":
                    for (String expression : values) {
                        // If not (float or numerical operator)
                        if (!(expression.equals("+") || expression.equals("/") || expression.equals("*") || expression.equals("-") || expression.equals("(") || expression.equals(")") || checkIfFloat(expression))) {
                            if (!lookUp(expression, "float", new ExpressionNode(new Object[]{}))) {
                                System.out.println("Error: Expected expression of type float but received \"" + expression + "\".");
                                System.exit(-2);
                            }
                        }
                    }
                    break;
                case "int":
                    for (String expression : values) {
                        // If not (int or numerical operator)
                        if (!(expression.equals("+") || expression.equals("/") || expression.equals("*") || expression.equals("-") || expression.equals("(")  || expression.equals(")") || checkIfInt(expression))) {
                            if (!lookUp(expression, "int", new ExpressionNode(new Object[]{}))) {
                                System.out.println("Error: Expected expression of type int but received \"" + expression + "\".");
                                System.exit(-2);
                            }
                        }
                    }
                    break;
                case "bool":
                    validateExpression(values);
                    break;

                case "return":
                    for (int i = programStack.size() - 1; 0 <= i; i--) {
                        ArrayList<String[]> scope = programStack.get(i);
                        for (int j = 0; j < scope.size(); j++) {
                            if (scope.get(j)[1].contains("F_")) {
                                if (scope.get(j)[1].equals("F_auto")) {
                                    scope.set(j, new String[]{scope.get(j)[0], handleAuto(myExpression)});
                                    return true;
                                }
                                return lookUp("", scope.get(j)[1].substring(2), myExpression);
                            }
                        }
                    }
            }
        }
        return found;
    }
}


