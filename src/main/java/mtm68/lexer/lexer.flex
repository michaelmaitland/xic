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

%{

  /** Data to manage string state */
  StringBuffer string = new StringBuffer();
  int stringCol, stringLine;

	public int yyline() { return yyline + 1; }

	public int yycolumn() { return yycolumn + 1; }


  private Token newToken(TokenType type) {
    return new TokenFactory().newToken(type, yyline(), yycolumn());
  }

  private Token newToken(TokenType type, Object value) {
    return new TokenFactory().newToken(type, value, yyline(), yycolumn());
  }

  private Token newToken(TokenType type, Object value, int line, int column) {
    return new TokenFactory().newToken(type, value, line, column);
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

    "."           { return newToken(TokenType.DOT); }
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
    {Integer}     { return newToken(TokenType.INTEGER, Long.parseLong(yytext())); }  // TODO: What to do if integer constant is too big?

	  {IntegerLiteral}     { return newToken(TokenType.CHARACTER, yytext().charAt(1)); }  
    {HexLiteral}     { return newToken(TokenType.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", ""))); }  

    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			 { return newToken(TokenType.error, "Invalid character " + yytext()); }
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return newToken(TokenType.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    \\n                            { string.append('\n'); }
    \\\'                           { string.append('\''); }
    \\                             { string.append('\\'); }
    {Hex}                          { char hexChar = StringUtils.convertHexToChar(yytext());
                                       string.append(hexChar); }
}
