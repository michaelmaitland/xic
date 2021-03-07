package mtm68.ast.nodes;

import java.util.Optional;

import mtm68.ast.nodes.stmts.Statement;

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
}
