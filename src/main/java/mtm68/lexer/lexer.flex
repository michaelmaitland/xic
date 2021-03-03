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


  private Token newToken(int id) {
    return new TokenFactory().newToken(id, yyline(), yycolumn());
  }

  private Token newToken(int id, Object value) {
    return new TokenFactory().newToken(id, value, yyline(), yycolumn());
  }

  private Token newToken(int id, Object value, int line, int column) {
    return new TokenFactory().newToken(id, value, line, column);
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

    "use"         { return newToken(sym.USE); }
    "if"		  { return newToken(sym.IF); }
    "while"  	  { return newToken(sym.WHILE); }
    "else"		  { return newToken(sym.ELSE); }
    "return" 	  { return newToken(sym.RETURN); }
    "length"      { return newToken(sym.LENGTH); }
    "int"         { return newToken(sym.INT); }
    "bool"        { return newToken(sym.BOOL); }
    "true"        { return newToken(sym.TRUE); }
    "false"       { return newToken(sym.FALSE); }

    "."           { return newToken(sym.DOT); }
    "["           { return newToken(sym.OPEN_SQUARE); }
    "]"           { return newToken(sym.CLOSE_SQUARE); }
    "("           { return newToken(sym.OPEN_PAREN); }
    ")"           { return newToken(sym.CLOSE_PAREN); }
    "{"           { return newToken(sym.OPEN_CURLY); }
    "}"           { return newToken(sym.CLOSE_CURLY); }
    "!"           { return newToken(sym.EXCLAMATION); }
    ":"           { return newToken(sym.COLON); }
    ";"           { return newToken(sym.SEMICOLON); }
    ","           { return newToken(sym.COMMA); }
    "="           { return newToken(sym.EQ); }
    "_"           { return newToken(sym.UNDERSCORE); }

    "+"           { return newToken(sym.ADD); }
    "-"           { return newToken(sym.SUB); }
    "*"           { return newToken(sym.MULT); }
    "/"           { return newToken(sym.DIV); }
    "%"           { return newToken(sym.MOD); }
    "*>>"         { return newToken(sym.HIGH_MULT); }
    "<"           { return newToken(sym.LT); }
    "<="          { return newToken(sym.LEQ); }
    ">"           { return newToken(sym.GT); }
    ">="          { return newToken(sym.GEQ); }
    "=="          { return newToken(sym.EQEQ); }
    "!="          { return newToken(sym.NEQ); }
    "&"           { return newToken(sym.AND); }
    "|"           { return newToken(sym.OR); }

    {Identifier}  { return newToken(sym.ID, yytext()); }
    {Integer}     { return newToken(sym.INTEGER, Long.parseLong(yytext())); }  // TODO: What to do if integer constant is too big?

	  {IntegerLiteral}     { return newToken(sym.CHARACTER, yytext().charAt(1)); }  
    {HexLiteral}     { return newToken(sym.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", ""))); }  

    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			 { return newToken(sym.error, "Invalid character " + yytext()); }
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return newToken(sym.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    \\n                            { string.append('\n'); }
    \\\'                           { string.append('\''); }
    \\                             { string.append('\\'); }
    {Hex}                          { char hexChar = StringUtils.convertHexToChar(yytext());
                                       string.append(hexChar); }
}
