package mtm68.ast.nodes.binary;

import java.util.Set;

import mtm68.util.ArrayUtils;

public enum Binop {
	OR("|"),
	AND("&"),
	EQEQ("=="),
	NEQ("!="),
	LT("<"),
	LEQ("<="),
	GEQ(">="),
	GT(">"),
	ADD("+"),
	SUB("-"),
	MULT("*"),
	HIGH_MULT("*>>"),
	DIV("/"),
	MOD("%");
	
	private String pp;
	
	private Binop(String pp) {
		this.pp = pp;
	}
	
	@Override
	public String toString() {
		return pp;
	}
}
