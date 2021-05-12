package edu.cornell.cs.cs4120.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a call statement.
 * t_1, t_2, _, t_4 = CALL(e_target, e_1, ..., e_n)
 */
public class IRCallStmt extends IRStmt {
    protected IRExpr target;
    protected List<IRExpr> args;
    protected int numRets;

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCallStmt(IRExpr target, int numRets, IRExpr... args) {
        this(target, numRets, Arrays.asList(args));
    }

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCallStmt(IRExpr target, int numRets, List<IRExpr> args) {
        this.target = target;
        this.args = args;
        this.numRets = numRets;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }
    
    public int getNumRets() {
		return numRets;
	}
    
    @Override
    public String label() {
        return "CALL_STMT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        IRExpr target = (IRExpr) v.visit(this, this.target);
        if (target != this.target) modified = true;

        List<IRExpr> results = new ArrayList<>(args.size());
        for (IRExpr arg : args) {
            IRExpr newExpr = (IRExpr) v.visit(this, arg);
            if (newExpr != arg) modified = true;
            results.add(newExpr);
        }

        if (modified) return v.nodeFactory().IRCallStmt(target, numRets, results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        for (IRExpr arg : args)
            result = v.bind(result, v.visit(arg));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inExpr();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CALL_STMT");
        target.printSExp(p);
        for (IRExpr arg : args)
            arg.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.transformCall(target, numRets, args);
	}
	
	@Override
	public IRNode unusedLabels(UnusedLabelVisitor v) {
		v.addLabelsInUse(((IRName)target).name());
		return this;
	}
	
	@Override
	public IRNode tile(Tiler t) {
		return t.tileCallStmt(this);
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return args.stream()
				   .map(IRNode::genAvailableExprs)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toSet());
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return target.containsExpr(expr) 
			   || args.stream()
					  .map(e -> e.containsExpr(expr))
					  .reduce(Boolean.FALSE, Boolean::logicalOr);
	}

	@Override
	public IRNode decorateContainsMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = target.isContainsMemSubexpr() 
					|| args.stream()
						.map(IRNode::isContainsMemSubexpr)
						.reduce(Boolean.FALSE, Boolean::logicalOr);
		IRCallStmt copy = copy();
		copy.setContainsMemSubexpr(b);
		return copy;
	}
}
