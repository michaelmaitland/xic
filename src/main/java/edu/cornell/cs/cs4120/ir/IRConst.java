package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsExprWithSideEffect;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
public class IRConst extends IRExpr_c {
    private long value;

    /**
     *
     * @param value value of this constant
     */
    public IRConst(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public long constant() {
        return value;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CONST");
        p.printAtom(String.valueOf(value));
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return copyAndSetSideEffects(ArrayUtils.empty());
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}
	
	@Override
	public List<Tile> getTiles() {
		return ArrayUtils.elems(TileFactory.constTile());
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return SetUtils.empty();
	}
	
	@Override
	public Set<IRTemp> use() {
		return SetUtils.empty();
	}
	
	@Override
	public boolean containsExpr(IRExpr expr) {
		return this.equals(expr);
	}

	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		if(this.equals(toReplace)) return replaceWith;
		else return this;
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		IRConst copy = copy();
		copy.setContainsMutableMemSubexpr(false);
		return copy;
	}
	
	@Override
	public IRNode decorateContainsExprWithSideEffect(IRContainsExprWithSideEffect irContainsExprWithSideEffect) {
		IRConst copy = copy();
		copy.setContainsExprWithSideEffect(false);
		return copy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IRConst other = (IRConst) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
