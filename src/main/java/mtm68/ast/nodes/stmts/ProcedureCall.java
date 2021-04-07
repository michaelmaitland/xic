package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ProcedureCall extends Statement {
	
	private FExpr fexp;

	public ProcedureCall(FExpr fexp) {
		this.fexp = fexp;
	}

	public FExpr getFexp() {
		return fexp;
	}
	
	@Override
	public String toString() {
		return "FunctionCall [fexp=" + fexp + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		fexp.prettyPrint(p);
	}

	@Override
	public Node visitChildren(Visitor v) {
		// We traverse like this because we don't actually want the FExp
		// to get its own traversal. This is important for type-checking
		// because procedure calls have different typing rules than FExp's
		List<Expr> newArgs = acceptList(fexp.getArgs(), v);
		
		if(newArgs != fexp.getArgs()) {
			ProcedureCall call = copy();

			call.fexp = new FExpr(fexp.getId(), newArgs);
			return call;
		}

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkProcCall(this);

		ProcedureCall stmt = copy();
		stmt.result = Result.UNIT;

		return stmt;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		
		String sym = cv.getFuncSymbol(fexp);
		IRName name = inf.IRName(sym);
		List<IRExpr> irArgs = fexp.getArgs().stream()
								.map(Expr::getIRExpr)
								.collect(Collectors.toList());

		IRCallStmt call = inf.IRCallStmt(name, irArgs);

		return copyAndSetIRStmt(call);
	}
}
