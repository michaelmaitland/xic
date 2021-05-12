package edu.cornell.cs.cs4120.ir;

import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.util.SetUtils;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr_c {
    private IRStmt stmt;
    private IRExpr expr;

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRStmt stmt, IRExpr expr) {
        this.stmt = stmt;
        this.expr = expr;
    }

    public IRStmt stmt() {
        return stmt;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "ESEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRStmt stmt = (IRStmt) v.visit(this, this.stmt);
        IRExpr expr = (IRExpr) v.visit(this, this.expr);

        if (expr != this.expr || stmt != this.stmt)
            return v.nodeFactory().IRESeq(stmt, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(stmt));
        result = v.bind(result, v.visit(expr));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return false;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("ESEQ");
        stmt.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {	
		expr.setSideEffects(v.getESeqSideEffects(stmt, expr.getSideEffects())); 
		return expr;
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		Set<IRExpr> exprs = SetUtils.union(stmt.genAvailableExprs(), expr.genAvailableExprs());
		exprs.add(this);
		return exprs;
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		throw new InternalCompilerError("containsExpr built to work on lowered IR. IRCall not part of lowered IR");
	}

	@Override
	public IRNode decorateContainsMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = stmt.isContainsMemSubexpr() || expr.isContainsMemSubexpr();
		
		IRESeq copy = copy();
		copy.setContainsMemSubexpr(b);
		return copy;
	}
	
}
