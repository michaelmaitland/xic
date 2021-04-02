package mtm68.ast.nodes.stmts;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.ast.types.Types;
import mtm68.util.ArrayUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class While extends Statement {
	
	private Expr condition;
	private Statement body;

	public While(Expr condition, Statement body) {
		this.condition = condition;
		this.body = body;
	}

	public Expr getCondition() {
		return condition;
	}
	
	public Statement getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "While [condition=" + condition + ", body=" + body + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("while");
		condition.prettyPrint(p);
		body.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newCondition = condition.accept(v);
		Statement newBody = body.accept(v);
		
		if(newCondition != condition || newBody != body) {
			While stmt = copy();
			stmt.condition = newCondition;
			stmt.body = newBody;

			return stmt;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkType(condition, Types.BOOL);
		
		While stmt = copy();
		stmt.result = Result.UNIT;

		return stmt;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		String header = cv.getFreshLabel();
		String trueLabel = cv.getFreshLabel();
		String falseLabel = cv.getFreshLabel();

		IRSeq seq = new IRSeq(
				new IRLabel(header),
				cv.getCtrlFlow(condition, trueLabel, falseLabel),
				new IRLabel(trueLabel),
				body.getIRStmt(),
				new IRJump(new IRName(header)),
				new IRLabel(falseLabel)
				);
		
		return copyAndSetIRStmt(seq);
	}
}
