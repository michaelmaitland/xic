package mtm68.ir.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.util.ArrayUtils;

/**
 * Responsible for building the control flow graph following the IR
 * code being lowered. 
 * 
 * @author Scott
 */
public class CFGBuilder {
	
	private int nodeIdx;
	private int stmtIdx;
	private CFGNode currNode;
	private Map<Integer, CFGNode> nodeMap; // nodeIdx -> node
	private Map<Integer, CFGNode> stmtMap; // stmtIdx -> node
	private Map<String, Integer> labelMap; // label -> stmtIdx
	private Map<String, Set<CFGNode>> waitingNodes; // label -> Set<node>
	private CFGKind kind;
	private CFGMode mode;
	private String savedLabel;
	
	public CFGBuilder(CFGMode mode) {
		nodeIdx = 0;
		stmtIdx = 0;
		nodeMap = new HashMap<>();
		stmtMap = new HashMap<>();
		labelMap = new HashMap<>();
		waitingNodes = new HashMap<>();
		kind = CFGKind.RET;  
		this.mode = mode;
	}
	
	/**
	 * Call from the visitor to include the statement in the CFG
	 * @param stmt
	 */
	public void visitStatement(IRStmt stmt) {
		if(mode == CFGMode.BB) basicBlockAnalysis(stmt);
		else if (mode == CFGMode.STMT) stmtAnalysis(stmt);
	}
	
	private void basicBlockAnalysis(IRStmt stmt) {
		if(isLabel(stmt)) {
			createCFGNode(stmt);
			storeLabelLoc(stmt);
		}
		else if(kind == CFGKind.RET) {
			createCFGNode(stmt);
		} 
		
		if(isJump(stmt)) {
			currNode.addJumpStmt(stmt, stmtIdx);
			addOutboundConnections(stmt);
			kind = CFGKind.JMP;
		}
		else if(isReturn(stmt)) {
			kind = CFGKind.RET;
		}
		
		stmtIdx++;
	}

	private void stmtAnalysis(IRStmt stmt) {
		if(!(stmt instanceof IRJump)) {
			if(isLabel(stmt)) {
				assert(kind != CFGKind.LABEL); //Behavior not handled, but believed to be not possible
				savedLabel = ((IRLabel) stmt).name();
				kind = CFGKind.LABEL;
			}
			else if (kind == CFGKind.LABEL) {
				createCFGNode(stmt);
				storeLabelLoc(stmt, savedLabel);
			}
			else{ 
				createCFGNode(stmt);
			}
		}
		
		if(isJump(stmt)) {
			if(currNode != null) addOutboundConnections(stmt);
		}
		
		stmtIdx++;
	}
	
	/**
	 * Returns a list the CFG nodes
	 * @return
	 */
	public List<CFGNode> getNodes() {
		SortedSet<Integer> keys = new TreeSet<>(nodeMap.keySet());
		return keys.stream()
				.map(nodeMap::get)
				.collect(Collectors.toList());
	}
	
	public Map<Integer, CFGNode> getNodeMap() {
		return nodeMap;
	}
	
	private boolean isLabel(IRStmt stmt) {
		return stmt instanceof IRLabel;
	}
	
	private boolean isJump(IRStmt stmt) {
		return stmt instanceof IRCJump ||
				stmt instanceof IRJump;
	}

	private boolean isReturn(IRStmt stmt) {
		return stmt instanceof IRReturn;
	}

	private void storeLabelLoc(IRStmt stmt) {
		String label = ((IRLabel) stmt).name();
		storeLabelLoc(stmt, label);
	}
	
	private void storeLabelLoc(IRStmt stmt, String label) {
		labelMap.put(label, stmtIdx);
		currNode.addLabel(label);
		resolveWaitingNodes(label);
	}
	
	private void createCFGNode(IRStmt stmt) {
		CFGNode node = new CFGNode(stmt, nodeIdx, stmtIdx);
		nodeMap.put(nodeIdx, node);
		stmtMap.put(stmtIdx, node);
		nodeIdx++;

		if(currNode != null && !(currNode.getStmt() instanceof IRCJump)) addInboundConnections(node);

		if(mode == CFGMode.BB) 
			kind = CFGKind.LABEL;
		else if(mode == CFGMode.STMT)
			kind = CFGKind.DEF;
			
		currNode = node;
	}
	
	private void addOutboundConnections(IRStmt stmt) {
		Set<String> toLabels = labelsFromJump(stmt);
		toLabels.forEach(l -> this.addOutboundConnection(currNode, l));
	}
	
	private void addOutboundConnection(CFGNode from, String label) {
		if(labelMap.containsKey(label)) {
			Integer stmtIdx = labelMap.get(label);
			CFGNode to = stmtMap.get(stmtIdx);
			
			link(from, to);
		} else {
			addToWaitingNodes(label);
		}
	}
	
	private void resolveWaitingNodes(String label) {
		if(!waitingNodes.containsKey(label)) return;
		
		Set<CFGNode> waiting = waitingNodes.get(label);
		waitingNodes.remove(label);
		
		waiting.forEach(n -> this.addOutboundConnection(n, label));
	}
	
	private void addToWaitingNodes(String label) {
		if(!waitingNodes.containsKey(label)) {
			waitingNodes.put(label, new HashSet<>());
		}
		
		waitingNodes.get(label).add(currNode);
	}
	
	private Set<String> labelsFromJump(IRStmt stmt) {
		if(stmt instanceof IRJump) {
			IRJump jump = (IRJump) stmt;
			IRName name = (IRName) jump.target();
			return ArrayUtils.newHashSet(name.name());
		} else if (stmt instanceof IRCJump) {
			IRCJump cjump = (IRCJump) stmt;

			Set<String> set = ArrayUtils.newHashSet(cjump.trueLabel());
			if(cjump.falseLabel() != null) set.add(cjump.falseLabel());

			return set; 
		}
		
		// Maybe print a warning?
		return new HashSet<>();
	}
	
	private void addInboundConnections(CFGNode node) {
		switch(kind) {
		case LABEL:
			link(currNode, node);
			break;
		case DEF:
			link(currNode, node);
			break;
		case JMP:
		case RET:
		default:
			break;
		}
	}
	
	private void link(CFGNode from, CFGNode to) {
		CFGEdge edge = new CFGEdge(from, to);

		from.addOutgoing(edge);
		to.addIncoming(edge);
	}
	
	public static class CFGNode {
		private IRStmt stmt;
		private int nodeIdx;
		private int stmtIdx;

		private List<CFGEdge> in;
		private List<CFGEdge> out;
		
		private int jumpStmtOff;
		private Optional<IRStmt> jumpStmt;
		private Optional<String> label;

		public CFGNode(IRStmt stmt, int nodeIdx, int stmtIdx) {
			this.stmt = stmt;
			this.nodeIdx = nodeIdx;
			this.stmtIdx = stmtIdx;

			in = new ArrayList<>();
			out = new ArrayList<>();

			jumpStmt = Optional.empty();
			label = Optional.empty();
		}
		
		public void addIncoming(CFGEdge inbound) {
			in.add(inbound);
		}

		public void addOutgoing(CFGEdge outbound) {
			out.add(outbound);
		}
		
		public IRStmt getStmt() {
			return stmt;
		}
		
		public int getNodeIdx() {
			return nodeIdx;
		}
		
		public int getStmtIdx() {
			return stmtIdx;
		}
		
		public List<CFGEdge> getIn() {
			return in;
		}
		
		public List<CFGEdge> getOut() {
			return out;
		}
		
		public void addJumpStmt(IRStmt stmt, int jumpStmtIdx) {
			this.jumpStmt = Optional.of(stmt);
			this.jumpStmtOff = jumpStmtIdx - stmtIdx;
		}
		
		public void addLabel(String label) {
			this.label = Optional.of(label);
		}
		
		public Optional<IRStmt> getJumpStmt() {
			return jumpStmt;
		}
		
		public int getJumpStmtOff() {
			return jumpStmtOff;
		}
		
		public Optional<String> getLabel() {
			return label;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(nodeIdx);
			builder.append(" - ");
			builder.append(stmt);
			builder.append('[');
			builder.append(stmtIdx);
			builder.append(']');
			builder.append(" In: ");
			builder.append(edgesToString(in, true));
			builder.append(", Out: ");
			builder.append(edgesToString(out, false));
			
			jumpStmt.ifPresent(stmt -> {
				builder.append(", Jump["); 
				builder.append(jumpStmtOff);
				builder.append("]: ");
				builder.append(stmt);
			});
			
			return builder.toString().replaceAll("[\n\r]", "");
		}
		
		private String edgesToString(List<CFGEdge> edges, boolean from) {
			String str = edges.stream()
					.map(e -> from ? e.getFrom() : e.getTo())
					.map(n -> n.getNodeIdx())
					.map(Object::toString)
					.collect(Collectors.joining(","));
			return "[" + str + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + nodeIdx;
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
			CFGNode other = (CFGNode) obj;
			if (nodeIdx != other.nodeIdx)
				return false;
			return true;
		}
	}
	
	public static class CFGEdge {
		private CFGNode from;
		private CFGNode to;

		public CFGEdge(CFGNode from, CFGNode to) {
			this.from = from;
			this.to = to;
		}

		public CFGNode getFrom() {
			return from;
		}
		
		public CFGNode getTo() {
			return to;
		}
	}
	
	public static enum CFGKind {
		LABEL,
		RET,
		JMP,
		DEF
	}
	
	public static enum CFGMode{
		BB,
		STMT
	}
}
