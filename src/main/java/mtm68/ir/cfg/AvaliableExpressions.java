package mtm68.ir.cfg;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.util.SetUtils;

public class AvaliableExpressions {

	private Graph<IRData<AvailableData>> graph;
	
	public void performAvaliableExpressionsAnalysis(IRCompUnit ir) {
		IRCFGBuilder<AvailableData> builder = new IRCFGBuilder<>();
		graph = builder.buildCFG(ir, AvailableData::new);
		List<Node> nodes = graph.getNodes();
		
		boolean changes = true;
		while (changes) {
			changes = false;
			
			for(Node node : nodes) {
				IRData<AvailableData> data = graph.getDataForNode(node);
				AvailableData flowData = data.getFlowData();
				
				Set<IRExpr> inOld = flowData.getIn();
				Set<IRExpr> outOld = flowData.getOut();
				
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
		Set<IRExpr> kill = kill(n, in);
		
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
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return exprsXGetsE((IRMove)ir);

		} else if(hasMemE1GetsMemE2Form(ir)) {
			return exprsMemE1GetsMemE2((IRMove)ir);

		} else if(hasXGetsFForm(ir)) {
			return exprsXGetsF((IRMove)ir);

		} else if(hasIfEForm(ir)) {
			return exprsIfE((IRCJump)ir);

		} else {
			return SetUtils.empty();
		}
	}

	private Set<IRExpr> exprsXGetsE(IRMove mov) {
		return mov.source().genAvailableExprs();
	}

	private Set<IRExpr> exprsMemE1GetsMemE2(IRMove mov) {
		return SetUtils.union(mov.source().genAvailableExprs(), mov.target().genAvailableExprs());
	}

	private Set<IRExpr> exprsXGetsF(IRMove ir) {
		IRCallStmt call = (IRCallStmt) ir.source();
		return call.genAvailableExprs();
	}

	private Set<IRExpr> exprsIfE(IRCJump jmp) {
		return jmp.cond().genAvailableExprs();
	}
	
	/**
	 * Expressions killed by a node that has facts l.
	 * 
	 * For example,
	 * kill(x <- e)       = all exprs containing x
	 * kill([e1] <- [e2]) = all exprs [e'] that can alias [e1]
	 * kill(x <- f(es))   = exprs containing x and expressions [e'] 
	 * 					    that could be changed by function call to f
	 * kill(if e)         = {}
	 */
	private Set<IRExpr> kill(Node node, Set<IRExpr> l) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return killXGetsE((IRMove)ir, l);

		} else if(hasMemE1GetsMemE2Form(ir)) {
			return killMemE1GetsMemE2((IRMove)ir, l);

		} else if(hasXGetsFForm(ir)) {
			return killXGetsF((IRMove)ir, l);

		} else if(hasIfEForm(ir)) {
			return killIfE((IRCJump)ir,l);

		} else {
			return SetUtils.empty();
		}
	}

	/**
	 * The subset of IRExpr in l that contains t.
	 * @param ir of the form IRMove(IRTemp t, IRExpr)
	 */
	private Set<IRExpr> killXGetsE(IRMove ir, Set<IRExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		return l.stream().filter(e -> e.containsExpr(temp)).collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that could alias IRMem(e1).
	 * @param ir of the form IRMove(IRMem e1, IRMem e2)
	 */
	private Set<IRExpr> killMemE1GetsMemE2(IRMove ir, Set<IRExpr> l) {
		IRMem e1 = (IRMem)ir.target();
		// TODO: this is wrong. need to add to kill all mem subexprs unless we do aliasing
		return l.stream().filter(e -> e.containsExpr(e1)).collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that contains x and any arguments IRMem(e')
	 * @param ir of the form IRMove(IRMem e1, IRCallStmt c)
	 */
	private Set<IRExpr> killXGetsF(IRMove ir, Set<IRExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		// TODO: this is wrong. need to add to kill all mem subexprs unless we do aliasing
		return l.stream().filter(e -> e.containsExpr(temp)).collect(Collectors.toSet());
	}

	private Set<IRExpr> killIfE(IRCJump ir, Set<IRExpr> l) {
		return SetUtils.empty();
	}

	private boolean hasXGetsEForm(IRStmt ir) {
		return ir instanceof IRMove 
				&& ((IRMove)ir).target() instanceof IRTemp
				&& ((IRMove)ir).source() instanceof IRExpr;
	}

	private boolean hasMemE1GetsMemE2Form(IRStmt ir) {
		return ir instanceof IRMove 
				&& ((IRMove)ir).target() instanceof IRMem 
				&& ((IRMove)ir).source() instanceof IRMem;
	}

	private boolean hasXGetsFForm(IRStmt ir) {
		return ir instanceof IRMove 
			&& ((IRMove)ir).target() instanceof IRTemp
			&& ((IRMove)ir).source() instanceof IRCallStmt;
	}

	private boolean hasIfEForm(IRStmt ir) {
		return ir instanceof IRCJump; 
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
