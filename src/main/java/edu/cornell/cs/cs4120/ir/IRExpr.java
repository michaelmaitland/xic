package edu.cornell.cs.cs4120.ir;

import java.util.List;

import mtm68.assem.operand.Reg;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();
    
	void setSideEffects(List<IRStmt> sideEffects);

	List<IRStmt> getSideEffects();
	
	Reg getResultReg();
}
