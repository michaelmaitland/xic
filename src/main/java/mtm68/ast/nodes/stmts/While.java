package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.ast.types.Types;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class While extends Statement {
	
	private Expr condition;
	private Statement body;

	public While(Expr condition, Statement body) {
		this.condition = condition;
		this.body = body;
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
	
	public Expr getCondition() {
		return condition;
	}
	
	public Statement getBody() {
		return body;
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newCondition = condition.accept(v);
		Statement newBody = body.accept(v);
		
		if(newCondition != condition || newBody != body) {
			return new While(condition, body);
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.typeCheck(condition, Types.BOOL);
		
		While stmt = new While(condition, body);
		stmt.result = Result.UNIT;

		return stmt;
	}
}
