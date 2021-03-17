package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Negate extends UnExpr {

	public Negate(Expr expr) {
		super(expr);
	}
	
	@Override
	public String toString() {
		return "(- " + expr.toString() + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("-");
		expr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newExpr = expr.accept(v);
		if (newExpr != expr) {
			return new Negate(expr);
        } else {
            return this; // no new node needed
        }	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}

}
