package mtm68.lexer;
import mtm68.util.StringUtils;

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

    public enum TokenType {
		// Reserved
		USE,
		IF,
		WHILE,
		ELSE,
		RETURN,
		LENGTH,
		INT_T,
		BOOL_T,
		TRUE,
		FALSE,

		// Constants
		ID,
		INT,
		STRING,
		
		// Punctuation
		DOT,
		OPEN_SQUARE,
		CLOSE_SQUARE,
		OPEN_PAREN,
		CLOSE_PAREN,
		OPEN_CURLY,
		CLOSE_CURLY,
		EXCLAMATION,
		COLON,
		SEMICOLON,
		COMMA,
		
		// Operators
		ADD,
		SUB,
		MULT,
		DIV,
		MOD,
		HIGH_MULT,
		LT,
		LEQ,
		GT,
		GEQ,
		EQ,
		NEQ,
		AND,
		OR;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
    }
    public static class Token {
		private TokenType type;
		private Object attribute;
		private int lineNum;
		private int column;
		public Token(TokenType tt, Object attr, int lineNum, int column) {
			type = tt; attribute = attr;
			this.lineNum = lineNum;
			this.column = column;
		}
		public String toString() {
			return lineNum + ":" + column + " " + type + " " + (attribute == null ? "" : attribute);
		}
		
		public int getLineNum() {
			return lineNum;
		}

		public int getColumn() {
			return column;
		}
    }

    StringBuffer string = new StringBuffer();

	public int yyline() { return yyline + 1; }

	public int yycolumn() { return yycolumn + 1; }
	
	public Token createToken(TokenType tt, Object attribute) {
		return new Token(tt, attribute, yyline(), yycolumn());	
	}

	public Token createToken(TokenType tt) {
		return createToken(tt, null);	
	}
%}

// TODO: How to parse UTF

Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Digit}|{Letter}|_)*
Integer = 0 | [1-9]{Digit}*
HexDigit = [0-9A-Fa-f]
Hex = "\\x" {HexDigit}{1,4}

%state STRING

%%

{Whitespace}  { /* ignore */ }

<YYINITIAL> {
    "use"         { return createToken(TokenType.USE); }
    "if"		  { return createToken(TokenType.IF); }
    "while"  	  { return createToken(TokenType.WHILE); }
    "else"		  { return createToken(TokenType.ELSE); }
    "return" 	  { return createToken(TokenType.RETURN); }
    "length"      { return createToken(TokenType.LENGTH); }
    "int"         { return createToken(TokenType.INT_T); }
    "bool"        { return createToken(TokenType.BOOL_T); }
    "true"        { return createToken(TokenType.TRUE); }
    "false"       { return createToken(TokenType.FALSE); }

    {Identifier}  { return createToken(TokenType.ID, yytext()); }
    {Integer}     { return createToken(TokenType.INT,
                     Long.parseLong(yytext())); }  // TODO: What to do if integer constant is too big?

    "."           { return createToken(TokenType.DOT); }

    \"            { string.setLength(0); yybegin(STRING); }
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return createToken(TokenType.STRING, string.toString()); }

    [^\n\'\\]+                      { string.append(yytext());}

      \\n                            { string.append('\n'); }
      \\\'                           { string.append('\''); }
      \\                             { string.append('\\'); }
      {Hex}                          { char hexChar = StringUtils.convertHexToChar(yytext());
                                       string.append(hexChar); }
}
