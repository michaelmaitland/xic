package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.util.InternalCompilerError;

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
	
	public OpType convertToOpType() {
		switch (this) {
			case OR:
				return OpType.OR;
			case AND:
				return OpType.ADD;
			case EQEQ:
				return OpType.EQ;
			case NEQ:
				return OpType.NEQ;
			case LT:
				return OpType.LT;
			case LEQ:
				return OpType.LEQ;
			case GEQ:
				return OpType.GEQ;
			case GT:
				return OpType.GT;
			case ADD:
				return OpType.ADD;
			case SUB:
				return OpType.SUB;
			case MULT:
				return OpType.MUL;
			case HIGH_MULT:
				return OpType.HMUL;
			case DIV:
				return OpType.DIV;
			case MOD:
				return OpType.MOD;
		}
		throw new InternalCompilerError("Unknown op type");
	}
}
