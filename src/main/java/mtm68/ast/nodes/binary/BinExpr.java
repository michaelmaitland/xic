package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

public class BinExpr extends Expr {
	
	private Binop op;
	private Expr left;
	private Expr right;

	public BinExpr(Binop op, Expr left, Expr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String toString() {
		return "(" + left + " " + op + " " + right + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(op.toString());
		left.prettyPrint(p);
		right.prettyPrint(p);
		p.endList();
	}
}
