package mtm68.ir.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.CFGBuilder.CFGEdge;
import mtm68.ir.cfg.CFGBuilder.CFGNode;

public class CFGTracer {
	
	private List<IRStmt> stmts;
	private Set<CFGNode> unmarked;
	private Map<Integer, CFGNode> nodeMap;
	
	public CFGTracer(List<CFGNode> nodes, List<IRStmt> stmts) {
		this.stmts = stmts;
		
		// For convenience, we will sort the unmarked nodes by their index
		// giving us a (semi-)predictable order in which the traces are constructed.
		unmarked = new TreeSet<>((n1, n2) -> n1.getNodeIdx() - n2.getNodeIdx());
		unmarked.addAll(nodes);
		
		nodeMap = nodes.stream()
				.collect(Collectors.toMap(CFGNode::getNodeIdx, n -> n));
	}

	public List<IRStmt> performReordering() {
		List<IRStmt> result = new ArrayList<>();

		while(!unmarked.isEmpty()) {
			CFGNode node = unmarked.iterator().next();
			List<CFGNode> trace = maximalTrace(node);
			unmarked.removeAll(trace);
			printTrace(trace);
			
			addStmtsFromTraceToResult(result, trace);
		}
		return result;
	}
	
	
	private void addStmtsFromTraceToResult(List<IRStmt> result, List<CFGNode> trace) {
		for(int i = 0; i < trace.size(); i++) {
			CFGNode node = trace.get(i);
			CFGNode next = i + 1 < trace.size() ? trace.get(i + 1) : null;
			
			int startIdx = node.getStmtIdx();
			int endIdx = getBasicBlockEndIdx(node);
			
			List<IRStmt> basicBlock = stmts.subList(startIdx, endIdx);
			
			IRStmt jumpStmt = node.getJumpStmt().orElse(null);
			
			if(jumpStmt instanceof IRJump) {
				removeUnnecessaryJump(basicBlock, node, next);
			} else if (jumpStmt instanceof IRCJump) {
				IRCJump jump = (IRCJump) jumpStmt;

				removeFalseCaseFromCJump(jump, basicBlock, node, next);
				repairCJumpWithNoFallthrough(jump, basicBlock, node, next);
			}
			
			addJumpIfNoFallthrough(basicBlock, node, next);

			result.addAll(basicBlock);
		}
	}

	private void addJumpIfNoFallthrough(List<IRStmt> basicBlock, CFGNode node, CFGNode next) {
		if(next != null 
			|| node.getJumpStmt().isPresent() 
			|| node.getOut().isEmpty()) return;
		
		// This is a node with the following properties:
		// 1. It ends a trace
		// 2. It doesn't have a jump statement
		// 3. It has an outgoing connection
		//
		// Therefore we can deduce that this node must have had
		// a fallthrough to a labeled node. 
		CFGNode fallthrough = node.getOut().get(0).getTo();
		String fallthroughLabel = fallthrough.getLabel().get();
		
		IRJump jump = new IRJump(new IRName(fallthroughLabel));
		basicBlock.add(jump);
	}

	private void repairCJumpWithNoFallthrough(IRCJump jump, List<IRStmt> basicBlock, CFGNode node, CFGNode next) {
		if(next != null) return;
		
		IRJump addedJump = new IRJump(new IRName(jump.falseLabel()));
		
		jump = jump.removeFalseLabel();

		basicBlock.set(node.getJumpStmtOff(), jump);
		basicBlock.add(addedJump);
	}

	private void removeFalseCaseFromCJump(IRCJump jump, List<IRStmt> basicBlock, CFGNode node, CFGNode next) {
		if(next == null) return;
		
		String fallthroughLabel = next.getLabel().get();
		
		if(fallthroughLabel.equals(jump.trueLabel())) {
			jump = jump.negateCondition();
		}
		jump = jump.removeFalseLabel();
		basicBlock.set(node.getJumpStmtOff(), jump);
	}

	// The only way "node -> next" can appear in the trace is if
	// the jump statement from "node" goes to "next", so we can always
	// delete the jump statement then so long as next exists.
	private void removeUnnecessaryJump(List<IRStmt> basicBlock, CFGNode node, CFGNode next) {
		if(next == null) return;
		
		basicBlock.remove(node.getJumpStmtOff());
	}

	private int getBasicBlockEndIdx(CFGNode node) {
		int nextNodeIdx = node.getNodeIdx() + 1;
		
		CFGNode nextNode = nodeMap.get(nextNodeIdx);
		
		return nextNode == null ? stmts.size() : nextNode.getStmtIdx();
	}

	private void printTrace(List<CFGNode> trace) {
		String out = trace.stream()
			.map(CFGNode::getNodeIdx)
			.map(Object::toString)
			.collect(Collectors.joining(","));

		System.out.println("Trace: " + out);
	}
	

	private List<CFGNode> maximalTrace(CFGNode node) {
		List<CFGNode> trace = new ArrayList<>();
		trace.add(node);
		
		Set<CFGNode> inTrace = new HashSet<>();
		inTrace.add(node);
		
		CFGNode curr = node;
		
		while(true) {
			Set<CFGNode> nextNodes = curr.getOut().stream()
					.map(CFGEdge::getTo)
					.collect(Collectors.toSet());
			
			nextNodes.retainAll(unmarked);
			nextNodes.removeIf(inTrace::contains);
			
			// No more nodes to consider
			if(nextNodes.isEmpty()) break;
			
			CFGNode next = nextNodes.iterator().next();
			trace.add(next);
			inTrace.add(next);

			curr = next;
		}
		
		return trace;
	}
}
