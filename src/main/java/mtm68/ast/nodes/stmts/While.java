package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
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
		Expr condition = visitChild(this.condition, v);
		Statement body = visitChild(this.body, v);

		// TODO check copy
		return new While(condition, body);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
