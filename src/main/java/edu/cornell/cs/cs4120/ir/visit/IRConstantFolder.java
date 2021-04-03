package edu.cornell.cs.cs4120.ir.visit;

import java.math.BigInteger;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;

public class IRConstantFolder extends IRVisitor {

	public IRConstantFolder(IRNodeFactory inf) {
		super(inf);
	}
	
	@Override
	public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {		
		return n_.constantFold(this);
	}

	public IRNode foldBinOp(IRBinOp binOp) {
		if(isFoldable(binOp)) {
			return performBinOp(binOp);
		}
		else return binOp;
	}
	
	private IRNode performBinOp(IRBinOp binOp) {
		IRBinOp.OpType opType = binOp.opType();
		long leftVal = ((IRConst) binOp.left()).value();
		long rightVal = ((IRConst) binOp.right()).value();
		
		switch(opType) {
			case ADD:
				return new IRConst(leftVal + rightVal);
			case AND:
				long andVal = leftVal > 0 && rightVal > 0 ? 1L : 0L;
				return new IRConst(andVal);
			case ARSHIFT:
				long arShiftVal = leftVal >> rightVal;
				return new IRConst(arShiftVal);
			case DIV:
				if(rightVal == 0) return binOp;
				return new IRConst(leftVal / rightVal);
			case EQ:
				long eqVal = leftVal == rightVal ? 1L : 0L;
				return new IRConst(eqVal);
			case GEQ:
				long geqVal = leftVal >= rightVal ? 1L : 0L;
				return new IRConst(geqVal);
			case GT:
				long gtVal = leftVal > rightVal ? 1L : 0L;
				return new IRConst(gtVal);			
			case HMUL:
				long hMulVal = BigInteger.valueOf(leftVal)
					.multiply(BigInteger.valueOf(rightVal))
					.shiftRight(64).longValue();
				return new IRConst(hMulVal);
			case LEQ:
				long leqVal = leftVal <= rightVal ? 1L : 0L;
				return new IRConst(leqVal);
			case LSHIFT:
				long lShiftVal = leftVal << rightVal;
				return new IRConst(lShiftVal);
			case LT:
				long ltVal = leftVal < rightVal ? 1L : 0L;
				return new IRConst(ltVal);
			case MOD:
				if(rightVal == 0) return binOp;
				return new IRConst(leftVal % rightVal);
			case MUL:
				return new IRConst(leftVal * rightVal);
			case NEQ:
				long neqVal = leftVal != rightVal ? 1L : 0L;
				return new IRConst(neqVal);			
			case OR:
				long orVal = leftVal > 0 || rightVal > 0 ? 1L : 0L;
				return new IRConst(orVal);
			case RSHIFT:
				long rShiftVal = leftVal >>> rightVal;
				return new IRConst(rShiftVal);
			case SUB:
				return new IRConst(leftVal - rightVal);
			case XOR:
				long xorVal = leftVal + rightVal == 1 ? 1L : 0L;
				return new IRConst(xorVal);
		}
		return binOp;
	}
	
	private boolean isFoldable(IRBinOp binOp) {
		return binOp.left() instanceof IRConst && binOp.right() instanceof IRConst;
	}
	
	
}
