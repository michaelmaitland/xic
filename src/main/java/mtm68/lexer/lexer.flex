package mtm68.lexer;
import mtm68.util.StringUtils;
import mtm68.parser.sym;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

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
         return newToken(TokenType.INTEGER, Long.parseLong(toParse));
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
MinInt = -9223372036854775808
Newline = "\\n"
EscapedSingleQuote = "\\\'"
EscapedDoubleQuote = "\\\""
EscapedBackslash = "\\\\"

%state STRING
%state CHAR

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
    {MinInt}  	  { return newIntegerToken(yytext()); }  

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
	
    \'            { yybegin(CHAR); stringLine = yyline(); stringCol = yycolumn();}
    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			  { return newToken(TokenType.error, "Invalid input " + yytext());}
}

<CHAR> {

    [^\n\'\\\"]\'		{ yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, yytext().charAt(0), stringLine, stringCol); }

	{Newline}"\'"	  		 { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '\n', stringLine, stringCol); }  
	{EscapedSingleQuote}"\'" { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '\'', stringLine, stringCol); }  
	{EscapedDoubleQuote}"\'" { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '\"', stringLine, stringCol); }  
	{EscapedBackslash}"\'" 	 { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '\\', stringLine, stringCol); }  
    "\""     		 	 	 { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '"', stringLine, stringCol); }  
    "\\t"   		 	 	 { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, '\t', stringLine, stringCol); }  
    {Hex}"\'"     		 	 { yybegin(YYINITIAL); return newToken(TokenType.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", "")), stringLine, stringCol); }  

    \'       		 { yybegin(YYINITIAL); return newToken(TokenType.error, "Character literals may not be empty.", stringLine, stringCol); }  
    \\.  			 { yybegin(YYINITIAL); return newToken(TokenType.error, "Illegal escape sequence: '" + yytext() +  "'", stringLine, stringCol); }  
    {LineTerminator} { yybegin(YYINITIAL); return newToken(TokenType.error, "Character literals may not span multiple lines", stringLine, stringCol); }
    <<EOF>>	  		 { yybegin(YYINITIAL); return newToken(TokenType.error, "Character literals must be terminated with a matching '", stringLine, stringCol); }
    [^]			 	 { yybegin(YYINITIAL); return newToken(TokenType.error, "Invalid character '" + yytext() + "'", stringLine, stringCol); }
}

<STRING> {

    \"               { yybegin(YYINITIAL); return newToken(TokenType.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"\t]+     { string.append(yytext());}

    {Newline}              { string.append('\n'); }
    {EscapedSingleQuote}   { string.append('\''); }
    {EscapedDoubleQuote}   { string.append('\"'); }
    {EscapedBackslash}	   { string.append('\\'); }
    "'"				   	   { string.append('\''); }
    "\\t"				   { string.append('\t'); }
    {Hex}     		 	   { char hexChar = StringUtils.convertHexToChar(yytext());
                	   	     string.append(hexChar); }
                
    \\.      		 { yybegin(YYINITIAL); return newToken(TokenType.error, "Illegal escape sequence \"" + yytext() + "\"", stringLine, stringCol);}
    {LineTerminator} { yybegin(YYINITIAL); return newToken(TokenType.error, "String literals may not span multiple lines", stringLine, stringCol); }
    <<EOF>>	  		 { yybegin(YYINITIAL); return newToken(TokenType.error, "String literals must be terminated with a matching \"", stringLine, stringCol); }
    [^]			 	 { yybegin(YYINITIAL); return newToken(TokenType.error, "Invalid string \"" + string.toString() + yytext() + "\"", stringLine, stringCol);}
}
