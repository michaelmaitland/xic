package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Not extends UnExpr {

	public Not(Expr expr) {
		super(expr);
	}
	
	@Override
	public String toString() {
		return "(! " + expr.toString() + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("!");
		expr.prettyPrint(p);
		p.endList();
	}
}
