package edu.cornell.cs.cs4120.ir;

import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.tile.TileCosts;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

/**
 * An intermediate representation for a temporary register
 * TEMP(name)
 */
public class IRTemp extends IRExpr_c {
    private String name;

    /**
     *
     * @param name name of this temporary register
     */
    public IRTemp(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "TEMP(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("TEMP");
        p.printAtom(name);
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
	public IRNode tile(Tiler t) {
		IRTemp newTemp = copy();
		newTemp.setResultReg(new AbstractReg(name));
		newTemp.tileCost = TileCosts.NO_COST;
		return newTemp;
	}

	@Override
	public Set<IRExpr> getExprs() {
		return SetUtils.elems(this);
	}

}
