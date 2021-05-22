package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRContainsExprWithSideEffect;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    private IRExpr target;
    private IRExpr src;

    /**
     *
     * @param target the destination of this move
     * @param src the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr src) {
        this.target = target;
        this.src = src;
    }

    public IRExpr target() {
        return target;
    }
    
    public void setTarget(IRExpr target) {
    	this.target = target;
    }

    public IRExpr source() {
        return src;
    }

    public void setSource(IRExpr source) {
    	this.src = source;
    }
    
    @Override
    public String label() {
        return "MOVE";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        IRExpr target = (IRExpr) v.visit(this, this.target);
        IRExpr expr = (IRExpr) v.visit(this, src);

        if (target != this.target || expr != src)
            return v.nodeFactory().IRMove(target, expr);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        result = v.bind(result, v.visit(target));
        result = v.bind(result, v.visit(src));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("MOVE");
        target.printSExp(p);
        src.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.transformMove(this);
	}

	@Override
	public List<Tile> getTiles() {
		return ArrayUtils.elems(
				TileFactory.moveBasic(),
				TileFactory.moveConst(),
				TileFactory.moveArg(),
				TileFactory.moveFromMem(),
				TileFactory.moveIntoMem(),
				TileFactory.moveConstIntoMem()
			);
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		return target.genAvailableExprs();
	}
	
	@Override
	public Set<IRTemp> use() {
		return SetUtils.union(target.use(), src.use());
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return target.containsExpr(expr) || src.containsExpr(expr);
	}

	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {

		IRExpr newSrc = (IRExpr)src.replaceExpr(toReplace, replaceWith);
		IRExpr newTarget = (IRExpr)target.replaceExpr(toReplace, replaceWith);
		
		IRMove copy = copy();
		copy.src = newSrc;
		copy.target = newTarget;
		return copy;
	}	

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = target.doesContainsMutableMemSubexpr() || src.doesContainsMutableMemSubexpr();
		
		IRMove copy = copy();
		copy.setContainsMutableMemSubexpr(b);
		return copy;
	}

	@Override
	public IRNode decorateContainsExprWithSideEffect(IRContainsExprWithSideEffect irContainsExprWithSideEffect) {
		boolean b = target.doesContainsExprWithSideEffect() || src.doesContainsExprWithSideEffect();
		
		IRMove copy = copy();
		copy.setContainsExprWithSideEffect(b);
		return copy;
	}
}
