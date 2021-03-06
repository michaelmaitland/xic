package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Or extends BinExpr {

	public Or(Expr left, Expr right) {
		super(Binop.OR, left, right);
	}
}
