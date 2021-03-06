package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class NotEqual extends BinExpr {

	public NotEqual(Expr left, Expr right) {
		super(Binop.NEQ, left, right);
	}
}
