package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Mult extends BinExpr {

	public Mult(Expr left, Expr right) {
		super(Binop.MULT, left, right);
	}
}
