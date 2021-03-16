package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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

	@Override
	public Node visitChildren(Visitor v) {
		Expr expr = visitChild(this.expr, v);
		
		// TODO: check copy
		return new Not(expr);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
