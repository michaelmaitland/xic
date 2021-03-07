package mtm68.lexer;

public enum TokenType {
	// Reserved
	USE,
	IF,
	WHILE,
	ELSE,
	RETURN,
	LENGTH,
	INT,
	BOOL,
	TRUE,
	FALSE,

	// Constants
	ID,
	INTEGER,
	CHARACTER,
	STRING,
	
	// Punctuation
	OPEN_SQUARE("["),
	CLOSE_SQUARE("]"),
	OPEN_PAREN("("),
	CLOSE_PAREN(")"),
	OPEN_CURLY("{"),
	CLOSE_CURLY("}"),
	EXCLAMATION("!"),
	COLON(":"),
	SEMICOLON(";"),
	COMMA(","),
    EQ("="),
    UNDERSCORE("_"),
	
	// Operators
	ADD("+"),
	SUB("-"),
	MULT("*"),
	DIV("/"),
	MOD("%"),
	HIGH_MULT("*>>"),
	LT("<"),
	LEQ("<="),
	GT(">"),
	GEQ(">="),
	EQEQ("=="),
	NEQ("!="),
	AND("&"),
	OR("|"),

	// Error
	error,
	
	//EOF
	EOF;

	private String pp;

	private TokenType(String pp) {
		this.pp = pp;
	}

	private TokenType() {
		this.pp = name().toLowerCase();
	}

	@Override
	public String toString() {
		return pp;
	}
}