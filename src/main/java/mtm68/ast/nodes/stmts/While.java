package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

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
	
	
}
