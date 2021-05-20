package edu.cornell.cs.cs4120.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a function call
 * CALL(e_target, e_1, ..., e_n)
 */
public class IRCall extends IRExpr_c {
    protected IRExpr target;
    protected List<IRExpr> args;

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, IRExpr... args) {
        this(target, Arrays.asList(args));
    }

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    public IRCall(IRExpr target, List<IRExpr> args) {
        this.target = target;
        this.args = args;
    }

    public IRExpr target() {
        return target;
    }

    public List<IRExpr> args() {
        return args;
    }

    @Override
    public String label() {
        return "CALL";
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

        if (modified) return v.nodeFactory().IRCall(target, results);

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
        p.printAtom("CALL");
        target.printSExp(p);
        for (IRExpr arg : args)
            arg.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.transformCall(target, 1, args);
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		Set<IRExpr> exprs = args.stream()
				   .map(IRNode::genAvailableExprs)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toSet());
		exprs.add(this);
		return exprs;
	}
	
	@Override
	public Set<IRTemp> getTemps() {
		return args.stream()
				   .map(IRNode::getTemps)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toSet());
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		throw new InternalCompilerError("containsExpr built to work on lowered IR. IRCall not part of lowered IR");
	}

	@Override
	public IRExpr replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		throw new InternalCompilerError("containsExpr built to work on lowered IR. IRCall not part of lowered IR");
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = target.isContainsMutableMemSubexpr() 
					|| args.stream()
						.map(IRNode::isContainsMutableMemSubexpr)
						.reduce(Boolean.FALSE, Boolean::logicalOr);
		IRCall copy = copy();
		copy.setContainsMutableMemSubexpr(b);
		return copy;
	}
}
