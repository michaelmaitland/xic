package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Div extends BinExpr {

	public Div(Expr left, Expr right) {
		super(Binop.DIV, left, right);
	}
}
