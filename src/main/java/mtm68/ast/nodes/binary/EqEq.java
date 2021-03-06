package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class EqEq extends BinExpr {

	public EqEq(Expr left, Expr right) {
		super(Binop.EQEQ, left, right);
	}
}
