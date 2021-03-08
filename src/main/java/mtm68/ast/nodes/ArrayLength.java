package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class ArrayLength extends Expr {
	
	private Expr exp;

	public ArrayLength(Expr exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "length [exp=" + exp + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("length");
		exp.prettyPrint(p);
		p.endList();
	}
	
	
}