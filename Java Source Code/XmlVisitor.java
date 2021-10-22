import Nodes.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

class XmlVisitor implements Visitor {

    // Variable Declaration
    static XmlVisitor xmlVisitor = new XmlVisitor();
    static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder docBuilder;
    static Element currentRoot;

    static {
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    static Document doc = docBuilder.newDocument();


    @Override
    public void visit(ProgramNode program) {
        Element rootElement = doc.createElement("Program");
        doc.appendChild(rootElement);
        for(StatementNode statement: program.getStatements()){
            currentRoot = rootElement;
            generateXMLNodes(new Object[]{"<Statement>", statement});
        }
    }

    @Override
    public void visit(VariableDeclNode variableDeclNode) {
        Element variableDeclElement = doc.createElement("Decl");
        currentRoot.appendChild(variableDeclElement);
        currentRoot = variableDeclElement;
        Element varTypeElement = doc.createElement("Var");
        variableDeclElement.appendChild(varTypeElement);
        varTypeElement.setAttribute("Type", variableDeclNode.getType());
        varTypeElement.appendChild(doc.createTextNode(variableDeclNode.getIdentifier()));
        variableDeclNode.getExpression().accept(xmlVisitor);
    }

    @Override
    public void visit(AssignmentNode assignment) {
        Element assignmentElement = doc.createElement("Assignment");
        currentRoot.appendChild(assignmentElement);
        Element identifierElement = doc.createElement("Identifier");
        assignmentElement.appendChild(identifierElement);
        assignmentElement.appendChild(doc.createTextNode(assignment.getIdentifier()));
        assignment.getExpression().accept(xmlVisitor);
        currentRoot = assignmentElement;
        assignment.getExpression().accept(xmlVisitor);
    }

    @Override
    public void visit(FactorNode term) {
        generateXMLNodes(new Object[]{"<Factor>", term});
    }

    @Override
    public void visit(PrintStatementNode printStatement) {
        Element printElement = doc.createElement("Print");
        currentRoot.appendChild(printElement);
        currentRoot = printElement;
        printStatement.getExpression().accept(xmlVisitor);
    }

    @Override
    public void visit(IfStatementNode ifStatement) {
        Element ifElement = doc.createElement("If");
        currentRoot.appendChild(ifElement);
        Element element = doc.createElement("Condition");
        ifElement.appendChild(element);
        currentRoot = ifElement;
        ifStatement.getExpression().accept(xmlVisitor);
        currentRoot = ifElement;
        ifStatement.getFirstBlock().accept(xmlVisitor);
        if(ifStatement.getSecondBlock() != null){
            element = doc.createElement("Else");
            ifElement.appendChild(element);
            currentRoot = element;
            ifStatement.getSecondBlock().accept(xmlVisitor);
        }
    }

    @Override
    public void visit(BlockNode block) {
        Element blockElement = doc.createElement("Block");
        currentRoot.appendChild(blockElement);
        for(StatementNode statement: block.getStatements()){
            currentRoot = blockElement;
            generateXMLNodes(new Object[]{"<Statement>", statement});
        }
    }

    @Override
    public void visit(ForStatementNode forStatement) {
        Element forElement = doc.createElement("ForLoop");
        currentRoot.appendChild(forElement);
        currentRoot = forElement;
        forStatement.getVariableDeclaration().accept(xmlVisitor);
        forStatement.getExpression().accept(xmlVisitor);
        currentRoot = forElement;
        forStatement.getAssignment().accept(xmlVisitor);
        currentRoot = forElement;
        forStatement.getBlock().accept(xmlVisitor);
    }

    @Override
    public void visit(WhileStatementNode whileStatement) {
        Element whileElement = doc.createElement("WhileLoop");
        currentRoot.appendChild(whileElement);
        currentRoot = whileElement;
        whileStatement.getExpression().accept(xmlVisitor);
        currentRoot = whileElement;
        whileStatement.getBlock().accept(xmlVisitor);
    }

    @Override
    public void visit(RtrnStatementNode rtrnStatement) {
        Element rtrnElement = doc.createElement("Return");
        currentRoot.appendChild(rtrnElement);
        currentRoot = rtrnElement;
        rtrnStatement.getExpression().accept(xmlVisitor);
    }

    @Override
    public void visit(FunctionDeclNode functionDecl) {
        Element element = doc.createElement("Function");
        currentRoot.appendChild(element);
        element.setAttribute("Type", functionDecl.getType());
        element.appendChild(doc.createElement("Identifier").appendChild(doc.createTextNode(functionDecl.getIdentifier())));
        Element parametersElement = doc.createElement("Parameters");
        element.appendChild(parametersElement);
        for(FormalParamNode parameter: functionDecl.getParameters()){
            element = doc.createElement("Parameter");
            parametersElement.appendChild(element);
            element.setAttribute("Type", parameter.getType());
            element.appendChild(doc.createTextNode(parameter.getIdentifier()));
        }
        functionDecl.getBlock().accept(xmlVisitor);

    }

    @Override
    public void visit(ExpressionNode expression) {
        handleSubNodes(expression.getSimpleExpressions(), "RelationalNode");

    }

    @Override
    public void visit(SimpleExpressionNode simpleExpression) {
        handleSubNodes(simpleExpression.getValues(), "BinExprNode");
    }

    @Override
    public void visit(StatementNode statement) {

    }

    @Override
    public void visit(TermNode termNode) {
        handleSubNodes(termNode.getValues(), "BinExprNode");
    }

    public void handleSubNodes(Object[] node, String nodeType){
        if(node.length == 1) {
            if (nodeType.equals("RelationalNode")) {
                ((SimpleExpressionNode) node[0]).accept(xmlVisitor);
            } else if (nodeType.equals("BinExprNode")) {
                try{
                    ((TermNode) node[0]).accept(xmlVisitor);
                }
                catch (ClassCastException e){
                    ((FactorNode) node[0]).accept(xmlVisitor);
                }
            }
        }
        boolean first = true;
        for(int i = 0; i < node.length; i++){
            if(node[i] instanceof String){
                Element operatorElement = doc.createElement(nodeType);
                currentRoot.appendChild(operatorElement);
                currentRoot = operatorElement;
                currentRoot.setAttribute("Op", (String) node[i]);

                if(first){
                    if(node[i-1] instanceof FactorNode){
                        generateXMLNodes(new Object[]{"<Factor>", node[i-1]});
                        first = false;
                    } else if (node[i-1] instanceof TermNode){
                        generateXMLNodes(new Object[]{"<Term>", node[i-1]});
                        first = false;
                    } else if (node[i-1] instanceof SimpleExpressionNode){
                        generateXMLNodes(new Object[]{"<SimpleExpression>", node[i-1]});
                        first = false;
                    }
                }
                if(node[i-1] instanceof FactorNode){
                    generateXMLNodes(new Object[]{"<Factor>", node[i+1]});
                } else if (node[i-1] instanceof TermNode){
                    generateXMLNodes(new Object[]{"<Term>", node[i+1]});
                } else if (node[i-1] instanceof SimpleExpressionNode){
                    generateXMLNodes(new Object[]{"<SimpleExpression>", node[i+1]});
                }
            }
        }
    }

    public void outputXMLFile(ProgramNode program) throws TransformerException {

        program.accept(xmlVisitor);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("../xmlRepresentation.xml"));
        transformer.transform(source, result);

        // Uncomment these lines to see xml printed out in console
        // StreamResult consoleResult = new StreamResult(System.out);
        // transformer.transform(source, consoleResult);

    }

    public void generateXMLNodes(Object[] token){
        switch ((String) token[0]){
            case "<Statement>":
                StatementNode statement = (StatementNode) token[1];
                switch (statement.getStatementType()) {
                    case "<VariableDecl>":
                        ((VariableDeclNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<Assignment>":
                        ((AssignmentNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<PrintStatement>":
                        ((PrintStatementNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<IfStatement>":
                        ((IfStatementNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<ForStatement>":
                        ((ForStatementNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<WhileStatement>":
                        ((WhileStatementNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<RtrnStatement>":
                        ((RtrnStatementNode) statement.getStatement()).accept(xmlVisitor);
                        break;
                    case "<FunctionDecl>":
                        ((FunctionDeclNode) statement.getStatement()).accept(xmlVisitor);
                        break;

                }
                return;
            case "<Factor>":
                FactorNode tempFactor = (FactorNode) token[1];
                switch (tempFactor.getType()) {
                    case "<Identifier>":
                        currentRoot.appendChild(doc.createTextNode((String) tempFactor.getLiteralValue()));
                        return;
                    case "<SubExpression>":
                        Element tempRoot = currentRoot;
                        Element subExpressionElement = doc.createElement("SubExpression");
                        currentRoot.appendChild(subExpressionElement);
                        currentRoot = subExpressionElement;
                        ((SubExpressionNode) tempFactor.getValue()).getExpression().accept(xmlVisitor);
                        currentRoot = tempRoot;
                        return;
                    case "<Unary>":
                        Element tempRoot1 = currentRoot;
                        Element unaryElement = doc.createElement("Unary");
                        currentRoot.appendChild(unaryElement);
                        currentRoot = unaryElement;
                        ((UnaryNode) tempFactor.getValue()).getExpression().accept(xmlVisitor);
                        currentRoot = tempRoot1;
                        return;
                    case "<FunctionCall>":
                        FunctionCallNode tempFunctionCall = (FunctionCallNode) tempFactor.getValue();
                        Element functionCallElement = doc.createElement("FunctionCall");
                        currentRoot.appendChild(functionCallElement);
                        functionCallElement.appendChild(doc.createTextNode(tempFunctionCall.getIdentifier()));
                        Element parametersElement = doc.createElement("Parameters");
                        functionCallElement.appendChild(parametersElement);
                        currentRoot = functionCallElement;
                        for(Object parameters : tempFunctionCall.getActualParams().getValues()){
                            if(!(parameters instanceof String)){
                                ((ExpressionNode) parameters).accept(xmlVisitor);
                            }
                        }
                        return;
                    default:
                        currentRoot.appendChild(getType(tempFactor.getLiteralType())).appendChild(doc.createTextNode((String) tempFactor.getLiteralValue()));
                        return;
                }
            case "<Term>":
                handleSubNodes(((TermNode) token[1]).getValues(), "BinExprNode");
                return;
            case "<SimpleExpression>":
                handleSubNodes(((SimpleExpressionNode) token[1]).getValues(), "BinExprNode");
        }
    }

    public Element getType(String type){
        Element typeElement = null;
        switch (type) {
            case "<FloatLiteral>":
                typeElement = doc.createElement("FloatConst");
                break;
            case "<BooleanLiteral>":
                typeElement = doc.createElement("BooleanConst");
                break;
            case "<IntegerLiteral>":
                typeElement = doc.createElement("IntegerConst");
                break;
        }
        return typeElement;
    }
}
