package edu.cornell.cs.cs4120.ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.tile.Tile;
import mtm68.assem.tile.TileFactory;
import mtm68.util.ArrayUtils;

/** RETURN statement */
public class IRReturn extends IRStmt {
    protected List<IRExpr> rets;

    public IRReturn() {
        this(new ArrayList<>());
    }

    /**
     * @param rets values to return
     */
    public IRReturn(IRExpr... rets) {
        this(Arrays.asList(rets));
    }

    /**
     * @param rets values to return
     */
    public IRReturn(List<IRExpr> rets) {
        this.rets = rets;
    }

    public List<IRExpr> rets() {
        return rets;
    }

    @Override
    public String label() {
        return "RETURN";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        List<IRExpr> results = new ArrayList<>(rets.size());

        for (IRExpr ret : rets) {
            IRExpr newExpr = (IRExpr) v.visit(this, ret);
            if (newExpr != ret) modified = true;
            results.add(newExpr);
        }

        if (modified) return v.nodeFactory().IRReturn(results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRExpr ret : rets)
            result = v.bind(result, v.visit(ret));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("RETURN");
        for (IRExpr ret : rets)
            ret.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return v.transformReturn(rets);
	}
	
	@Override
	public List<Tile> getTiles() {
		return ArrayUtils.singleton(TileFactory.returnBasic());
	}

	@Override
	public Set<IRExpr> getExprs() {
		return rets.stream()
				   .map(IRNode::getExprs)
				   .flatMap(Collection::stream)
				   .collect(Collectors.toSet());
	}
}
