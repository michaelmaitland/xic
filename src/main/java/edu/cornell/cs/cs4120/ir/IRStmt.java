package edu.cornell.cs.cs4120.ir;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;

/**
 * An intermediate representation for statements
 */
public abstract class IRStmt extends IRNode_c {
	
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}
}
