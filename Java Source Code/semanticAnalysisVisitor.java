import Nodes.*;

public class semanticAnalysisVisitor implements Visitor {

    // Variable Declaration
    static semanticAnalysisVisitor semanticAnalysisVisitor  = new semanticAnalysisVisitor();
    static symbolTable ST = new symbolTable();


    public void pass(ProgramNode program){
        program.accept(semanticAnalysisVisitor);
    }

    @Override
    public void visit(AssignmentNode term){

        if(!ST.lookUp(term.getIdentifier(), "", term.getExpression())){
            System.out.println("Error: Identifier " + term.getIdentifier() + " not declared in scope");
            System.exit(-5);
        }

    }

    @Override
    public void visit(BlockNode term) {
        ST.push();
        for(StatementNode statement: term.getStatements()){
            statement.accept(semanticAnalysisVisitor);
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
    public void visit(ForStatementNode term) {
        // Handle variable declaration
        if(term.getVariableDeclaration() != null){
            term.getVariableDeclaration().accept(semanticAnalysisVisitor);
        }

        // Handle expresion
        ST.lookUp("" , "bool", term.getExpression());

        // Handle assignment
        term.getAssignment().accept(semanticAnalysisVisitor);

        // Handle block
        term.getBlock().accept(semanticAnalysisVisitor);

    }

    @Override
    public void visit(FunctionDeclNode term) {
        // Handle identifier
        if(ST.lookUp(term.getIdentifier(), "F_" + term.getType(), new ExpressionNode(new Object[]{}))){
            System.out.println("Error: Function " + term.getIdentifier() + " already declared.");
            System.exit(-4);
        }
        ST.insert(term.getIdentifier(), "F_" + term.getType());

        // Handle parameters
        for(FormalParamNode formalParam: term.getParameters()){
            if(ST.lookUp(formalParam.getIdentifier(), formalParam.getType(), new ExpressionNode(new Object[]{}))){
                System.out.println("Error: Variable " + formalParam.getIdentifier() + " already declared.");
                System.exit(-3);
            }
            ST.insert(formalParam.getIdentifier(), formalParam.getType());
        }

        // Handle block
        term.getBlock().accept(semanticAnalysisVisitor);

    }

    @Override
    public void visit(IfStatementNode term) {
        // Handle expresion
        ST.lookUp("" , "bool", term.getExpression());

        // Handle first block
        term.getFirstBlock().accept(semanticAnalysisVisitor);

        // Handle second block
        if(term.getSecondBlock()!= null){
            term.getSecondBlock().accept(semanticAnalysisVisitor);
        }
    }

    @Override
    public void visit(PrintStatementNode term) {
        ST.validateExpression(term.getExpression().getLiteralValues());
        }

    @Override
    public void visit(ProgramNode program) {
        // Add global scope
        ST.push();

        for(StatementNode statement: program.getStatements()){
            statement.accept(semanticAnalysisVisitor);
        }

        // Remove global scope
        ST.pop();
    }

    @Override
    public void visit(RtrnStatementNode term) {
        ST.lookUp("", "return", term.getExpression());
    }

    @Override
    public void visit(SimpleExpressionNode simpleExpression) {

    }

    @Override
    public void visit(StatementNode statement) {
        switch (statement.getStatementType()) {
            case "<VariableDecl>":
                ((VariableDeclNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<Assignment>":
                ((AssignmentNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<IfStatement>":
                ((IfStatementNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<ForStatement>":
                ((ForStatementNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<WhileStatement>":
                ((WhileStatementNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<RtrnStatement>":
                ((RtrnStatementNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<FunctionDecl>":
                ((FunctionDeclNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<Block>":
                ((BlockNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
            case "<PrintStatement>":
                ((PrintStatementNode) statement.getStatement()).accept(semanticAnalysisVisitor);
                break;
        }
    }

    @Override
    public void visit(VariableDeclNode variableDeclNode) {

        if(ST.lookUp(variableDeclNode.getIdentifier(), variableDeclNode.getType(), variableDeclNode.getExpression())){
            System.out.println("Error: Variable " + variableDeclNode.getIdentifier() + " already declared.");
            System.exit(-3);
        }

        if(variableDeclNode.getType().equals("auto")){
            variableDeclNode.setType(ST.handleAuto(variableDeclNode.getExpression()));
            //System.out.println((ST.handleAuto(variableDeclNode.getExpression())));
        }

        ST.insert(variableDeclNode.getIdentifier(), variableDeclNode.getType());

    }

    @Override
    public void visit(TermNode term) {

    }

    @Override
    public void visit(WhileStatementNode term) {
        //Handle expression
        ST.lookUp("", "bool", term.getExpression());

        //Handle block
        term.getBlock().accept(semanticAnalysisVisitor);
    }
}
