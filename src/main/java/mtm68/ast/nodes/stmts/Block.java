package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

public class Block extends Statement {
	
	private List<Statement> stmts;
	private Optional<Return> returnStmt;

	public Block(List<Statement> stmts) {
		this.stmts = stmts;
		this.returnStmt = Optional.empty();
	}

	public Block(List<Statement> stmts, Return returnStmt) {
		this.stmts = stmts;
		this.returnStmt = Optional.of(returnStmt);
	}

	@Override
	public String toString() {
		return "Block [stmts=" + stmts + ", returnStmt=" + returnStmt + "]";
	}
}
