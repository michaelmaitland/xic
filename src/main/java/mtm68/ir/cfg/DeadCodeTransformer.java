package mtm68.ir.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.ir.cfg.LiveVariables.LiveData;
import mtm68.ir.cfg.LiveVariables.LiveVar;

public class DeadCodeTransformer {

	private IRCompUnit ir;
	private IRNodeFactory f;

	public DeadCodeTransformer(IRCompUnit ir, IRNodeFactory f) {
		this.ir = ir;
		this.f = f;
	}

	/**
	 * Algorithm: 
	 * 
	 * Compute live variables.
	 *
	 * Suppose statement 
	 *   s : a <- b bop c
	 * or
	 *   s : a <- M[x]
	 * such that a is not live out of s, then it can be deleted
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
		LiveVariables lv = new LiveVariables(ir);
		lv.performAnalysis();
		Graph<IRData<LiveData>> graph = lv.getGraph();
		
		for(Node s : graph.getNodes()) {
			removeIfPossible(s, graph);
		}

		IRFuncDefn copy = ir.copy();
		IRSeq seq = f.IRSeq(lv.getBuilder().convertBackToIR());
		copy.setBody(seq);
		return copy;
	}
	
	private void removeIfPossible(Node s, Graph<IRData<LiveData>> graph) {

		IRData<LiveData> data = graph.getDataForNode(s);

		IRStmt stmt = data.getIR();
		if(!isMove(stmt)) return;
		IRMove mov = (IRMove)stmt;
		
		if(!isTemp(mov.target())) return;
		IRTemp temp = (IRTemp)mov.target();

		Set<LiveVar> liveOut = data.getFlowData().getOut();
		
		boolean isAlive = liveOut.stream().anyMatch(l ->  l.a.equals(temp));
		
		if(!isAlive) {
			data.setIR(f.IRSeq());
		}
	}
	
	private boolean isTemp(IRNode n) {
		return n instanceof IRTemp;
	}
	
	private boolean isMove(IRNode n) {
		return n instanceof IRMove;
	}
}
