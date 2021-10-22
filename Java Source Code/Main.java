/*TODO
Completed:
Handle comments
Replace ReplaceAll methods will something more efficient
Properly implement error codes
Error handle the "not" keyword
Handle the auto keyword
Error handle function calls
 */

import Nodes.*;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static Lexer lex;
    static Object[] nextToken = null;
    static boolean finishingExpression = false;
    static boolean inActualParams = false;
    static ArrayList<ExpressionNode> actualParams = new ArrayList<>();
    static List<String> specialCharacters = Arrays.asList(";", ")", "+", ":", ">", "<");

    public static void main(String[] args) throws TransformerException {

        try {
            // Table-driven lexer
            if(args.length != 0) {
                lex = new Lexer(args[0]);
            } else {
                lex = new Lexer("default.sl.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<StatementNode> tree = new ArrayList<>();
        Object[] token;
        ProgramNode program = null;
        try {
            // Hand-crafted LL(1) parser

            while (lex.count < lex.numberOfWords - 1) {
                token = lex.getNextToken();
                if (!token[0].equals("<EOF>")) {
                    tree.add((StatementNode) simplify(token)[1]);
                }
            }

            program = new ProgramNode(tree.toArray(new StatementNode[0]));

            // AST XML Generation Pass
            new XmlVisitor().outputXMLFile(program);
        } catch(Exception e){
            System.out.println("Error: The parser could not successfully read the input");
            System.exit(-98);
        }

        try {
            // Semantic Analysis Pass
            new semanticAnalysisVisitor().pass(program);

            // Interpreter Execution Pass
            new interpreterExecutionVisitor().pass(program);
        } catch (Exception e){
            System.out.println("Error: The Semantic analysis process could not be completed on the entered input.");
            System.exit(-97);
        }
    }


    public static Object[] simplify(Object[] token) {
        String identifier;
        ExpressionNode expression;
        switch ((String) token[0]) {
            case "let":
                token = lex.getNextToken();
                if (!token[0].equals("<Identifier>")) {
                    sendErrorMssg("Identifier");
                }
                identifier = (String) token[1];
                if (!lex.getNextToken()[0].equals(":")) {
                    sendErrorMssg(":");
                }
                token = lex.getNextToken();
                if (!(token[0].equals("<Type>") || token[0].equals("<Auto>"))) {
                    sendErrorMssg("Type");
                }
                String varType = (String) token[1];
                if (!(lex.getNextToken()[0].equals("="))) {
                    sendErrorMssg("=");
                }
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                if (!lex.getNextToken()[0].equals(";")) {
                    sendErrorMssg(";");
                }
                return new Object[]{"<Statement>", new StatementNode("<VariableDecl>", new VariableDeclNode(identifier, varType, (ExpressionNode) token[1]))};

            case "print":
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                if (!lex.getNextToken()[0].equals(";")) {
                    lex.count = lex.count -2;
                    if(!lex.getNextToken()[0].equals(";")) {
                        sendErrorMssg(";");
                    }
                }
                return new Object[]{"<Statement>", new StatementNode("<PrintStatement>", new PrintStatementNode((ExpressionNode) token[1]))};

            case "return":
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                if (!lex.getNextToken()[0].equals(";")) {
                    lex.count = lex.count - 3;
                    if (!lex.getNextToken()[0].equals(";")) {
                        sendErrorMssg(";");
                    }
                }
                return new Object[]{"<Statement>", new StatementNode("<RtrnStatement>", new RtrnStatementNode((ExpressionNode) token[1]))};
            case "if":
                if (!lex.getNextToken()[0].equals("(")) {
                    sendErrorMssg("(");
                }
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                expression = (ExpressionNode) token[1];
                if (!lex.getNextToken()[0].equals(")")) {
                    sendErrorMssg(")");
                }
                token = simplify(lex.getNextToken());
                nextToken = lex.getNextToken();
                if(nextToken[0].equals("else")){
                    BlockNode firstBlock = (BlockNode) token[1];
                    lex.getNextToken();
                    if(!lex.getNextToken()[0].equals("{")) {
                        sendErrorMssg("{");
                    }
                        token = simplify(new Object[]{"{", "{"});
                        return new Object[]{"<Statement>", new StatementNode("<IfStatement>", new IfStatementNode(expression, firstBlock, (BlockNode) token[1]))};

                } else {
                    return new Object[]{"<Statement>", new StatementNode("<IfStatement>", new IfStatementNode(expression, (BlockNode) token[1], null))};
                }

            case "for":
                if (!lex.getNextToken()[0].equals("(")) {
                    sendErrorMssg("(");
                }
                StatementNode tempStatement = (StatementNode) simplify(lex.getNextToken())[1];
                if(!tempStatement.getStatementType().equals("<VariableDecl>")) {
                    sendErrorMssg("Variable Declaration");
                }
                VariableDeclNode variableDeclaration = (VariableDeclNode) tempStatement.getStatement();
                token = simplify(lex.getNextToken());
                if(!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                expression = (ExpressionNode) token[1];
                if(!lex.getNextToken()[0].equals(";")) {
                    sendErrorMssg(";");
                }
                token = simplify(lex.getNextToken());
                if(!token[0].equals("<Assignment>")) {
                    sendErrorMssg("Assignment");
                }
                AssignmentNode assignment = (AssignmentNode) token[1];
                if(!lex.getNextToken()[0].equals(")")) {
                    sendErrorMssg(")");
                }
                if(!lex.getNextToken()[0].equals("{")) {
                    sendErrorMssg("{");
                }
                return new Object[]{"<Statement>", new StatementNode("<ForStatement>", new ForStatementNode(variableDeclaration, expression, assignment, (BlockNode) simplify(new Object[]{"{", "{"})[1]))};
            case "while":
                if (!lex.getNextToken()[0].equals("(")) {
                    sendErrorMssg("(");
                }
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                expression = (ExpressionNode) token[1];
                if (!lex.getNextToken()[0].equals(")")) {
                    sendErrorMssg(")");
                }
                if(!lex.getNextToken()[0].equals("{")) {
                    sendErrorMssg("{");
                }
                return new Object[]{"<Statement>", new StatementNode("<WhileStatement>", new WhileStatementNode(expression, (BlockNode) simplify(new Object[]{"{", "{"})[1]))};
            case "ff":
                token = lex.getNextToken();
                if (!token[0].equals("<Identifier>")) {
                    sendErrorMssg("Identifier");
                }
                identifier = (String) token[1];
                if (!lex.getNextToken()[0].equals("(")) {
                    sendErrorMssg("(");
                }
                token = simplify(lex.getNextToken());
                if(!token[0].equals("<FormalParams>")) {
                    sendErrorMssg("Parameters");
                }
                FormalParamsNode parameters = (FormalParamsNode) token[1];
                if (!lex.getNextToken()[0].equals(")")) {
                    sendErrorMssg(")");
                }
                if (!lex.getNextToken()[0].equals(":")) {
                    sendErrorMssg(":");
                }
                token = lex.getNextToken();
                if(!(token[0].equals("<Type>") || token[0].equals("<Auto>"))) {
                    sendErrorMssg("Type");
                }
                String type = (String) token[1];
                if(!lex.getNextToken()[0].equals("{")) {
                    sendErrorMssg("{");
                }
                symbolTable.listOfFunctions.add(new Object[]{identifier, parameters.getParameterTypes()});
                return new Object[]{"<Statement>", new StatementNode("<FunctionDecl>", new FunctionDeclNode(identifier, parameters, type, (BlockNode) simplify(new Object[]{"{", "{"})[1]))};

            case "<BooleanLiteral>":
            case "<IntegerLiteral>":
            case "<FloatLiteral>":
                return simplify(new Object[]{"<Literal>", new LiteralNode((String) token[0], token[1])});
            case "<Expression>":
                if(nextToken[0].equals(",")){
                    inActualParams = true;
                    actualParams.add((ExpressionNode) token[1]);
                    finishExpression(",", "<Expression>", token[1]);
                    inActualParams = false;
                    ExpressionNode[] tempParams = actualParams.toArray(new ExpressionNode[0]);
                    actualParams.clear();
                    return new Object[]{"<ActualParams>", new ActualParamsNode(tempParams)};
                }
                if(inActualParams){
                    actualParams.add((ExpressionNode) token[1]);
                }
                return token;
            case "<Identifier>":
                nextToken = lex.getNextToken();
                identifier = (String) token[1];
                switch ((String) nextToken[0]) {
                    case "(":
                        lex.getNextToken();
                        token = simplify(lex.getNextToken());
                        if(!token[1].equals(")")){
                            if(!(lex.getNextToken()[0].equals(")"))){
                                sendErrorMssg(")");
                            }
                        }
                        if(token[0].equals("<ActualParams>")){
                            ActualParamsNode actualParameters = (ActualParamsNode) token[1];
                            return simplify(new Object[]{"<Factor>", new FactorNode("<FunctionCall>", new FunctionCallNode(identifier, actualParameters))});
                        }
                        else if(token[0].equals("<Expression>")){
                            return simplify(new Object[]{"<Factor>", new FactorNode("<FunctionCall>", new FunctionCallNode(identifier, new ActualParamsNode(new Object[]{token[1]})))});
                        } else if (token[1].equals(")")){
                            return simplify(new Object[]{"<Factor>", new FactorNode("<FunctionCall>", new FunctionCallNode(identifier, new ActualParamsNode(new Object[]{})))});
                        } else{
                            sendErrorMssg("Parameters");
                        }
                    case "=":
                        lex.getNextToken();
                        token = simplify(lex.getNextToken());
                        if (token[0].equals("<Expression>")) {
                            nextToken = lex.getNextToken();
                            if (nextToken[0].equals(";")) {
                                lex.getNextToken();
                                return new Object[]{"<Statement>", new StatementNode("<Assignment>", new AssignmentNode(identifier, (ExpressionNode) token[1]))};
                            } else {
                                return new Object[]{"<Assignment>", new AssignmentNode(identifier, (ExpressionNode) token[1])};
                            }
                        }
                        break;
                    case ":":
                        lex.getNextToken();
                        token = lex.getNextToken();
                        if (token[0].equals("<Type>")) {
                            Object[] tempParameters = finishExpression(",", "<FormalParam>", new FormalParamNode(identifier, (String) token[1]));
                            FormalParamNode[] formalParameters = new FormalParamNode[tempParameters.length];
                            for(int i = 0; i < tempParameters.length; i++){
                                formalParameters[i] = (FormalParamNode) tempParameters[i];
                            }
                            return (new Object[]{"<FormalParams>", new FormalParamsNode(formalParameters)});
                        }
                        break;
                    default:
                        return simplify(new Object[]{"<Factor>", new FactorNode((String) token[0], token[1])});
                }
            case "(":
                token = simplify(lex.getNextToken());
                if (!token[0].equals("<Expression>")) {
                    sendErrorMssg("Expression");
                }
                expression = (ExpressionNode) token[1];
                Object temp = lex.getNextToken()[0];
                if(!temp.equals(")")){
                    lex.count--;
                    if(lex.getNextToken()[0].equals(")")) {
                        System.out.println(temp);
                        System.out.println("sendErrorMssg();");
                    }
                }

                return  simplify(new Object[]{"<SubExpression>", new SubExpressionNode(expression)});
            case "not":
                token = lex.getNextToken();
                if(!token[0].equals("<Expression>")){
                    sendErrorMssg("Expression");
                }
                return new Object[]{"<Unary>", new UnaryNode("not", (ExpressionNode) token[1])};
            case "<Literal>":
            case "<FunctionCall>":
            case "<SubExpression>":
                return simplify(new Object[]{"<Factor>", new FactorNode((String) token[0], token[1])});
            case "<Unary>":
                Object[] tempExpression = simplify(lex.getNextToken());
                if(!tempExpression[0].equals("<Expression>")){
                    sendErrorMssg("Expression");
                }
                //tempExpression = simplify(tempExpression);
                return simplify(new Object[]{"<Factor>", new FactorNode((String) token[0], new UnaryNode((String) token[1], (ExpressionNode) tempExpression[1]))});
            case "<Factor>":
                return simplify(new Object[]{"<Term>", new TermNode(finishExpression("<MultiplicativeOp>", "<Factor>", token[1]))});
            case "<Term>":
                return simplify(new Object[]{"<SimpleExpression>", new SimpleExpressionNode(finishExpression("<AdditiveOp>", "<Term>", token[1]))});
            case "<SimpleExpression>":
                return simplify(new Object[]{"<Expression>", new ExpressionNode(finishExpression("<RelationalOp>", "<SimpleExpression>", token[1]))});
            case "{":
                ArrayList<StatementNode> statements = new ArrayList<>();
                while (!(token = lex.getNextToken())[0].equals("}")) {
                    if (token[0].equals("<EOF>")) {
                        sendErrorMssg("}");
                    }
                    statements.add((StatementNode) simplify(token)[1]);
                }
                return new Object[]{"<Block>", new BlockNode(statements.toArray(new StatementNode[0]))};
        }
        // Handle Unary node that stats with '-'
        if(token[1].equals("-")){
            //lex.getNextToken();
            token = simplify(lex.getNextToken());
            if(!token[0].equals("<Expression>")){
                sendErrorMssg("Expression");
            }
            return simplify(new Object[]{"<Factor>", new FactorNode((String) token[0], new UnaryNode("-", (ExpressionNode) token[1]))});
        }
        return new String[]{(String) token[0], (String) token[1]};
    }


    public static Object[] finishExpression(String firstExpression, String secondExpression, Object token) {
        int counter = 1;
        ArrayList<Object> values = new ArrayList<>();
        values.add(token);
        Object[] tokens;
        boolean finishingCurrent = false;
        if (finishingExpression) {
            nextToken = null;
            finishingExpression = false;
        }
        while (true) {
            nextToken = lex.getNextToken();
            tokens = nextToken;
            if (tokens[0].equals(firstExpression)) {
                if (!(counter % 2 == 0)) {
                    values.add(tokens[1]);
                    nextToken = null;
                    finishingExpression = true;
                    finishingCurrent = true;
                } else {
                    System.out.println("Error: The parser could not successfully read the input");
                    System.exit(-98);
                }
            } else if (!specialCharacters.contains(tokens[1]) && finishingCurrent) {
                if(firstExpression.equals(",") && !inActualParams){
                    lex.getNextToken();
                }
                tokens = simplify(tokens);
                if (tokens[0].equals("<Expression>")) {
                    finishingCurrent = false;
                    ExpressionNode tempExpression = (ExpressionNode) tokens[1];
                    switch (secondExpression) {
                        case "<FormalParam>":
                            values.addAll(Arrays.asList((tempExpression.getLiteralValues())));
                            finishingCurrent = false;
                            values.add(tokens[1]);
                            break;
                        case "<SimpleExpression>":
                            values.addAll(Arrays.asList((tempExpression.getSimpleExpressions())));
                            break;
                        case "<Term>":
                            values.addAll(Arrays.asList(tempExpression.getTerms()));
                            break;
                        case "<Factor>":
                            values.addAll(Arrays.asList(tempExpression.getFactors()));
                            break;
                        default:
                            if(!nextToken[0].equals(")") && !nextToken[1].equals(")")){
                                nextToken = null;
                            }
                            break;
                    }

                } else if(tokens[0].equals("<FormalParams>")){
                    finishingCurrent = false;
                    values.addAll(Arrays.asList(((FormalParamsNode) tokens[1]).getParameters()));
                }else if(tokens[0].equals("<ActualParams>")){
                    return ((ActualParamsNode) tokens[1]).getValues();
                }
            } else{
                break;
            }
            counter++;
        }
        finishingExpression = false;
        if(secondExpression.equals("<FormalParam>")){
            ArrayList<FormalParamNode> parameters = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof FormalParamNode) {
                    parameters.add((FormalParamNode) value);
                }
            }
            return parameters.toArray();

        }
        return values.toArray();
    }

    public static void sendErrorMssg(String mssg){
        if(mssg.length() == 1){
            System.out.println("Error: Expected '" + mssg + "' in line " + lex.lineCounter);
        } else {
            System.out.println("Error: Expected " + mssg + " in line " + lex.lineCounter);
        }
        System.exit(-1);
    }
}