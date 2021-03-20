package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.util.ArrayUtils;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Block extends Statement {
	
	private List<Statement> stmts;
	private Optional<Return> returnStmt;

	public Block(List<Statement> stmts) {
		this(stmts, Optional.empty());
	}

	public Block(List<Statement> stmts, Return returnStmt) {
		this(stmts, Optional.of(returnStmt));
	}
	
	public Block(List<Statement> stmts, Optional<Return> returnStmt) {
		this.stmts = stmts;
		this.returnStmt = returnStmt; 
	}

	@Override
	public String toString() {
		return "Block [stmts=" + stmts + ", returnStmt=" + returnStmt + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();
		for(Statement stmt : stmts) {
			stmt.prettyPrint(p);
		}
		
		if(returnStmt.isPresent()) returnStmt.get().prettyPrint(p);
		p.endList();
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}

	public Optional<Return> getReturnStmt() {
		return returnStmt;
	}
	
	private List<Statement> getStmtsIncludingReturn() {
		List<Statement> ret = ArrayUtils.empty();
		stmts.forEach(ret::add);
		returnStmt.ifPresent(ret::add);
		return ret;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Statement> newStmts = acceptList(stmts, v);
		Optional<Return> newReturnStmt = acceptOptional(returnStmt, v);
		
		if(newStmts != stmts 
				|| newReturnStmt != returnStmt) {
			return new Block(newStmts, newReturnStmt);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		List<Statement> allStmts = getStmtsIncludingReturn();
		Result result = Result.UNIT;
		for(int i = 0; i < allStmts.size(); i++) {
			Statement stmt = allStmts.get(i);

			// Not the last item in the list
			if(i == allStmts.size() - 1) {
				result = stmt.getResult();
			} else {
				tc.checkResultIsUnit(stmt);
			}
		}
		Block newBlock = new Block(stmts, returnStmt);
		newBlock.result = result;
		return newBlock;
	}
}
