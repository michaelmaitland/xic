package mtm68.ir.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.ir.cfg.ReachingDefns.ReachingData;

public class LoopInvCodeMotion {

	private IRCompUnit ir;
	private IRNodeFactory f;

	public LoopInvCodeMotion(IRCompUnit ir, IRNodeFactory f) {
		this.ir = ir;
		this.f = f;
	}

	/**
	 * Algorithm: 
	 * 
	 * 1. Compute reaching defns.
	 *
	 * 2. Initialize INV := {all exprs in loop, including subexprs}
	 * 
	 * 3. Repeat until no change
	 *   - remove all exprs from INV that:
	 *     - use mem operand whose value might change in loop
	 *     - might cause side effect (exceptions, nondeterminism) 
	 *     - use variables x with more than one definition inside the loop, or whose single defn x = e in loop has e not in INV
	 * 
	 */
	public IRCompUnit doDeadCodeRemoval() {

		Map<String, IRFuncDefn> newFuncs = new HashMap<>();
		for(String k : ir.functions().keySet()) {
			IRFuncDefn func = ir.functions().get(k);
			IRFuncDefn newFunc = doDeadCodeRemoval(func);
			newFuncs.put(k, newFunc);
		}

		ir.copy();
		ir.setFunctions(newFuncs);
		return ir;
	}
	
	private IRFuncDefn doDeadCodeRemoval(IRFuncDefn ir) {
		ReachingDefns rd = new ReachingDefns(ir);
		rd.performAnalysis();
		Graph<IRData<ReachingData>> graph = rd.getGraph();
		
		//Set<IRExpr> inv = initializeINV();
		
		for(Node s : graph.getNodes()) {
		}

		IRFuncDefn copy = ir.copy();
		IRSeq seq = f.IRSeq(rd.getBuilder().convertBackToIR());
		copy.setBody(seq);
		return copy;
	}
}
