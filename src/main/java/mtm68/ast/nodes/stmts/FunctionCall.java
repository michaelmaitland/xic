package mtm68.ast.nodes.stmts;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
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
		// We traverse like this because we don't actually want the FExp
		// to get its own traversal. This is important for type-checking
		// because procedure calls have different typing rules than FExp's
		List<Expr> newArgs = acceptList(fexp.getArgs(), v);
		
		if(newArgs != fexp.getArgs()) {
			return new FunctionCall(new FExpr(fexp.getId(), newArgs));
		}

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkProcCall(this);

		FunctionCall stmt = new FunctionCall(fexp);
		stmt.result = Result.UNIT;

		return stmt;
	}
	
}
