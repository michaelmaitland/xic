package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class GreaterThanOrEqual extends BinExpr {

	public GreaterThanOrEqual(Expr left, Expr right) {
		super(Binop.GEQ, left, right);
	}
}
