package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;

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

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();
		for(Statement stmt : stmts) stmt.prettyPrint(p);
		
		if(returnStmt.isPresent()) returnStmt.get().prettyPrint(p);
		p.endList();
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}
	
	public Optional<Return> getReturnStmt() {
		return returnStmt;
	}
}
