package mtm68.ast.nodes.binary;

import mtm68.ast.types.Type;
import static mtm68.ast.types.Types.*;

public enum Binop {
	OR(BOOL, BOOL),
	AND(BOOL, BOOL),
	EQEQ(INT, BOOL),
	NEQ(INT, BOOL),
	LT(INT, BOOL),
	LEQ(INT, BOOL),
	GEQ(INT, BOOL),
	GT(INT, BOOL),
	ADD(INT, INT),
	SUB(INT, INT),
	MULT(INT, INT),
	HIGH_MULT(INT, INT),
	DIV(INT, INT),
	MOD(INT, INT);
	
	private Type expectedType;
	private Type returnType;
	
	private Binop(Type expectedType, Type returnType) {
		this.expectedType = expectedType;
		this.returnType = returnType;
	}
	
	public Type getExpectedType() {
		return expectedType;
	}
	
	public Type getReturnType() {
		return returnType;
	}
}
