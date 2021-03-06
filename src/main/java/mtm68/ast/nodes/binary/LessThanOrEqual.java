package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class LessThanOrEqual extends BinExpr {

	public LessThanOrEqual(Expr left, Expr right) {
		super(Binop.LEQ, left, right);
	}
}
