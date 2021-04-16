package edu.cornell.cs.cs4120.ir;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.Assem;
import mtm68.assem.SeqAssem;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.Reg;

/**
 * An intermediate representation for a binary operation
 * OP(left, right)
 */
public class IRBinOp extends IRExpr_c {

    /**
     * Binary operators
     */
    public enum OpType {
        ADD, SUB, MUL, HMUL, DIV, MOD, AND, OR, XOR, LSHIFT, RSHIFT, ARSHIFT,
        EQ, NEQ, LT, ULT, GT, LEQ, GEQ;

        @Override
        public String toString() {
            switch (this) {
            case ADD:
                return "ADD";
            case SUB:
                return "SUB";
            case MUL:
                return "MUL";
            case HMUL:
                return "HMUL";
            case DIV:
                return "DIV";
            case MOD:
                return "MOD";
            case AND:
                return "AND";
            case OR:
                return "OR";
            case XOR:
                return "XOR";
            case LSHIFT:
                return "LSHIFT";
            case RSHIFT:
                return "RSHIFT";
            case ARSHIFT:
                return "ARSHIFT";
            case EQ:
                return "EQ";
            case NEQ:
                return "NEQ";
            case LT:
                return "LT";
            case ULT:
            	return "ULT";
            case GT:
                return "GT";
            case LEQ:
                return "LEQ";
            case GEQ:
                return "GEQ";
            }
            throw new InternalCompilerError("Unknown op type");
        }
    };

    private OpType type;
    private IRExpr left, right;

    public IRBinOp(OpType type, IRExpr left, IRExpr right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public OpType opType() {
        return type;
    }

    public IRExpr left() {
        return left;
    }
    
    public void setLeft(IRExpr left) {
    	this.left = left;
    }

    public IRExpr right() {
        return right;
    }

    @Override
    public String label() {
        return type.toString();
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr left = (IRExpr) v.visit(this, this.left);
        IRExpr right = (IRExpr) v.visit(this, this.right);

        if (left != this.left || right != this.right)
            return v.nodeFactory().IRBinOp(type, left, right);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(left));
        result = v.bind(result, v.visit(right));
        return result;
    }

    @Override
    public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
        if (isConstant()) {
            switch (type) {
            case DIV:
            case MOD:
                return right.constant() == 0;
            default:
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom(type.toString());
        left.printSExp(p);
        right.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.transformBinOp(this);
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return v.foldBinOp(this);
	}

	@Override
	public IRNode tile(Tiler t) {
		Assem assem = null;
		Reg resultReg = t.getFreshAbstractReg();
		switch(type) {
		case ADD:
			assem = new LeaAssem(resultReg, new Mem(left.getResultReg(), right.getResultReg()));
		case AND:
			break;
		case ARSHIFT:
			break;
		case DIV:
			break;
		case EQ:
			break;
		case GEQ:
			break;
		case GT:
			break;
		case HMUL:
			break;
		case LEQ:
			break;
		case LSHIFT:
			break;
		case LT:
			break;
		case MOD:
			break;
		case MUL:
			break;
		case NEQ:
			break;
		case OR:
			break;
		case RSHIFT:
			break;
		case SUB:
			break;
		case ULT:
			break;
		case XOR:
			break;
		default:
			break;
		}
		IRBinOp newOp = copyAndSetAssem(new SeqAssem(left.getAssem(), right.getAssem(), assem));
		newOp.setResultReg(resultReg);
		return newOp;
	}
}
