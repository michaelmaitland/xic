package edu.cornell.cs.cs4120.ir;

public interface IRExpr extends IRNode {
    boolean isConstant();

    long constant();
}
