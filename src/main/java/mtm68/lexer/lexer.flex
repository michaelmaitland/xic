package mtm68.lexer;

%%

%public
%class Lexer
%type Token
%function nextToken

%unicode
%pack
%line
%column

%{
    enum TokenType {
	IF,
	ID,
	INT,
	FLOAT,
	DOT
    }
    class Token {
	TokenType type;
	Object attribute;
	int lineNum;
	int column;
	Token(TokenType tt, Object attr, int lineNum, int column) {
	    type = tt; attribute = attr;
	    this.lineNum = lineNum;
	    this.column = column;
	}
	public String toString() {
	    return "" + type + "(" + attribute + ")";
	}
	
	public int getLineNum() {
		return lineNum;
	}

	public int getColumn() {
		return column;
	}
    }

	public int yyline() { return yyline + 1; }

	public int yycolumn() { return yycolumn + 1; }
	
	public Token createToken(TokenType tt, Object attribute) {
		return new Token(tt, attribute, yyline(), yycolumn());	
	}

	public Token createToken(TokenType tt) {
		return createToken(tt, null);	
	}
%}

Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Digit}|{Letter}|_)*
Integer = "0"|"-"?[1-9]{Digit}*
Float = {Digit}+ "." {Digit}+

%%

{Whitespace}  { /* ignore */ }
"if"          { return createToken(TokenType.IF); }
{Identifier}  { return createToken(TokenType.ID, yytext()); }
{Integer}     { return createToken(TokenType.INT,
				 Integer.parseInt(yytext())); }
{Float}       { return createToken(TokenType.FLOAT,
                                 Float.parseFloat(yytext())); }
"."           { return createToken(TokenType.DOT); }