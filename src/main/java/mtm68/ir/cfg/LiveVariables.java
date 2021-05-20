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
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.cfg.Graph;
import mtm68.assem.cfg.Graph.Node;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.util.SetUtils;

public class LiveVariables {

	private Graph<IRData<LiveData>> graph;
	private IRCFGBuilder<LiveData> builder;
	
	private IRFuncDefn ir;

	public LiveVariables(IRFuncDefn ir) {
		builder = new IRCFGBuilder<>();
		this.ir = ir;
	}
	
	public void performAnalysis() {

		IRStmt body = ir.body();
	    List<IRStmt> stmts = ((IRSeq)body).stmts();

		graph = builder.buildIRCFG(stmts, LiveData::new);
		List<Node> nodes = graph.getNodes();
		
		boolean changes = true;
		while (changes) {
			changes = false;
			
			for(Node node : nodes) {
				IRData<LiveData> data = graph.getDataForNode(node);
				LiveData flowData = data.getFlowData();
				
				Set<LiveVar> inOld = flowData.getIn();
				Set<LiveVar> outOld = flowData.getOut();
				
				Set<LiveVar> in = in(node);
				Set<LiveVar> out = out(node);
				
				flowData.setIn(in);
				flowData.setOut(out);
				
				changes = changes || (!inOld.equals(in) || !outOld.equals(out));
			}
		}
	}
	
	/**
	 * The set of live on entry.
	 * in[n] = use(n) U (out(n) - def(n))
	 */
	private Set<LiveVar> in(Node node) {
		Set<LiveVar> use = use(node);
		Set<LiveVar> out = out(node);
		Set<LiveVar> def = def(node);
		
		Set<LiveVar> difference = SetUtils.difference(out, def);
		 
		return SetUtils.union(use, difference);
	}

	/**
	 * The set of live variables on edges leaving node n.
	 * out[n] = vars live on entry to any successor node
	 */
	 private Set<LiveVar> out(Node node) {
		Set<LiveVar> out= SetUtils.empty();
		for(Node succ: node.succ()) {
			Set<LiveVar> succData = graph.getDataForNode(succ)
											   .getFlowData()
											   .getOut();
			out = SetUtils.union(out, succData);
		}

		return out;
	 }

	/**
	 * Variables used by a node.
	 * 
	 * For example,
	 * use(x <- e)       = use(e)
	 * use([e1] = e2)    = use(e1) U use(e2)
	 * use(x <- f(es))   = forall e in es, U use(e) 
	 * use(if(e))   	 = use(e)
	 */
	private Set<LiveVar> use(Node node) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return useXGetsE((IRMove)ir, node);

		} else if(hasMemE1GetsE2Form(ir)) {
			return useMemE1GetsE2((IRMove)ir, node);

		} else if(hasXGetsFForm(ir)) {
			return useXGetsF((IRCallStmt)ir, node);

		} else if(hasIfEForm(ir)) {
			return useIfE((IRCJump)ir, node);

		} else {
			return SetUtils.empty();
		}
	}

	private Set<LiveVar> useXGetsE(IRMove mov, Node node) {
		return mov.source()
				  .use()
				  .stream()
				  .map(LiveVar::new)
				  .collect(Collectors.toSet());
	}

	private Set<LiveVar> useMemE1GetsE2(IRMove mov, Node node) {
		IRMem mem = (IRMem)mov.target();
		return SetUtils.union(mem.use(), mov.source().use())
				.stream()
				.map(LiveVar::new)
				.collect(Collectors.toSet());
	}

	private Set<LiveVar> useXGetsF(IRCallStmt call, Node node) {
		return call.use()
				   .stream()
				   .map(LiveVar::new)
				   .collect(Collectors.toSet());
	}

	private Set<LiveVar> useIfE(IRCJump jmp, Node node) {
		return jmp.cond()
				  .use()
				  .stream()
				  .map(LiveVar::new)
				  .collect(Collectors.toSet());
	}

	/**
	 * Expressions def by a node 
	 * 
	 * For example,
	 * def(x <- e)    = x
	 * else		      = {}	
	 */
	private Set<LiveVar> def(Node node) {
		IRStmt ir = graph.getDataForNode(node).getIR();

		if (hasXGetsEForm(ir)) {
			return defXGetsE((IRMove)ir);

		} else {
			return SetUtils.empty();
		}
	}
	
	private Set<LiveVar> defXGetsE(IRMove ir) {
		IRTemp temp = (IRTemp)ir.target();
		return SetUtils.elems(temp).stream()
								   .map(LiveVar::new)
								   .collect(Collectors.toSet());
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

	public Graph<IRData<LiveData>> getGraph() {
		return graph;
	}
	
	public void showGraph(Writer writer) throws IOException {
		graph.show(writer, "AvailableExpressions", true, this::showAvailable);
	}
	
	public IRCFGBuilder<LiveData> getBuilder() {
		return builder;
	}

	private String showAvailable(IRData<LiveData> data) {
		Set<LiveVar> in = data.getFlowData().getIn();
		Set<LiveVar> out = data.getFlowData().getOut();
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
	
	public static class LiveData {
		Set<LiveVar> in;
		Set<LiveVar> out;
		
		public LiveData() {
			in = SetUtils.empty();
			out = SetUtils.empty();
		}

		public Set<LiveVar> getIn() {
			return in;
		}

		public void setIn(Set<LiveVar> in) {
			this.in = in;
		}

		public Set<LiveVar> getOut() {
			return out;
		}

		public void setOut(Set<LiveVar> out) {
			this.out = out;
		}
	}

	public static class LiveVar {
		IRTemp a;
		
		public LiveVar(IRTemp a) {
			this.a = a;
		}

		public IRTemp getA() {
			return a;
		}

		public void setA(IRTemp a) {
			this.a = a;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
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
			LiveVar other = (LiveVar) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			return true;
		}
	}
}
