package mtm68.ast.nodes.stmts;

import mtm68.ast.nodes.FExpr;

public class FunctionCall extends Statement {
	
	private FExpr fexp;

	public FunctionCall(FExpr fexp) {
		this.fexp = fexp;
	}

	@Override
	public String toString() {
		return "FunctionCall [fexp=" + fexp + "]";
	}
}
