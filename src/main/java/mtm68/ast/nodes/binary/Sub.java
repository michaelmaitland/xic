package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Sub extends BinExpr {

	public Sub(Expr left, Expr right) {
		super(Binop.SUB, left, right);
	}
}
