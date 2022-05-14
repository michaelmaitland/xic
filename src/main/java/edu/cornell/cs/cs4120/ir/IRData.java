package edu.cornell.cs.cs4120.ir;

import java.util.Arrays;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsExprWithSideEffect;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;

/** Static data. */
public final class IRData extends IRNode_c {
	private final String name;
	private final long[] data;

	public IRData(String name, long[] data) {
		this.name = name;
		this.data = data;
	}

	public String name() {
		return name;
	}

	public long[] data() {
		return data;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof IRData)) return false;
		IRData otherData = (IRData) other;
		return name.equals(otherData.name) && Arrays.equals(data, otherData.data);
	}

	public int hashCode() {
		return name.hashCode() * 31 + Arrays.hashCode(data);
	}
	
	@Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("DATA");
        p.printAtom(name);
        p.printAtom(Arrays.toString(data));
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return this;
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return null;
	}

	@Override
	public Set<IRTemp> use() {
		return null;
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return false;
	}

	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		return this;
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		return this;
	}

	@Override
	public IRNode decorateContainsExprWithSideEffect(IRContainsExprWithSideEffect irContainsExprWithSideEffect) {
		return this;
	}

	@Override
	public String label() {
		return "DATA ( " + name + ", " + Arrays.toString(data) + ")";
	}
}
