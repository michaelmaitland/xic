package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;

public class BinopPattern implements Pattern {
	
	private OpType opType;
	private Pattern leftPattern;
	private Pattern rightPattern;
	
	public BinopPattern(OpType opType, Pattern leftPattern, Pattern rightPattern) {
		this.opType = opType;
		this.leftPattern = leftPattern;
		this.rightPattern = rightPattern;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRBinOp)) return false;
		
		IRBinOp binop = (IRBinOp) node;
		
		if(binop.opType() != opType) return false;
		

		return leftPattern.matches(binop.left()) && rightPattern.matches(binop.right()) ||
					patternCommutes(opType) && rightPattern.matches(binop.left()) && leftPattern.matches(binop.right());
	}
	
	private boolean patternCommutes(OpType opType) {
		switch(opType) {
		case ADD:
		case AND:
		case ARSHIFT:
		case DIV:
		case EQ:
		case GEQ:
		case GT:
		case HMUL:
		case LEQ:
		case LSHIFT:
		case LT:
		case MOD:
		case MUL:
		case NEQ:
		case OR:
		case RSHIFT:
		case SUB:
		case ULT:
		case XOR:
		default:
			return true;
		}
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		leftPattern.addMatchedExprs(exprs);
		rightPattern.addMatchedExprs(exprs);
	}

}
