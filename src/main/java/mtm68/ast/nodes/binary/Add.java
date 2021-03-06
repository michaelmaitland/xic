package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Add extends BinExpr {

	public Add(Expr left, Expr right) {
		super(Binop.ADD, left, right);
	}
}
