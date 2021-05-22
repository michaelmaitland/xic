package mtm68.ir.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.util.SetUtils;

public class AvailableExprs {

	private Graph<IRData<AvailableData>> graph;
	private IRCFGBuilder<AvailableData> builder;
	
	private IRFuncDefn ir;
	private IRNodeFactory f;

	public AvailableExprs(IRFuncDefn ir, IRNodeFactory f) {
		builder = new IRCFGBuilder<>();
		this.ir = ir;
		this.f = f;
	}
	
	public void performAnalysis() {

		// need this data for kill
		IRFuncDefn visitedIr = (IRFuncDefn)new IRContainsMemSubexprDecorator(f).visit(ir);
		// need formated as flat list
		IRStmt body = visitedIr.body();
	    List<IRStmt> stmts = ((IRSeq)body).stmts();

		graph = builder.buildIRCFG(stmts, AvailableData::new);
		List<Node> nodes = graph.getNodes();
		
		boolean changes = true;
		while (changes) {
			changes = false;
			
			for(Node node : nodes) {
				IRData<AvailableData> data = graph.getDataForNode(node);
				AvailableData flowData = data.getFlowData();
				
				
				Set<AvailableExpr> inOld = flowData.getIn();
				Set<AvailableExpr> outOld = flowData.getOut();
				
				Set<AvailableExpr> exprs = exprs(node);
				Set<AvailableExpr> in = in(node);
				Set<AvailableExpr> out = out(node, exprs);
				
				flowData.setIn(in);
				flowData.setOut(out);
				flowData.setExprs(exprs.stream()
									   .map(AvailableExpr::getExpr)
									   .collect(Collectors.toSet()));
				
				flowData.isTop = false;
				changes = changes || (!inOld.equals(in) || !outOld.equals(out));
			}
		}
	}
	
	/**
	 * The set of available expressions on edges entering node n.
	 * in[n] = expressions available on all edges into n
	 */
	private Set<AvailableExpr> in(Node node) {
		Set<AvailableExpr> in = SetUtils.empty();
		boolean first = true;
		for(Node pred : node.pred()) {
			AvailableData predData = graph.getDataForNode(pred)
											   .getFlowData();
			Set<AvailableExpr> out = predData.getOut();
			/**
			* The first iteration, we need to set the base set 
			* because intersect with empty set will always be empty
			* and there is no good way to set the initial set
			* as the empty set
			*/
			if(predData.isTop) continue;
			if(first) {
				in = SetUtils.copy(out);
				first = false;
			} else {
				in = SetUtils.intersect(in, out);
			}
		}
		
		return in;
	}

	/**
	 * The set of available expressions on edges leaving node n.
	 * out[n] = in[n] U exprs(n) - kill(n)
	 */
	 private Set<AvailableExpr> out(Node n, Set<AvailableExpr> exprs) {
		Set<AvailableExpr> in = in(n);
		Set<AvailableExpr> kill = kill(n, in);
		
		/** 
		 * The definer of exprs and in will be different so 
		 * with a typical set union both cases will be added
		 * to the set. We only want to take from in, if the same
		 * IRExpr exists in exprs and in.
		 */
		Set<AvailableExpr> inUExprsWithCorrectDefs = 
				inUnionExprs(in, exprs);
		
		return SetUtils.difference(inUExprsWithCorrectDefs, kill);
	 }
	 
	 /**
	  * Returns the union of in and exprs where element equality is defined
	  * by the equality of the IRExpr. If in and exprs contain the same IRExpr,
	  * then the AvaliableExpr belonging to the in set is part of the union
	  * and the one from the exprs set is not.
	  */
	 private Set<AvailableExpr> inUnionExprs(Set<AvailableExpr> in, Set<AvailableExpr> exprs) {
		 Set<IRExpr> inExprs = in.stream()
				 .map(AvailableExpr::getExpr)
				 .collect(Collectors.toSet());
		 
		 Set<AvailableExpr> exprsToAdd = exprs.stream().filter(e -> {
			return !inExprs.contains(e.getExpr());
		 })
		 .collect(Collectors.toSet());
		 
		 return SetUtils.union(in, exprsToAdd);
	 }
	
	/**
	 * Expressions evaluated by a node.
	 * 
	 * For example,
	 * exprs(x <- e) 	   = e and all subexprs of e
	 * exprs([e1] <- e2)   = [e1], e2 and subexps of [e1] and e2
	 * exprs(x <- f(es))   = forall e in es, e and its subexprs
	 * exprs(if e)         = e and its subexprs
	 */
	private Set<AvailableExpr> exprs(Node node) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return exprsXGetsE((IRMove)ir, node);

		} else if(hasMemE1GetsE2Form(ir)) {
			return exprsMemE1GetsE2((IRMove)ir, node);

		} else if(hasXGetsFForm(ir)) {
			return exprsXGetsF((IRCallStmt)ir, node);

		} else if(hasIfEForm(ir)) {
			return exprsIfE((IRCJump)ir, node);

		} else {
			return SetUtils.empty();
		}
	}

	private Set<AvailableExpr> exprsXGetsE(IRMove mov, Node d) {
		return mov.source().genAvailableExprs()
			       .stream()
			       .map(e -> new AvailableExpr(e, d))
				   .collect(Collectors.toSet());
	}

	private Set<AvailableExpr> exprsMemE1GetsE2(IRMove mov, Node d) {
		return SetUtils.union(mov.source().genAvailableExprs(), mov.target().genAvailableExprs())
			       .stream()
			       .map(e -> new AvailableExpr(e, d))
			       .collect(Collectors.toSet());
	}

	private Set<AvailableExpr> exprsXGetsF(IRCallStmt call, Node d) {
		return call.genAvailableExprs()
			       .stream()
			       .map(e -> new AvailableExpr(e, d))
			       .collect(Collectors.toSet());
	}

	private Set<AvailableExpr> exprsIfE(IRCJump jmp, Node d) {
		return jmp.cond().genAvailableExprs()
			       .stream()
			       .map(e -> new AvailableExpr(e, d))
			       .collect(Collectors.toSet());
	}
	
	/**
	 * Expressions killed by a node that has facts l.
	 * 
	 * For example,
	 * kill(x <- e)       = all exprs containing x
	 * kill([e1] <- e2)   = all mem exprs
	 * kill([e1] <- [e2]) = all exprs [e'] that can alias [e1] (we don't do alias analysis so we're killing all mem exprs)
	 * kill(x <- f(es))   = exprs containing x and expressions [e'] 
	 * 					    that could be changed by function call to f
	 * kill(if e)         = {}
	 */
	private Set<AvailableExpr> kill(Node node, Set<AvailableExpr> l) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return killXGetsE((IRMove)ir, l);

		} else if (hasMemE1GetsE2Form(ir)) {
			return killMemE1GetsE2((IRMove)ir, l);

		} else if(hasXGetsFForm(ir)) {
			return killXGetsF((IRCallStmt)ir, l);

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
	private Set<AvailableExpr> killXGetsE(IRMove ir, Set<AvailableExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		return l.stream()
				.filter(e -> e.getExpr().containsExpr(temp))
				.collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that could alias IRMem(e1).
	 * 
	 * Without performing alias analysis, we must consider all  
	 * memory access as aliasing IRMem(e1).
	 * 
	 * @param ir of the form IRMove(IRMem e1, IRMem e2)
	 */
	private Set<AvailableExpr> killMemE1GetsE2(IRMove ir, Set<AvailableExpr> l) {
		return l.stream()
				.filter(e -> e.getExpr().doesContainsMutableMemSubexpr())
				.collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that contains any arguments IRMem(e')
	 * The x <- f(e) kills x by the subsequent IRStmt that moves RET_i into
	 * a temp.
	 * @param ir of the form IRCallStmt c. 
	 */
	private Set<AvailableExpr> killXGetsF(IRCallStmt ir, Set<AvailableExpr> l) {
		return l.stream()
				.filter(e -> e.getExpr().doesContainsMutableMemSubexpr())
				.collect(Collectors.toSet());
	}
	
	/**
	 * The empty set.
	 * @param ir of the form IRCJump(IRExpr e, l1, l2)
	 */
	private Set<AvailableExpr> killIfE(IRCJump ir, Set<AvailableExpr> l) {
		return SetUtils.empty();
	}

	private boolean hasXGetsEForm(IRStmt ir) {
		return ir instanceof IRMove 
				&& ((IRMove)ir).target() instanceof IRTemp
				&& ((IRMove)ir).source() instanceof IRExpr;
	}

	private boolean hasMemE1GetsE2Form(IRStmt ir) {
		return ir instanceof IRMove
			&& ((IRMove)ir).target() instanceof IRMem
			&& ((IRMove)ir).source() instanceof IRExpr;
	}

	private boolean hasXGetsFForm(IRStmt ir) {
		return ir instanceof IRCallStmt;
	}

	private boolean hasIfEForm(IRStmt ir) {
		return ir instanceof IRCJump; 
	}
	
	public Graph<IRData<AvailableData>> getGraph() {
		return graph;
	}
	
	public void showGraph(Writer writer) throws IOException {
		graph.show(writer, "AvailableExpressions", true, this::showAvailable);
	}
	
	public IRCFGBuilder<AvailableData> getBuilder() {
		return builder;
	}

	private String showAvailable(IRData<AvailableData> data) {
		Set<AvailableExpr> in = data.getFlowData().getIn();
		Set<AvailableExpr> out = data.getFlowData().getOut();
		IRStmt ir = data.getIR();

		StringBuilder sb = new StringBuilder();

		sb.append("In: ");
		sb.append(setToString(in));
		sb.append("\\n");
		
		sb.append(ir.toString());
		sb.append("\\n");

		sb.append("Out: ");
		sb.append(setToString(out));

		return sb.toString();
	}
	
	private <T> String setToString(Set<T> set) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		String elems = set.stream()
			.map(Object::toString)
			.collect(Collectors.joining(","));

		sb.append(elems);

		sb.append('}');
		
		return sb.toString();
	}
	
	public static class AvailableData {
		Set<AvailableExpr> in;
		Set<AvailableExpr> out;
		Set<IRExpr> exprs;
		boolean isTop = true;
		
		public AvailableData() {
			in = SetUtils.empty();
			out = SetUtils.empty();
			exprs = SetUtils.empty();
		}

		public Set<AvailableExpr> getIn() {
			return in;
		}

		public void setIn(Set<AvailableExpr> in) {
			this.in = in;
		}

		public Set<AvailableExpr> getOut() {
			return out;
		}

		public void setOut(Set<AvailableExpr> out) {
			this.out = out;
		}

		public Set<IRExpr> getExprs() {
			return exprs;
		}

		public void setExprs(Set<IRExpr> exprs) {
			this.exprs = exprs;
		}
	}

	public static class AvailableExpr {
		IRExpr expr;
		Node definer;
		
		public AvailableExpr(IRExpr expr, Node definer) {
			this.expr = expr;
			this.definer = definer;
		}
		
		public IRExpr getExpr() {
			return expr;
		}
		public void setExpr(IRExpr expr) {
			this.expr = expr;
		}
		public Node getDefiner() {
			return definer;
		}
		public void setDefiner(Node definer) {
			this.definer = definer;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("[Expr: ");
			sb.append(expr.toString());

			sb.append(", DefNode: ");
			sb.append(definer.getNodeId());

			sb.append("]");
			
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((definer == null) ? 0 : definer.hashCode());
			result = prime * result + ((expr == null) ? 0 : expr.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AvailableExpr other = (AvailableExpr) obj;
			if (definer == null) {
				if (other.definer != null)
					return false;
			} else if (!definer.equals(other.definer))
				return false;
			if (expr == null) {
				if (other.expr != null)
					return false;
			} else if (!expr.equals(other.expr))
				return false;
			return true;
		}
		
	}
}
