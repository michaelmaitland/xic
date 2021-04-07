package edu.cornell.cs.cs4120.ir;

import java.util.List;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();
    
	void setSideEffects(List<IRStmt> sideEffects);

	List<IRStmt> getSideEffects();
}
