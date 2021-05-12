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
import edu.cornell.cs.cs4120.ir.IRNode;
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

public class AvailableExpressions {

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
		boolean first = true;
		for(Node pred : node.pred()) {
			Set<IRExpr> predData = graph.getDataForNode(pred)
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
	 * exprs([e1] <- e2)    = {}
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
	 * kill([e1] <- e2)   = all mem exprs
	 * kill([e1] <- [e2]) = all exprs [e'] that can alias [e1]
	 * kill(x <- f(es))   = exprs containing x and expressions [e'] 
	 * 					    that could be changed by function call to f
	 * kill(if e)         = {}
	 */
	private Set<IRExpr> kill(Node node, Set<IRExpr> l) {
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
	private Set<IRExpr> killXGetsE(IRMove ir, Set<IRExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		return l.stream().filter(e -> e.containsExpr(temp)).collect(Collectors.toSet());
	}

	/**
	 * The subset of IRExpr in l that could alias IRMem(e1).
	 * 
	 * Without performing alias analysis, we must consider all  
	 * memory access as aliasing IRMem(e1).
	 * 
	 * @param ir of the form IRMove(IRMem e1, IRMem e2)
	 */
	private Set<IRExpr> killMemE1GetsMemE2(IRMove ir, Set<IRExpr> l) {
return l.stream()
				.filter(IRNode::isContainsMemSubexpr)
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
	private Set<IRExpr> killXGetsF(IRMove ir, Set<IRExpr> l) {
		IRTemp temp = (IRTemp)ir.target();
		// TODO: this is wrong. need to add to kill all mem subexprs unless we do aliasing
		return l.stream().filter(e -> e.containsExpr(temp)).collect(Collectors.toSet());
	}
	
	/**
	 * The subset of IRExpr in l of form M[x] forall x.
	 * @param ir of the form IRMove(IRMem e1, IRExpr e) where e not an IRMem
	 */
	private Set<IRExpr> killMemEGetsE(IRMove ir, Set<IRExpr> l) {
		return l.stream()
				.filter(IRNode::isContainsMemSubexpr)
				.collect(Collectors.toSet());
	}


	private Set<IRExpr> killIfE(IRCJump ir, Set<IRExpr> l) {
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
		Set<IRExpr> liveIn = data.getFlowData().getIn();
		Set<IRExpr> liveOut = data.getFlowData().getOut();
		IRStmt ir = data.getIR();

		StringBuilder sb = new StringBuilder();

		sb.append("In: ");
		sb.append(setToString(liveIn));
		sb.append("\\n");
		
		sb.append(ir.toString());
		sb.append("\\n");

		sb.append("Out: ");
		sb.append(setToString(liveOut));

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
		Set<IRExpr> in;
		Set<IRExpr> out;
		
		public AvailableData() {
			in = SetUtils.empty();
			out = SetUtils.empty();
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
	}
}
