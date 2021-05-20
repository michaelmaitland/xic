package mtm68.ir.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.AvailableExprs.AvailableData;
import mtm68.ir.cfg.AvailableExprs.AvailableExpr;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.ir.cfg.ReachingDefns.ReachingData;
import mtm68.ir.cfg.ReachingDefns.ReachingDefn;
import mtm68.util.FreshTempGenerator;

public class CopyPropTransformer {

	private IRCompUnit ir;
	private IRNodeFactory f;

	public CopyPropTransformer(IRCompUnit ir, IRNodeFactory f) {
		this.ir = ir;
		this.f = f;
	}

	/**
	 * Algorithm: 
	 * 
	 * Compute reaching definitions.
	 *
	 * Suppose statement 
	 *   d : t <- z 
	 * and another statement n that uses t:
	 *   n : y <- t bop x
	 *   
	 * If d reaches n, and no other definition of t reaches n, and there
	 * is no definition of z on any path from d to n, then we can rewrite n as
	 * 
	 *   n' : y <- z bop x
	 * 
	 */
	public IRCompUnit doCopyProp() {

		Map<String, IRFuncDefn> newFuncs = new HashMap<>();
		for(String k : ir.functions().keySet()) {
			IRFuncDefn func = ir.functions().get(k);
			IRFuncDefn newFunc = doCopyProp(func);
			newFuncs.put(k, newFunc);
		}

		ir.copy();
		ir.setFunctions(newFuncs);
		return ir;
	}
	
	private IRFuncDefn doCopyProp(IRFuncDefn ir) {
		ReachingDefns rd = new ReachingDefns(ir, f);
		rd.performReachingDefnsAnalysis();
		Graph<IRData<ReachingData>> graph = rd.getGraph();
		
		for(Node s : graph.getNodes()) {
			ReachingData d = graph.getDataForNode(s).getFlowData();
			
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
		Set<IRTemp> temps = stmt.getTemps();

		Set<ReachingDefn> reachingDefns = data.getFlowData().getIn();
		Set<ReachingDefn> tempsToReplace = reachingDefns.stream()
												  .filter(d -> temps.contains(d.getDefn()))
												  .collect(Collectors.toSet());
		
		for(ReachingDefn t : tempsToReplace) {
			ReachingData aaaa = graph.getDataForNode(t.getDefiner()).getFlowData();
//			stmt.replaceExpr(t.getDefn(), )
		}
	}
	
	private String getFreshTemp() {
		return FreshTempGenerator.getFreshTemp();
	}
}
