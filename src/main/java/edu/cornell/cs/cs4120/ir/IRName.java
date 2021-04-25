package edu.cornell.cs.cs4120.ir;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.util.ArrayUtils;

/**
 * An intermediate representation for named memory address
 * NAME(n)
 */
public class IRName extends IRExpr_c {
    private String name;

    /**
     *
     * @param name name of this memory address
     */
    public IRName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "NAME(" + name + ")";
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("NAME");
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

}
