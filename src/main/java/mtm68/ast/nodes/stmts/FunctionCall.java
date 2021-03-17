package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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

	@Override
	public Node visitChildren(Visitor v) {
		FExpr newFexp = fexp.accept(v);
	
		if(newFexp != fexp) {
			return new FunctionCall(fexp);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
