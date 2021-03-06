package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class Mod extends BinExpr {

	public Mod(Expr left, Expr right) {
		super(Binop.MOD, left, right);
	}
}
