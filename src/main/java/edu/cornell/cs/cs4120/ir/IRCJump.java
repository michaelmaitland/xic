package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel, falseLabel)
 */
public class IRCJump extends IRStmt {
    private IRExpr cond;
    private String trueLabel, falseLabel;

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRExpr cond, String trueLabel) {
        this(cond, trueLabel, null);
    }

    /**
     *
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRExpr cond, String trueLabel, String falseLabel) {
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public IRExpr cond() {
        return cond;
    }

    public String trueLabel() {
        return trueLabel;
    }

    public String falseLabel() {
        return falseLabel;
    }

    public boolean hasFalseLabel() {
        return falseLabel != null;
    }

    @Override
    public String label() {
        return "CJUMP";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr expr = (IRExpr) v.visit(this, this.cond);

        if (expr != this.cond)
            return v.nodeFactory().IRCJump(expr, trueLabel, falseLabel);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(cond));
        return result;
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return !hasFalseLabel();
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("CJUMP");
        cond.printSExp(p);
        p.printAtom(trueLabel);
        if (hasFalseLabel()) p.printAtom(falseLabel);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.prependSideEffectsToStmt(this, cond.getSideEffects());
	}
	
	@Override
	public IRNode unusedLabels(UnusedLabelVisitor v) {
		v.addLabelsInUse(trueLabel, falseLabel);
		return this;
	}
	
	public IRCJump negate() {
		IRCJump newJump = copy();
		
		// 1 XOR cond - negate condition
		IRBinOp newCond =  new IRBinOp(OpType.XOR, cond, new IRConst(1));
		newJump.cond = newCond;
		newJump.trueLabel = falseLabel;
		newJump.falseLabel = trueLabel;
		
		return newJump;
	}
	
	public IRCJump removeFalseLabel() {
		IRCJump newJump = copy();
		newJump.falseLabel = null;
		
		return newJump;
	}
	
	@Override
	public List<Tile> getTiles() {
		return ArrayUtils.elems(
				TileFactory.cjumpBasic(),
				TileFactory.cjumpNotEqual(),
				TileFactory.cjumpGreaterThan(),
				TileFactory.cjumpGreaterThanEqual(),
				TileFactory.cjumpLessThanEqual(),
				TileFactory.cjumpLessThan()
//				//TileFactory.cjumpIfZero()
				);
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return cond.genAvailableExprs();
	}

	@Override
	public Set<IRTemp> getTemps() {
		return cond.getTemps();
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return cond.containsExpr(expr);
	}

	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		IRExpr newCond = (IRExpr)cond.replaceExpr(toReplace, replaceWith);
		
		IRCJump copy = copy();
		copy.cond = newCond;
		return copy;
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		IRCJump copy = copy();
		copy.setContainsMutableMemSubexpr(cond.isContainsMutableMemSubexpr());
		return copy;
	}
}
