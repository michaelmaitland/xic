package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class GreaterThan extends BinExpr {

	public GreaterThan(Expr left, Expr right) {
		super(Binop.GT, left, right);
	}
}
