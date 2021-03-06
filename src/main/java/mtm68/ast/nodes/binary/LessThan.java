package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class LessThan extends BinExpr {

	public LessThan(Expr left, Expr right) {
		super(Binop.LT, left, right);
	}
}
