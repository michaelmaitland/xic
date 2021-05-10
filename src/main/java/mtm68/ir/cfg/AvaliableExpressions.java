package mtm68.ir.cfg;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.util.SetUtils;

public class AvaliableExpressions {

	private Graph<IRData<AvailableData>> graph;
	
	public void performAvaliableExpressionsAnalysis(IRCompUnit ir) {
		//IRCFGBuilder<AvailableData> builder = new IRCFGBuilder<>();
		//graph = builder.buildCFG(ir, AvailableData::new);
		List<Node> nodes = graph.getNodes();
		
		boolean changes = true;
		while (changes) {
			changes = false;
			
			for(Node node : nodes) {
				IRData<AvailableData> data = graph.getDataForNode(node);
				IRStmt stmt = data.getIR();
				AvailableData flowData = data.getFlowData();
				
				Set<IRExpr> inOld = flowData.getIn();
				Set<IRExpr> outOld = flowData.getOut();
				
				Set<IRExpr> gen;
				Set<IRExpr> kill;
				
				Set<IRExpr> in = in(node);
				Set<IRExpr> out = out(node);
				
				flowData.setIn(in);
				flowData.setOut(out);
				
				changes = changes || (!inOld.equals(in) || !outOld.equals(out));
			}
		}
	}
	
	/**
	 * The set of available expressions on edges entering node n.
	 * in[n] = expressions available on all edges into n
	 */
	private Set<IRExpr> in(Node node) {
		Set<IRExpr> in = SetUtils.empty();
		for(Node pred : node.pred()) {
			Set<IRExpr> predData = graph.getDataForNode(pred)
											   .getFlowData()
											   .getOut();
			in = SetUtils.intersect(in, predData);
		}
		
		return in;
	}

	/**
	 * The set of available expressions on edges leaving node n.
	 * out[n] = in[n] U exprs(n) - kill(n)
	 */
	 private Set<IRExpr> out(Node n) {
		Set<IRExpr> in = in(n);
		Set<IRExpr> exprs = exprs(n);
		Set<IRExpr> kill = kill(n);
		
		return SetUtils.difference(
			       SetUtils.union(in, exprs), 
				   kill
			   );
	 }
	
	/**
	 * Expressions evaluated by a node.
	 * 
	 * For example,
	 * exprs(x <- e) 	   = e and all subexpressions or e
	 * exprs([e1] <- [e2]) = [e2], [e1] and subexpressions
	 * exprs(x <- f(es))   = forall e in es, e and its subexprs
	 * exprs(if e)         = e and its subexprs
	 */
	 private Set<IRExpr> exprs(Node node) {
		Set<IRExpr> exprs = SetUtils.empty();
		return exprs;
	 }
	 
	
	/**
	 * Expressions killed by a node.
	 * 
	 * For example,
	 * kill(x <- e)       = all exprs containing x
	 * kill([e1] <- [e2]) = all exprs [e'] that can alias [e1]
	 * kill(x <- f(es))   = exprs containing x and expressions [e'] 
	 * 					    that could be changed by function call to f
	 * kill(if e)         = {}
	 */
	 private Set<IRExpr> kill(Node node) {
		Set<IRExpr> kill = SetUtils.empty();
		return kill;
	 }
	
	public static class AvailableData {
		Set<IRExpr> in;
		Set<IRExpr> out;
		Node definingNode;
		
		public AvailableData(Node node) {
			in = SetUtils.empty();
			out = SetUtils.empty();
			this.definingNode = node;
		}

		public Set<IRExpr> getIn() {
			return in;
		}

		public void setIn(Set<IRExpr> in) {
			this.in = in;
		}

		public Set<IRExpr> getOut() {
			return out;
		}

		public void setOut(Set<IRExpr> out) {
			this.out = out;
		}

		public Node getDefiningNode() {
			return definingNode;
		}

		public void setDefiningNode(Node definingNode) {
			this.definingNode = definingNode;
		}
	}
}
