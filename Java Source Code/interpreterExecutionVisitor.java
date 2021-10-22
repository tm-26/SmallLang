import Nodes.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;

public class interpreterExecutionVisitor implements Visitor {


    // Variable Declaration
    static interpreterExecutionVisitor interpreterExecutionVisitor  = new interpreterExecutionVisitor();
    static symbolTable ST = new symbolTable();
    static ScriptEngineManager manager = new ScriptEngineManager();
    static ScriptEngine engine = manager.getEngineByName("javascript");
    static boolean firstPrint = true;
    static int functionCount = 0;
    static String rtrnValue;

    public void pass(ProgramNode program){
        program.accept(interpreterExecutionVisitor);
    }

    // Used to evaluate an expression
    public String eval(ExpressionNode term) {
         boolean isFunctionCall = false;

        // Assign function parameters
        for(Object factor: term.getFactors()){
            if(factor instanceof FactorNode && ((FactorNode) factor).getValue() instanceof FunctionCallNode){
                isFunctionCall = true;
                ArrayList<String> params = new ArrayList<>();
                FunctionCallNode functionCall = (FunctionCallNode) ((FactorNode) factor).getValue();
                params.add(functionCall.getIdentifier());
                for(int i = 0; i < functionCall.getActualParams().getValues().length; i++){
                    params.add(eval(((ExpressionNode) functionCall.getActualParams().getValues()[i])));
                }

                for(int i = symbolTable.programStack.size() - 1; i >= 0; i--){
                    // Find the function
                    if(symbolTable.programStack.get(i).get(0)[0].equals(functionCall.getIdentifier())){
                        // Set parameters
                        for(int j = 0; j < params.size(); j++){
                                if(j != 0) {
                                    symbolTable.programStack.get(i).set(j, new String[]{symbolTable.programStack.get(i).get(j)[0], params.get(j)});
                                }
                        }
                        break;
                    }
                }

            }
        }

        String[] values = term.getLiteralValues();


        for(int i = 0; i < values.length; i ++){

            switch (values[i]) {
                case ">=":
                case "<=":
                case "==":
                case "<>":
                case "<":
                case ">":
                case "+":
                case "/":
                case "*":
                case "-":
                case "true":
                case "false":
                case "(":
                case ")":
                    break;
                case "<EOP>":
                    values[i] = "";
                    break;
                case "[true]":
                    values[i] = "true";
                    break;
                case "[false]":
                    values[i] = "false";
                    break;

                case "and":
                    values[i] = "&&";
                    break;
                case "or":
                    values[i] = "||";
                    break;
                case "not":
                    values[i] = "";
                    if(values[i+1].equals("[true]")){
                        values[i+1] = "true";
                    } else if (values[i+1].equals("[false]")){
                        values[i+1] = "false";
                    }
                    values[i + 1] = "!" + values[i + 1];
                    break;
                default:

                    if(!(ST.checkIfInt(values[i]) || ST.checkIfFloat(values[i]) || values[i].contains("!"))){
                        values[i] = ST.getValue(values[i]);
                        if(isFunctionCall){
                            while(!values[i+1].equals("<EOP>")){
                                i++;
                                values[i] = "";
                            }
                            isFunctionCall = false;
                        }
                    }
            }
        }

        try {
            Object value = engine.eval(String.join(" ", values));
            if (value instanceof Integer){
                value = Integer.toString((Integer) value);
            } else if (value instanceof Double){
                value = Double.toString((Double) value);
            } else if (value instanceof Boolean){
                value = Boolean.toString((Boolean) value);
            }
            return (String) value;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void visit(AssignmentNode term) {
        // Find which scope identifier is located in
        out:
        for(int i = symbolTable.programStack.size() - 1; i >= 0; i--){
            ArrayList<String[]> tempStack = symbolTable.programStack.get(i);
            for(int j = tempStack.size() - 1; j >= 0; j--){
                if(tempStack.get(j)[0].equals(term.getIdentifier())){
                    symbolTable.programStack.get(i).add(new String[]{term.getIdentifier(), eval(term.getExpression())});
                    break out;
                }
            }
        }
    }

    @Override
    public void visit(BlockNode term){
        ST.push();
        for(StatementNode statement: term.getStatements()){
            statement.accept(interpreterExecutionVisitor);
        }
        ST.pop();
    }

    @Override
    public void visit(ExpressionNode expression) {

    }

    @Override
    public void visit(FactorNode term) {

    }

    @Override
    public void visit(ForStatementNode term){
        boolean firstLoop = true;

        // Handle variable declaration
        if(term.getVariableDeclaration() != null){
            term.getVariableDeclaration().accept(interpreterExecutionVisitor);
        }

        while(eval(term.getExpression()).equals("true")){
            // Handle block
            if(firstLoop){
                ST.push();
                firstLoop = false;
            }
            for(StatementNode statement: term.getBlock().getStatements()){
                statement.accept(interpreterExecutionVisitor);
            }

            // Handle assignment
            term.getAssignment().accept(interpreterExecutionVisitor);
        }
        if(!firstLoop){
            ST.pop();
        }
    }

    @Override
    public void visit(FunctionDeclNode term) {
        // Handle identifier
        ST.insert(term.getIdentifier(), "F_" + term.getType());

        // Handle parameters
        for(FormalParamNode formalParam: term.getParameters()){
            ST.insert(formalParam.getIdentifier(), formalParam.getType());
        }

        // Handle block
        symbolTable.listOfFunctions.set(functionCount, new Object[]{symbolTable.listOfFunctions.get(functionCount)[0], term.getBlock()});
        functionCount++;

    }

    @Override
    public void visit(IfStatementNode term){
        if(eval(term.getExpression()).equals("true")){
            // If true handle first block
            term.getFirstBlock().accept(interpreterExecutionVisitor);
        }
        else if(term.getSecondBlock() != null){
            // If second block exists, handle it
            term.getSecondBlock().accept(interpreterExecutionVisitor);

        }


    }

    @Override
    public void visit(PrintStatementNode myTerm) {
        // Clears the screen
        if(firstPrint){
            try {
                if (System.getProperty("os.name").contains("Windows")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    Runtime.getRuntime().exec("clear");
                }
            } catch (IOException | InterruptedException ignored) {}

            firstPrint = false;
        }

        System.out.println(eval(myTerm.getExpression()));

    }

    @Override
    public void visit(ProgramNode program){
        // Add global scope
        ST.push();

        for(StatementNode statement: program.getStatements()){
            statement.accept(interpreterExecutionVisitor);
        }

        // Remove global scope
        ST.pop();
    }

    @Override
    public void visit(RtrnStatementNode term) {
        rtrnValue = eval(term.getExpression());
    }

    @Override
    public void visit(SimpleExpressionNode simpleExpression) {

    }

    @Override
    public void visit(StatementNode statement){
        switch (statement.getStatementType()) {
            case "<PrintStatement>":
                ((PrintStatementNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<VariableDecl>":
                ((VariableDeclNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<Assignment>":
                ((AssignmentNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<IfStatement>":
                ((IfStatementNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<Block>":
                ((BlockNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<WhileStatement>":
                ((WhileStatementNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<ForStatement>":
                ((ForStatementNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<FunctionDecl>":
                ((FunctionDeclNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
            case "<RtrnStatement>":
                ((RtrnStatementNode) statement.getStatement()).accept(interpreterExecutionVisitor);
                break;
        }
    }

    @Override
    public void visit(VariableDeclNode variableDeclNode) {
        ST.insert(variableDeclNode.getIdentifier(), eval(variableDeclNode.getExpression()));
    }

    @Override
    public void visit(TermNode term) {

    }

    @Override
    public void visit(WhileStatementNode term){
        boolean firstLoop = true;
        while(eval(term.getExpression()).equals("true")){
            //Handle block
            if(firstLoop){
                ST.push();
                firstLoop = false;
            }

            for(StatementNode statement: term.getBlock().getStatements()){
                statement.accept(interpreterExecutionVisitor);
            }
        }
        if(!firstLoop){
            ST.pop();
        }
    }
}
