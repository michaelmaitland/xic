package mtm68.ir.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.AvailableCopies.AvailableCopy;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.ir.cfg.ReachingDefns.ReachingData;
import mtm68.ir.cfg.ReachingDefns.ReachingDefn;

public class ConstantPropTransformer {

	private IRCompUnit ir;
	private IRNodeFactory f;

	public ConstantPropTransformer(IRCompUnit ir, IRNodeFactory f) {
		this.ir = ir;
		this.f = f;
	}

	/**
	 * Algorithm: 
	 * 
	 * Compute reaching definitions.
	 *
	 * Suppose statement 
	 *   d : t <- c where c constant
	 * and another statment n uses t such as
	 *   n : y <- t bop x
	 *   
	 * then we can rewrite n as 
	 *   n' : y <- c bop x
	 * 
	 */
	public IRCompUnit doConstantProp() {

		Map<String, IRFuncDefn> newFuncs = new HashMap<>();
		for(String k : ir.functions().keySet()) {
			IRFuncDefn func = ir.functions().get(k);
			IRFuncDefn newFunc = doConstantProp(func);
			newFuncs.put(k, newFunc);
		}

		ir.copy();
		ir.setFunctions(newFuncs);
		return ir;
	}
	
	private IRFuncDefn doConstantProp(IRFuncDefn ir) {
		ReachingDefns rd = new ReachingDefns(ir);
		rd.performAnalysis();
		Graph<IRData<ReachingData>> graph = rd.getGraph();
		
		for(Node s : graph.getNodes()) {
			makeSubstitution(s, graph);
		}

		IRFuncDefn copy = ir.copy();
		IRSeq seq = f.IRSeq(rd.getBuilder().convertBackToIR());
		copy.setBody(seq);
		return copy;
	}
	
	private void makeSubstitution(Node s, Graph<IRData<ReachingData>> graph) {

		IRData<ReachingData> data = graph.getDataForNode(s);

		IRStmt stmt = data.getIR();
		if(!(stmt instanceof IRMove)) return;
		IRMove mov = (IRMove)stmt;
		
		Set<IRTemp> temps = mov.source().use();
		Set<ReachingDefn> reachingDefns = data.getFlowData().getIn();
		Set<ReachingDefn> tempsToReplace = reachingDefns.stream()
												  .filter(d -> temps.contains(d.getDefn()))
												  .filter(d -> {
													  IRStmt definerIr = graph.getDataForNode(d.getDefiner()).getIR();
													  if(isMove(definerIr)) {
														  IRMove m = (IRMove)definerIr;
														  IRExpr src = m.source();
														  return isConst(src);
													  }
													  else return false;
												  })
												  .collect(Collectors.toSet());
		
		for(ReachingDefn r : tempsToReplace) {
			IRExpr newSource = (IRExpr)mov.source().replaceExpr(r.getDefn(), r.getDefnExpr());
			IRMove newStmt = stmt.copy();
			newStmt.setSource(newSource);
			data.setIR(newStmt);
		}
	}
	
	private boolean isMove(IRNode ir) {
		return ir instanceof IRMove;
	}
	
	private boolean isConst(IRNode ir) {
		return ir instanceof IRConst;
	}
}
