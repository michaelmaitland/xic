package mtm68.ir.cfg;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.AvailableCopies.AvailableCopy;
import mtm68.ir.cfg.AvailableCopies.AvailableCopyData;
import mtm68.ir.cfg.IRCFGBuilder.IRData;

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
	 * Compute available copies.
	 *
	 * Suppose statement 
	 *   d : t <- z 
	 * and another statement n that uses t:
	 *   n : y <- t bop x
	 *   
	 * If d reaches n, and no other definition of t reaches n, and there
	 * is no definition of z on any path from d to n, then we can rewrite n as
	 *   n' : y <- z bop x
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
		AvailableCopies rd = new AvailableCopies(ir);
		rd.performAnalysis();
		Graph<IRData<AvailableCopyData>> graph = rd.getGraph();
		
		for(Node s : graph.getNodes()) {
			makeSubstitution(s, graph);
		}

		IRFuncDefn copy = ir.copy();
		IRSeq seq = f.IRSeq(rd.getBuilder().convertBackToIR());
		copy.setBody(seq);
		return copy;
	}
	
	private void makeSubstitution(Node s, Graph<IRData<AvailableCopyData>> graph) {

		IRData<AvailableCopyData> data = graph.getDataForNode(s);

		IRStmt stmt = data.getIR();
		if(!(stmt instanceof IRMove)) return;
		IRMove mov = (IRMove)stmt;
		
		Set<IRTemp> temps = mov.source().getTemps();
		Set<AvailableCopy> reachingCopies = data.getFlowData().getIn();
		Set<AvailableCopy> tempsToReplace = reachingCopies.stream()
												  .filter(d -> temps.contains(d.getX()))
												  .collect(Collectors.toSet());
		
		for(AvailableCopy t : tempsToReplace) {
			IRExpr newSource = (IRExpr)mov.source().replaceExpr(t.getX(), t.getY());
			IRMove newStmt = stmt.copy();
			newStmt.setSource(newSource);
			data.setIR(newStmt);
		}
	}
}
