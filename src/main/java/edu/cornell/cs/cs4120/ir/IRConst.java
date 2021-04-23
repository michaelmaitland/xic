package edu.cornell.cs.cs4120.ir;

import java.util.List;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;

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
}
