package mtm68.lexer;
import mtm68.util.StringUtils;
import mtm68.parser.sym;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

import java.math.BigInteger;
%%

%public
%class Lexer
%cup

%unicode
%pack
%line
%column

%eofval{
  return newToken(TokenType.EOF);
%eofval}

%{

    /** Data to manage string state */
    StringBuffer string = new StringBuffer();
    int stringCol, stringLine;

	public int yyline() { return yyline + 1; }

	public int yycolumn() { return yycolumn + 1; }

	TokenFactory tFactory = new TokenFactory();

    private Token newToken(TokenType type) {
      return tFactory.newToken(type, yyline(), yycolumn());
    }

    private Token newToken(TokenType type, Object value) {
      return tFactory.newToken(type, value, yyline(), yycolumn());
    }

    private Token newToken(TokenType type, Object value, int line, int column) {
		return tFactory.newToken(type, value, line, column);
	}
	
	private Token newIntegerToken(String toParse) {
		try {
         return newToken(TokenType.INTEGER, new BigInteger(toParse));
		} catch (NumberFormatException ex) {
		  return newToken(TokenType.error, "Couldn't convert to integer: " + toParse);	
		}
	}

%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Comment = "//" {InputCharacter}* {LineTerminator}?
Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = [0-9A-Fa-f]
Hex = "\\x" {HexDigit}{1,4}
Identifier = {Letter}({Digit}|{Letter}|_|"\'")*
Integer = 0 | [1-9]{Digit}* 
IntegerLiteral = "\'" {InputCharacter} "\'" 
HexLiteral = "\'" {Hex} "\'" 
EmptyCharLiteral = "\'\'" 
Newline = "\\n"
EscapedSingleQuote = "\\\'"
EscapedDoubleQuote = "\\\""
EscapedBackslash = "\\\\"
IllegalEscapedCharLiteral = "\'" !({Newline}|{EscapedSingleQuote}|{EscapedDoubleQuote}|{EscapedBackslash}|{Hex}) "\'"  
IllegalEscapedString = "\\" !("n"|"\'"|"\""|"\\"|({HexDigit}{1,4})) 

%state STRING

%%


<YYINITIAL> {
    {Whitespace}  { /* ignore */ }
    {Comment}     { /* ignore */ }

    "use"         { return newToken(TokenType.USE); }
    "if"		  { return newToken(TokenType.IF); }
    "while"  	  { return newToken(TokenType.WHILE); }
    "else"		  { return newToken(TokenType.ELSE); }
    "return" 	  { return newToken(TokenType.RETURN); }
    "length"      { return newToken(TokenType.LENGTH); }
    "int"         { return newToken(TokenType.INT); }
    "bool"        { return newToken(TokenType.BOOL); }
    "true"        { return newToken(TokenType.TRUE); }
    "false"       { return newToken(TokenType.FALSE); }

    "["           { return newToken(TokenType.OPEN_SQUARE); }
    "]"           { return newToken(TokenType.CLOSE_SQUARE); }
    "("           { return newToken(TokenType.OPEN_PAREN); }
    ")"           { return newToken(TokenType.CLOSE_PAREN); }
    "{"           { return newToken(TokenType.OPEN_CURLY); }
    "}"           { return newToken(TokenType.CLOSE_CURLY); }
    "!"           { return newToken(TokenType.EXCLAMATION); }
    ":"           { return newToken(TokenType.COLON); }
    ";"           { return newToken(TokenType.SEMICOLON); }
    ","           { return newToken(TokenType.COMMA); }
    "="           { return newToken(TokenType.EQ); }
    "_"           { return newToken(TokenType.UNDERSCORE); }

    "+"           { return newToken(TokenType.ADD); }
    "-"           { return newToken(TokenType.SUB); }
    "*"           { return newToken(TokenType.MULT); }
    "/"           { return newToken(TokenType.DIV); }
    "%"           { return newToken(TokenType.MOD); }
    "*>>"         { return newToken(TokenType.HIGH_MULT); }
    "<"           { return newToken(TokenType.LT); }
    "<="          { return newToken(TokenType.LEQ); }
    ">"           { return newToken(TokenType.GT); }
    ">="          { return newToken(TokenType.GEQ); }
    "=="          { return newToken(TokenType.EQEQ); }
    "!="          { return newToken(TokenType.NEQ); }
    "&"           { return newToken(TokenType.AND); }
    "|"           { return newToken(TokenType.OR); }

    {Identifier}  { return newToken(TokenType.ID, yytext()); }
    {Integer}     { return newIntegerToken(yytext()); }  

	"\'"{Newline}"\'"	  			{ return newToken(TokenType.CHARACTER, '\n'); }  
	"\'"{EscapedSingleQuote}"\'"	{ return newToken(TokenType.CHARACTER, '\''); }  
	"\'"{EscapedDoubleQuote}"\'"    { return newToken(TokenType.CHARACTER, '\"'); }  
	"\'"{EscapedBackslash}"\'" 	    { return newToken(TokenType.CHARACTER, '\\'); }  
	{IntegerLiteral}     	 { return newToken(TokenType.CHARACTER, yytext().charAt(1)); }  
    {HexLiteral}     		 { return newToken(TokenType.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", ""))); }  
    {EmptyCharLiteral}       { return newToken(TokenType.error, "Character literals may not be empty."); }  
    {IllegalEscapedCharLiteral}     { return newToken(TokenType.error, "Illegal character: " + yytext()); }  

	
    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			 { return newToken(TokenType.error, "Invalid input" + yytext()); }
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return newToken(TokenType.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+              { string.append(yytext());}

    \\n                 { string.append('\n'); }
    \\\'      { string.append('\''); }
    \\\"      { string.append('\"'); }
    \\\\      { string.append('\\'); }
    {Hex}     { char hexChar = StringUtils.convertHexToChar(yytext());
                string.append(hexChar); }
    
    [^] 	  { return newToken(TokenType.error, "Invalid string: " + string.toString() + yytext()); }
}
