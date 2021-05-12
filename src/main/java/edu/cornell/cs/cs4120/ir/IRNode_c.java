package edu.cornell.cs.cs4120.ir;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.InsnMapsBuilder;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.Assem;
import mtm68.assem.SeqAssem;
import mtm68.assem.operand.FreshRegGenerator;
import mtm68.assem.operand.Reg;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;
import mtm68.assem.tile.Tile;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.util.ArrayUtils;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode_c implements IRNode, Cloneable {

	protected Assem assem;
	protected float tileCost = Float.MAX_VALUE;
	protected boolean containsMemSubexpr;
	
	
	@Override
	public boolean isContainsMemSubexpr() {
		return containsMemSubexpr;
	}

	@Override
	public void setContainsMemSubexpr(boolean containsMemSubexpr) {
		this.containsMemSubexpr = containsMemSubexpr;
	}

	@Override
	public IRNode visitChildren(IRVisitor v) {
		return this;
	}

	@Override
	public <T> T aggregateChildren(AggregateVisitor<T> v) {
		return v.unit();
	}

	@Override
	public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
		return v;
	}

	@Override
	public IRNode buildInsnMaps(InsnMapsBuilder v) {
		v.addInsn(this);
		return this;
	}

	@Override
	public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
		return v;
	}

	@Override
	public boolean isCanonical(CheckCanonicalIRVisitor v) {
		return true;
	}

	@Override
	public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
		return true;
	}

	@Override
	public IRNode tile(Tiler t) {
		if(assem != null) return this;
		
		List<Tile> tiles = getTiles();
		float leastCost = Float.MAX_VALUE;
		Assem bestAssem = null;
		Reg bestResultReg = null;
		Tile bestTile = null;
		
		for(Tile tile : tiles) {
			tile.setTiler(t);
			tile.setBaseNode(this);

			Pattern pattern = tile.getPattern();

			Map<String, IRExpr> matchedExprs = new HashMap<>();
			if(pattern.matches(this)) {
				pattern.addMatchedExprs(matchedExprs);

				PatternResults patternResults = new PatternResults(matchedExprs);
				
				Reg resultReg = FreshRegGenerator.getFreshAbstractReg();
				Assem tiledAssem = tile.getTiledAssem(resultReg, patternResults);
				
				float cost = patternResults.getUsedExprs()
						.stream()
						.filter(e -> e != this)
						.map(IRNode::getTileCost)
						.collect(Collectors.reducing(0.0f, (a, b) -> a + b));
				
				cost += tile.getCost();
				
				if(cost >= leastCost) continue;
				leastCost = cost;
				
				List<Assem> requiredAssem = patternResults.getUsedExprs().stream()
					.map(IRExpr::getAssem)
					.collect(Collectors.toList());

				requiredAssem.add(tiledAssem);
				
				bestAssem = new SeqAssem(requiredAssem);
				bestTile = tile;
				bestResultReg = resultReg;
			}
		}

		if(bestAssem == null) throw new InternalCompilerError("Could not tile node: " + this);
				
		IRNode_c newNode = copyAndSetAssem(bestAssem);
		newNode.tileCost = leastCost;
		
//		System.out.println("Best tile for " + newNode + " is " + bestTile.getPattern());
		
		if(newNode instanceof IRExpr_c) {
			((IRExpr_c)newNode).setResultReg(bestResultReg);
		}
		
		return newNode;
	}

	@Override
	public abstract String label();

	@Override
	public abstract void printSExp(SExpPrinter p);

	public IRNode doControlFlow(CFGBuilder builder) {
		return this;
	}

	@Override
	public IRNode unusedLabels(UnusedLabelVisitor v) {
		return this;
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw); SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
			printSExp(sp);
		}
		return sw.toString();
	}

	@SuppressWarnings("unchecked")
	public <N extends IRNode_c> N copy() {
		try {
			return (N) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public <N extends IRNode_c> N copyAndSetAssem(Assem assem) {
		return copyAndSetAssem(assem, tileCost);
	}

	public <N extends IRNode_c> N copyAndSetAssem(Assem assem, float tileCost) {
		N newN = this.copy();
		newN.assem = assem;
		newN.tileCost = tileCost;
		return newN;
	}

	public void setAssem(Assem assem) {
		this.assem = assem;
	}

	@Override
	public Assem getAssem() {
		return assem;
	}
	
	@Override
	public float getTileCost() {
		return tileCost;
	}

	@Override
	public void appendAssems(List<Assem> assems) {
		assem = new SeqAssem(ArrayUtils.prepend(assem, assems));
	}

	@Override
	public List<Tile> getTiles() {
		return ArrayUtils.empty();
	}
}
