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

    "use"         { return sFactory.newSymbol("use", Sym.USE); }
    "if"		  { return sFactory.newSymbol("if", Sym.IF); }
    "while"  	  { return sFactory.newSymbol("while", Sym.WHILE); }
    "else"		  { return sFactory.newSymbol("else", Sym.ELSE); }
    "return" 	  { return sFactory.newSymbol("return", Sym.RETURN); }
    "length"      { return sFactory.newSymbol("length", Sym.LENGTH); }
    "int"         { return sFactory.newSymbol("int", Sym.INT_T); }
    "bool"        { return sFactory.newSymbol("bool", Sym.BOOL_T); }
    "true"        { return sFactory.newSymbol("true", Sym.TRUE); }
    "false"       { return sFactory.newSymbol("false", Sym.FALSE); }

    "."           { return sFactory.newSymbol(".", Sym.DOT); }
    "["           { return sFactory.newSymbol("[", Sym.OPEN_SQUARE); }
    "]"           { return sFactory.newSymbol("]", Sym.CLOSE_SQUARE); }
    "("           { return sFactory.newSymbol("(", Sym.OPEN_PAREN); }
    ")"           { return sFactory.newSymbol(")", Sym.CLOSE_PAREN); }
    "{"           { return sFactory.newSymbol("{", Sym.OPEN_CURLY); }
    "}"           { return sFactory.newSymbol("}", Sym.CLOSE_CURLY); }
    "!"           { return sFactory.newSymbol("!", Sym.EXCLAMATION); }
    ":"           { return sFactory.newSymbol(":", Sym.COLON); }
    ";"           { return sFactory.newSymbol(";", Sym.SEMICOLON); }
    ","           { return sFactory.newSymbol(",", Sym.COMMA); }
    "="           { return sFactory.newSymbol("=", Sym.EQ); }
    "_"           { return sFactory.newSymbol("_", Sym.UNDERSCORE); }

    "+"           { return sFactory.newSymbol("+", Sym.ADD); }
    "-"           { return sFactory.newSymbol("-", Sym.SUB); }
    "*"           { return sFactory.newSymbol("*", Sym.MULT); }
    "/"           { return sFactory.newSymbol("/", Sym.DIV); }
    "%"           { return sFactory.newSymbol("%", Sym.MOD); }
    "*>>"         { return sFactory.newSymbol("*>>", Sym.HIGH_MULT); }
    "<"           { return sFactory.newSymbol("<", Sym.LT); }
    "<="          { return sFactory.newSymbol("<=", Sym.LEQ); }
    ">"           { return sFactory.newSymbol(">", Sym.GT); }
    ">="          { return sFactory.newSymbol(">=", Sym.GEQ); }
    "=="          { return sFactory.newSymbol("==", Sym.EQEQ); }
    "!="          { return sFactory.newSymbol("!=", Sym.NEQ); }
    "&"           { return sFactory.newSymbol("&", Sym.AND); }
    "|"           { return sFactory.newSymbol("|", Sym.OR); }

    {Identifier}  { return sFactory.newSymbol("id", Sym.ID, yytext()); }
    {Integer}     { return sFactory.newSymbol("integer", Sym.INT, Long.parseLong(yytext())); }  // TODO: What to do if integer constant is too big?

    \'            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(CHAR); }
    \"            { string.setLength(0); stringLine = yyline(); stringCol = yycolumn(); yybegin(STRING); }
    
    [^]			 { return sFactory.newSymbol("error", Sym.ERROR, "Invalid character " + yytext()); }
}

<CHAR> {

	// TODO Check to make sure this regex works. I think we're missing the case of 'abc'

    \'                             { yybegin(YYINITIAL); 
    								 if(string.length() == 0) return sFactory.newSymbol("error", Sym.error, "empty character literal");
    								 else return sFactory.newSymbol("character", Sym.CHAR, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    {IntegerLiteral}     { return sFactory.newSymbol("character", Sym.CHARACTER, yytext().charAt(1)); }  

    {HexLiteral}     { return sFactory.newSymbol("character", Sym.CHARACTER, StringUtils.convertHexToChar(yytext().replace("'", ""))); }  
}

<STRING> {

    \"                             { yybegin(YYINITIAL); 
                                    return sFactory.newSymbol(Sym.STRING, string.toString(), stringLine, stringCol); }

    [^\n\'\\\"]+                   { string.append(yytext());}

    \\n                            { string.append('\n'); }
    \\\'                           { string.append('\''); }
    \\                             { string.append('\\'); }
    {Hex}                          { char hexChar = StringUtils.convertHexToChar(yytext());
                                       string.append(hexChar); }
}
