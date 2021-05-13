package mtm68.ir.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRCompUnit;
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
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class AvailableExprs {

	private Graph<IRData<AvailableData>> graph;
	
	public void performAvaliableExpressionsAnalysis(IRCompUnit ir, IRNodeFactory f) {

		// need data for kill
		IRCompUnit visitedIr = (IRCompUnit)new IRContainsMemSubexprDecorator(f).visit(ir);

		IRCFGBuilder<AvailableData> builder = new IRCFGBuilder<>();
		
		List<IRStmt> stmts = ArrayUtils.empty();
		for(IRFuncDefn func : visitedIr.functions().values()) {
			IRStmt body = func.body();
			List<IRStmt> fstmts = ((IRSeq)body).stmts();
			ArrayUtils.concat(stmts, fstmts);
		}

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
			Set<AvailableExpr> predData = graph.getDataForNode(pred)
											   .getFlowData()
											   .getOut();
			/*
			* The first iteration, we need to set the base set 
			* because intersect with empty set will always be empty
			* and there is no good way to set the initial set
			* as the empty set
			*/
			if(first) {
				in = SetUtils.copy(predData);
				first = false;
			} else {
				in = SetUtils.intersect(in, predData);
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
	 * exprs([e1] <- e2)    = {}
	 * exprs([e1] <- [e2]) = [e2], [e1] and subexpressions
	 * exprs(x <- f(es))   = forall e in es, e and its subexprs
	 * exprs(if e)         = e and its subexprs
	 */
	private Set<AvailableExpr> exprs(Node node) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return exprsXGetsE((IRMove)ir, node);

		} else if(hasMemE1GetsMemE2Form(ir)) {
			return exprsMemE1GetsMemE2((IRMove)ir, node);

		} else if(hasXGetsFForm(ir)) {
			return exprsXGetsF((IRMove)ir, node);

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

	private Set<AvailableExpr> exprsMemE1GetsMemE2(IRMove mov, Node d) {
		return SetUtils.union(mov.source().genAvailableExprs(), mov.target().genAvailableExprs())
			       .stream()
				   .map(e -> new AvailableExpr(e, d))
				   .collect(Collectors.toSet());
	}

	private Set<AvailableExpr> exprsXGetsF(IRMove ir, Node d) {
		IRCallStmt call = (IRCallStmt) ir.source();
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
	 * kill([e1] <- [e2]) = all exprs [e'] that can alias [e1]
	 * kill(x <- f(es))   = exprs containing x and expressions [e'] 
	 * 					    that could be changed by function call to f
	 * kill(if e)         = {}
	 */
	private Set<AvailableExpr> kill(Node node, Set<AvailableExpr> l) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return killXGetsE((IRMove)ir, l);

		} else if (hasMemEGetsEForm(ir)) {
			return killMemEGetsE((IRMove)ir, l);

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
	private Set<AvailableExpr> killMemE1GetsMemE2(IRMove ir, Set<AvailableExpr> l) {
		return l.stream()
				.filter(e -> e.getExpr().isContainsMemSubexpr())
				.collect(Collectors.toSet());
//		IRMem e1 = (IRMem)ir.target();
//		return l.stream()
//				.filter(e -> e.containsExpr(e1))
//				.collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that contains x and any arguments IRMem(e')
	 * @param ir of the form IRMove(IRMem e1, IRCallStmt c)
	 */
	private Set<AvailableExpr> killXGetsF(IRMove ir, Set<AvailableExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		// TODO: this is wrong. need to add to kill all mem subexprs unless we do aliasing
		return l.stream()
				.filter(e -> e.getExpr().containsExpr(temp))
				.collect(Collectors.toSet());
	}
	
	/**
	 * The subset of IRExpr in l of form M[x] forall x.
	 * @param ir of the form IRMove(IRMem e1, IRExpr e) where e not an IRMem
	 */
	private Set<AvailableExpr> killMemEGetsE(IRMove ir, Set<AvailableExpr> l) {
		return l.stream()
				.filter(e -> e.getExpr().isContainsMemSubexpr())
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

	private boolean hasMemEGetsEForm(IRStmt ir) {
		return ir instanceof IRMove
			&& ((IRMove)ir).target() instanceof IRMem
			&& ((IRMove)ir).source() instanceof IRExpr
			&& !(((IRMove)ir).source() instanceof IRMem);
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

	public Graph<IRData<AvailableData>> getGraph() {
		return graph;
	}
	
	public void showGraph(Writer writer) throws IOException {
		graph.show(writer, "AvaliableExpressions", true, this::showAvailable);
	}

	
	public static class AvailableData {
		Set<AvailableExpr> in;
		Set<AvailableExpr> out;
		Set<IRExpr> exprs;
		
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
		boolean didTransformDefiner;
		
		public AvailableExpr(IRExpr expr, Node definer) {
			this.expr = expr;
			this.definer = definer;
			this.didTransformDefiner = false;
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
		
		public boolean isDidTransformDefiner() {
			return didTransformDefiner;
		}

		public void setDidTransformDefiner(boolean didTransformDefiner) {
			this.didTransformDefiner = didTransformDefiner;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("Expr: ");
			sb.append(expr.toString());

			sb.append(", DefNode: ");
			sb.append(expr.toString());
			sb.append(definer.getNodeId());
			
			return sb.toString();
		}
	}
}
