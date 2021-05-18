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
import mtm68.util.FreshTempGenerator;

public class CSETransformer {

	
	/*
	 * Algorithm: 
	 * 
	 * Compute reaching expressions, that is, find statements of the form
	 * n : v <- x binop y, st the path from n to s does not compute x binop y nor
	 * define x nor y.
	 * 
	 * Choose fresh temp w, and for such n, rewrite as:
	 *   n : w <- x bop y
	 *   n' : v <- w
	 *   
	 * Finally, modify statement s to be:
	 *   s : t <- w
	 * 
	 */
	public IRCompUnit doCSE(IRCompUnit ir, IRNodeFactory f) {
		
		Map<String, IRFuncDefn> newFuncs = new HashMap<>();
		for(String k : ir.functions().keySet()) {
			IRFuncDefn func = ir.functions().get(k);
			IRFuncDefn newFunc = doCSE(func, f);
			newFuncs.put(k, newFunc);
		}
		ir.copy();
		ir.setFunctions(newFuncs);
		return ir;
	}
	
	private IRFuncDefn doCSE(IRFuncDefn ir, IRNodeFactory f) {
		AvailableExprs ae = new AvailableExprs(ir, f);
		ae.performAvaliableExpressionsAnalysis();
		Graph<IRData<AvailableData>> graph = ae.getGraph();
		
		for(Node s : graph.getNodes()) {
			IRData<AvailableData> data = graph.getDataForNode(s);
			AvailableData d = data.getFlowData();
			
			Set<AvailableExpr> exprsToSub = 
				d.getIn()
			     .stream()
				 .filter(e -> d.getExprs().contains(e.getExpr()))
				 .collect(Collectors.toSet());
			
			for(AvailableExpr e : exprsToSub) {
				Node definer = e.getDefiner();
				IRData<AvailableData> dData = graph.getDataForNode(definer);
				IRTemp temp = f.IRTemp(getFreshTemp());

				IRSeq seq = f.IRSeq(
						f.IRMove(temp, e.getExpr()),
						(IRStmt)dData.getIR().replaceExpr(e.getExpr(), temp)
						);
				dData.setIR(seq);
				
				IRStmt newNodeIr = (IRStmt)data.getIR().replaceExpr(e.getExpr(), temp);
				data.setIR(newNodeIr);
			}
		}
		IRFuncDefn copy = ir.copy();
		IRSeq seq = f.IRSeq(ae.getBuilder().convertBackToIR());
		copy.setBody(seq);
		return copy;
	}
	
	private String getFreshTemp() {
		return FreshTempGenerator.getFreshTemp();
	}
}
