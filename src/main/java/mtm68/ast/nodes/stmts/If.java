package mtm68.ast.nodes.stmts;

import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

public class If extends Statement {
	
	private Expr condition;
	private Statement ifBranch;
	private Optional<Statement> elseBranch;

	public If(Expr condition, Statement ifBranch) {
		this.condition = condition;
		this.ifBranch = ifBranch;
		this.elseBranch = Optional.empty();
	}

	public If(Expr condition, Statement ifBranch, Statement elseBranch) {
		this.condition = condition;
		this.ifBranch = ifBranch;
		this.elseBranch = Optional.of(elseBranch);
	}

	@Override
	public String toString() {
		return "If [condition=" + condition + ", ifBranch=" + ifBranch + ", elseBranch=" + elseBranch + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("if");
		condition.prettyPrint(p);
		if(elseBranch.isPresent()) {
			p.startList();
			ifBranch.prettyPrint(p);
			elseBranch.get().prettyPrint(p);
			p.endList();
		}
		else {
			p.startList();
			ifBranch.prettyPrint(p);
			p.endList();
		}
		p.endList();
	}
}
