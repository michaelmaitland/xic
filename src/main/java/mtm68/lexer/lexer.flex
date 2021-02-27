package mtm68.lexer;
import mtm68.util.StringUtils;
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

	SymbolFactory sFactory = new ComplexSymbolFactory();

    StringBuffer string = new StringBuffer();
    int stringCol, stringLine;

	public int yyline() { return yyline + 1; }

	public int yycolumn() { return yycolumn + 1; }

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
%state CHAR

%%


<YYINITIAL> {
    {Whitespace}  { /* ignore */ }
    {Comment}     { /* ignore */ }

    "use"         { return sFactory.newSymbol("use", sym.USE); }
    "if"		  { return sFactory.newSymbol("if", sym.IF); }
    "while"  	  { return sFactory.newSymbol("while", sym.WHILE); }
    "else"		  { return sFactory.newSymbol("else", sym.ELSE); }
    "return" 	  { return sFactory.newSymbol("return", sym.RETURN); }
    "length"      { return sFactory.newSymbol("length", sym.LENGTH); }
    "int"         { return sFactory.newSymbol("int", sym.INT_T); }
    "bool"        { return sFactory.newSymbol("bool", sym.BOOL_T); }
    "true"        { return sFactory.newSymbol("true", sym.TRUE); }
    "false"       { return sFactory.newSymbol("false", sym.FALSE); }

    "."           { return sFactory.newSymbol(".", sym.DOT); }
    "["           { return sFactory.newSymbol("[", sym.OPEN_SQUARE); }
    "]"           { return sFactory.newSymbol("]", sym.CLOSE_SQUARE); }
    "("           { return sFactory.newSymbol("(", sym.OPEN_PAREN); }
    ")"           { return sFactory.newSymbol(")", sym.CLOSE_PAREN); }
    "{"           { return sFactory.newSymbol("{", sym.OPEN_CURLY); }
    "}"           { return sFactory.newSymbol("}", sym.CLOSE_CURLY); }
    "!"           { return sFactory.newSymbol("!", sym.EXCLAMATION); }
    ":"           { return sFactory.newSymbol(":", sym.COLON); }
    ";"           { return sFactory.newSymbol(";", sym.SEMICOLON); }
    ","           { return sFactory.newSymbol(",", sym.COMMA); }
    "="           { return sFactory.newSymbol("=", sym.EQ); }
    "_"           { return sFactory.newSymbol("_", sym.UNDERSCORE); }

    "+"           { return sFactory.newSymbol("+", sym.ADD); }
    "-"           { return sFactory.newSymbol("-", sym.SUB); }
    "*"           { return sFactory.newSymbol("*", sym.MULT); }
    "/"           { return sFactory.newSymbol("/", sym.DIV); }
    "%"           { return sFactory.newSymbol("%", sym.MOD); }
    "*>>"         { return sFactory.newSymbol("*>>", sym.HIGH_MULT); }
    "<"           { return sFactory.newSymbol("<", sym.LT); }
    "<="          { return sFactory.newSymbol("<=", sym.LEQ); }
    ">"           { return sFactory.newSymbol(">", sym.GT); }
    ">="          { return sFactory.newSymbol(">=", sym.GEQ); }
    "=="          { return sFactory.newSymbol("==", sym.EQEQ); }
    "!="          { return sFactory.newSymbol("!=", sym.NEQ); }
    "&"           { return sFactory.newSymbol("&", sym.AND); }
    "|"           { return sFactory.newSymbol("|", sym.OR); }

    {Identifier}  { return sFactory.newSymbol("id", sym.ID, yytext()); }
    {Integer}     { return sFactory.newSymbol("integer", sym.INT, Long.parseLong(yytext())); }  // TODO: What to do if integer constant is too big?

    \'            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(CHAR); }
    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			 { return sFactory.newSymbol("error", sym.ERROR, "Invalid character " + yytext()); }
}

<CHAR> {

	// TODO Check to make sure this regex works. I think we're missing the case of 'abc'

    \'                             { yybegin(YYINITIAL); 
    								 if(string.length() == 0) return sFactory.newSymbol("error", sym.error, "empty character literal");
    								 else return sFactory.newSymbol("character", sym.CHAR, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    {IntegerLiteral}     { return sFactory.newSymbol("character", sym.CHARACTER, yytext().charAt(1)); }  

    {HexLiteral}     { return sFactory.newSymbol("character", sym.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", ""))); }  
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return sFactory.newSymbol(sym.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    \\n                            { string.append('\n'); }
    \\\'                           { string.append('\''); }
    \\                             { string.append('\\'); }
    {Hex}                          { char hexChar = StringUtils.convertHexToChar(yytext());
                                       string.append(hexChar); }
}
