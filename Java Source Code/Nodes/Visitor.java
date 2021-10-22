package Nodes;//package com.company;

public interface Visitor{
    void visit(AssignmentNode term);
    void visit(BlockNode term);
    void visit(ExpressionNode expression);
    void visit(FactorNode term);
    void visit(ForStatementNode term);
    void visit(FunctionDeclNode term);
    void visit(IfStatementNode term);
    void visit(PrintStatementNode term);
    void visit(ProgramNode program);
    void visit(RtrnStatementNode term);
    void visit(SimpleExpressionNode simpleExpression);
    void visit(StatementNode statement);
    void visit(VariableDeclNode variableDeclNode);
    void visit(TermNode term);
    void visit(WhileStatementNode term);
}