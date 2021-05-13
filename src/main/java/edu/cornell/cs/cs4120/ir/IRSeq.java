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
import mtm68.assem.Assem;
import mtm68.assem.SeqAssem;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.ir.cfg.CFGTracer;

/**
 * An intermediate representation for a sequence of statements
 * SEQ(s1,...,sn)
 */
public class IRSeq extends IRStmt {
    private List<IRStmt> stmts;

    /**
     * @param stmts the statements
     */
    public IRSeq(IRStmt... stmts) {
        this(Arrays.asList(stmts));
    }

    /**
     * Create a SEQ from a list of statements.
     * The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    public IRSeq(List<IRStmt> stmts) {
        this.stmts = stmts;
    }

    public List<IRStmt> stmts() {
        return stmts;
    }

    @Override
    public String label() {
        return "SEQ";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRStmt> results = new ArrayList<>(stmts.size());
        for (IRStmt stmt : stmts) {
            IRStmt newStmt = (IRStmt) v.visit(this, stmt);
            if (newStmt != stmt) modified = true;
            results.add(newStmt);
        }

        if (modified) return v.nodeFactory().IRSeq(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRStmt stmt : stmts)
            result = v.bind(result, v.visit(stmt));
        return result;
    }

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterSeq();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !v.inSeq();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startUnifiedList();
        p.printAtom("SEQ");
        for (IRStmt stmt : stmts)
            stmt.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return new IRSeq(v.flattenSeq(stmts));
	}
	
	@Override
	public IRNode doControlFlow(CFGBuilder builder) {
		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> newStmts = tracer.performReordering();
		
		IRSeq seq = copy();
		seq.stmts = newStmts;
		
		return seq;
	}
	
	@Override
	public IRNode unusedLabels(UnusedLabelVisitor v) {
		v.markUnusedLabels();
		
		List<IRStmt> newStmts = stmts.stream()
			.filter(stmt -> {
				if(stmt instanceof IRLabel) {
					return ((IRLabel)stmt).isUsed();
				}
				return true;
			})
			.collect(Collectors.toList());
		
		IRSeq newSeq = copy();
		newSeq.stmts = newStmts;
		
		return newSeq;
	}
	
	@Override
	public IRNode tile(Tiler t) {
		List<Assem> assems = stmts.stream()
			.map(IRNode::getAssem)
			.collect(Collectors.toList());
		
		return copyAndSetAssem(new SeqAssem(assems));
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return stmts.stream()
				   .map(IRNode::genAvailableExprs)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toSet());
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return stmts.stream()
				.map(s -> s.containsExpr(expr))
				.reduce(Boolean.FALSE, Boolean::logicalOr);
	}
	
	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		
		List<IRStmt> newStmts = stmts.stream()
									 .map(s -> (IRStmt)s.replaceExpr(toReplace, replaceWith))
									 .collect(Collectors.toList());
		
		IRSeq copy = copy();
		copy.stmts = newStmts;
		return copy;
	}

	@Override
	public IRNode decorateContainsMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = stmts.stream()
					  .map(IRNode::isContainsMemSubexpr)
					  .reduce(Boolean.FALSE, Boolean::logicalOr);
		
		IRSeq copy = copy();
		copy.setContainsMemSubexpr(b);
		return copy;
	}
}
