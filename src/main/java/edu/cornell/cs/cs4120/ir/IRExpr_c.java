package edu.cornell.cs.cs4120.ir;

import java.util.List;

import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import mtm68.assem.operand.Reg;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr_c extends IRNode_c implements IRExpr {
	
	protected List<IRStmt> sideEffects;
	protected Reg resultReg;

	@Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterExpr();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return v.inExpr() || !v.inExp();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public long constant() {
        throw new UnsupportedOperationException();
    }
    
	@Override
	public void setSideEffects(List<IRStmt> sideEffects) {
		this.sideEffects = sideEffects;
	}

	@Override
	public List<IRStmt> getSideEffects() {
		return sideEffects;
	}
	
	public void setResultReg(Reg reg) {
		this.resultReg = reg;
	}
	
    public Reg getResultReg() {
		return resultReg;
	}
    
	public <E extends IRExpr_c> E copyAndSetSideEffects(List<IRStmt> sideEffects) {
		E newE = this.copy();
		newE.setSideEffects(sideEffects);
		return newE;
	}
}
