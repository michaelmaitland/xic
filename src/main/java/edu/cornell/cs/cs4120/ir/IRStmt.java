package edu.cornell.cs.cs4120.ir;

import mtm68.ir.cfg.CFGBuilder;

/**
 * An intermediate representation for statements
 */
public abstract class IRStmt extends IRNode_c {
	
	@Override
	public IRNode doControlFlow(CFGBuilder builder) {
		builder.visitStatement(this);
		return this;
	}
}
