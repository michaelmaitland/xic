package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
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

	@Override
	public void prettyPrint(SExpPrinter p) {
		fexp.prettyPrint(p);
	}
	
	public FExpr getFexp() {
		return fexp;
	}
	
}
