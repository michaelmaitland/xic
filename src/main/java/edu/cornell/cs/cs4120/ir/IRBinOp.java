package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.SetccAssem.CC;
import mtm68.assem.op.ARShiftAssem;
import mtm68.assem.op.AndAssem;
import mtm68.assem.op.IMulAssem;
import mtm68.assem.op.LShiftAssem;
import mtm68.assem.op.OrAssem;
import mtm68.assem.op.RShiftAssem;
import mtm68.assem.op.SubAssem;
import mtm68.assem.op.XorAssem;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

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
	public List<Tile> getTiles() {
		return ArrayUtils.concatMulti(
					ArrayUtils.elems(
					TileFactory.addBasic(),
					TileFactory.addConstant(),
					TileFactory.binopCompareBasic(OpType.EQ, CC.E),
					TileFactory.binopCompareBasic(OpType.NEQ, CC.NE),
					TileFactory.binopCompareBasic(OpType.GEQ, CC.GE),
					TileFactory.binopCompareBasic(OpType.GT, CC.G),
					TileFactory.binopCompareBasic(OpType.LT, CC.L),
					TileFactory.binopCompareBasic(OpType.LEQ, CC.LE),
					TileFactory.binopCompareBasic(OpType.ULT, CC.B),
					TileFactory.binopDivOrMod(OpType.DIV),
					TileFactory.binopDivOrMod(OpType.MOD),
					TileFactory.binopHighMul()
					),
					TileFactory.binopBasic(OpType.SUB, SubAssem::new),
					TileFactory.binopBasic(OpType.AND, AndAssem::new),
					TileFactory.binopBasic(OpType.MUL, IMulAssem::new),
					TileFactory.binopBasic(OpType.NEQ, XorAssem::new),
					TileFactory.binopBasic(OpType.XOR, XorAssem::new),
					TileFactory.binopBasic(OpType.OR, OrAssem::new),
					TileFactory.binopBasic(OpType.ARSHIFT, ARShiftAssem::new),
					TileFactory.binopBasic(OpType.LSHIFT, LShiftAssem::new),
					TileFactory.binopBasic(OpType.RSHIFT, RShiftAssem::new)
				);
	}

	@Override
	public Set<IRExpr> getExprs() {
		Set<IRExpr> exprs = SetUtils.union(left.getExprs(), right.getExprs());
		exprs.add(this);
		return exprs;
	}
}
