# SmallLang

The following rules describe the syntax of SmallLang in EBNF. Each rule has three parts: a left hand side (LHS), a right-hand side (RHS) and the '::==' symbol separating these two sides. The LHS names the EBNF rule whereas the RHS provides a description of this name. Note that the RHS uses four control forms namely sequence, choice, option and repetition. In a sequence order is important and items appear left-to-right. The stroke symbol (... | ...) is used to denote choice between alternatives. One item is chosen from this list; order is not important. Optional items are enclosed in square brackets ([...]) indicating that the item can either be included or discarded. Repeatable items are enclosed in curly brackets ({...}); the items within can be repeated zero or more times. For example, a Block consists of zero or more Statement enclosed in curly brackets.

\<Letter> ::== \[A-Za-z]
\<Digit> ::== \[0-9]
\<Type> ::== 'float' | 'int' | 'bool'
\<Auto> ::== 'auto'
\<BooleanLiteral> ::== 'true' | 'false'
\<IntegerLiteral> ::== \<Digit> {\<Digit>}
\<FloatLiteral> ::== \<Digit>{\<Digit>}'.'\<Digit>{\<Digit>}
\<Literal> ::== \<BooleanLiteral> | \<IntegerLiteral> | \<FloatLiteral>
\<Identifier> ::== ('\_' | \<Letter>) {'\_' | \<Letter> | \<Digit> }
\<MultiplicativeOp> ::== '*'' | '/'' | 'and'
\<AdditiveOp> ::== '+' | '-' | 'or'
\<RelationalOp> ::== '\<' | '>' | '\==' | '\<>' | '\<=' | '>='
\<ActualParams> ::== \<Expression> { ',' \<Expression> }
\<FunctionCall> ::== \<Identifier> '(' [\<ActualParams>] ')'
\<SubExpression> ::==  '(' \<Expression> ')'
\<Unary> ::== ('-'|'not') \<Expression>
\<Factor> ::== \<Literal> | \<Identifier> | \<FunctionCall> | \<SubExpression> | \<Unary>
\<Term> ::== \<Factor> {\<MultiplicativeOp> \<Factor>}
\<SimpleExpression> ::= \<Term> {\<AdditiveOp> \<Term>}
\<Expression> ::== \<SimpleExpression> {\<RelationalOp> \<SimpleExpression>}
\<Assignment> ::== \<Identifier> '=' \<Expression>
\<VariableDecl> ::== 'let' \<Identifier> ':' (\<Type> | \<Auto> ) '='' \<Expression>
\<PrintStatement> ::== 'print' \<Expression>
\<RtrnStatement> ::== 'return' \<Expression>
\<IfStatement> ::== 'if' '(' \<Expression> ')' \<Block> ['else' \<Block>]
\<ForStatement> ::== 'for' '(' [\<VariableDecl>] ';' \<Expression> ';' [\<Assignment> ] ')' \<Block>
\<WhileStatement> ::== 'while' '(' \<Expression> ')' \<Block>
\<FormalParam> ::== \<Identifier> ':' \<Type>
\<FormalParams> ::== \<FormalParam> { ',' \<FormalParam> }
\<FunctionDecl> ::== 'ff' \<Identifier> '('' [\<FormalParams>] ')' ':'' (\<Type> | \<Auto>) \<Block>
\<Statement> ::== \<VariableDecl> ';' | \<Assignment> ';' | \<PrintStatement> ';' | \<IfStatement> | \<ForStatement> | \<WhileStatement> | \<RtrnStatement> ';' | \<FunctionDecl>| \<Block>
\<Block> ::== '{' {\<Statement>} '}'
\<Program> ::== {\<Statement>}

## Using SmallLang
To use SmallLang java has to be installed on your machine.
To execute a SmallLang program use the command
```
java Main "path/to/SmallLangFile.txt"
```

If no argument is given the default.sl.txt file is read instead.

If the entered SmallLang.txt file has no syntax errors, an XML Representation of the entered code is generated in xmlRepresentation.xml

In the SmallLang example folder one can find a variety of example SmallLang programs.