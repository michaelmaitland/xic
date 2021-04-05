package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.HasResult;
import mtm68.ast.types.Result;

public abstract class Statement extends Node implements HasResult {
	
	protected Result result;
	
	protected IRStmt irStmt;
	
	public IRStmt getIRStmt() {
		return irStmt;
	}

	public void setIRStmt(IRStmt stmt) {
		this.irStmt = stmt;
	}

	@Override
	public Result getResult() {
		return result;
	}
	
	/**
	 * Copies this Statement, sets the IRStmt of the copied Statement, and returns
	 * that copied Statement.
	 * @param stmt the IRStmt to set the copied Statement
	 * @return the copied Statement
	 */
	public <E extends Statement> E copyAndSetIRStmt(IRStmt stmt) {
		E newE = this.copy();
		newE.setIRStmt(stmt);
		return newE;
	}
}