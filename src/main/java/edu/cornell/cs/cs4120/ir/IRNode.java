package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.InsnMapsBuilder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.Assem;
import mtm68.assem.tile.Tile;
import mtm68.ir.cfg.CFGBuilder;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public interface IRNode {

    /**
     * Visit the children of this IR node.
     * @param v the visitor
     * @return the result of visiting children of this node
     */
    IRNode visitChildren(IRVisitor v);

    <T> T aggregateChildren(AggregateVisitor<T> v);

    InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v);

    IRNode buildInsnMaps(InsnMapsBuilder v);

    CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v);

    boolean isCanonical(CheckCanonicalIRVisitor v);

    boolean isConstFolded(CheckConstFoldedIRVisitor v);

    String label();

    /**
     * Print an S-expression representation of this IR node.
     * @param p the S-expression printer
     */
    void printSExp(SExpPrinter p);
    
    IRNode lower(Lowerer v);
    
    IRNode doControlFlow(CFGBuilder builder);
    
    IRNode unusedLabels(UnusedLabelVisitor v);

    IRNode constantFold(IRConstantFolder v);
    
    IRNode tile(Tiler t);

	Assem getAssem();

	void appendAssems(List<Assem> assems);
	
	List<Tile> getTiles();
	
	float getTileCost();
	
	Set<IRExpr> genAvailableExprs();

	Set<IRTemp> use();
	
	boolean containsExpr(IRExpr expr);

	IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith);

	public boolean isContainsMutableMemSubexpr();

	void setContainsMutableMemSubexpr(boolean containsMemSubexpr);

	IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr);
}
