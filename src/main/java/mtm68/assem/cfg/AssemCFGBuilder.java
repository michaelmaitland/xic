package mtm68.assem.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import mtm68.assem.Assem;
import mtm68.assem.JumpAssem;
import mtm68.assem.LabelAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.cfg.Graph.Node;
import mtm68.assem.operand.Loc;
import mtm68.util.ArrayUtils;
import polyglot.util.InternalCompilerError;

public class AssemCFGBuilder<T> {
	
	private Graph<AssemData<T>> graph;
	private	Node prev;
	private Node curr;
	private boolean prevWasLabel = false;
	private boolean prevWasUnconditionalJump = false;
	private boolean prevWasRet = false;
	private List<String> lastLabels;
	
	private Map<Loc, Node> locationMap;
	private Map<Loc, List<Node>> waitingJumps;
	private Map<Loc, List<Loc>> waitingLabels;
	
	public AssemCFGBuilder() {
		graph = new Graph<>();
		locationMap = new HashMap<>();
		waitingJumps = new HashMap<>();
		waitingLabels = new HashMap<>();

		lastLabels = ArrayUtils.empty();
	}
	
	public Graph<AssemData<T>> buildAssemCFG(List<Assem> assems, Supplier<T> flowDataConstructor) {
		for(Assem assem : assems) {
			
			if(isLabel(assem)) {
				handleLabel((LabelAssem)assem);
				continue;
			}

			if(isJump(assem)) {
				JumpAssem jump = (JumpAssem)assem;
				handleJump(jump);
				
				// A label followed by an unconditional jump should
				// be connected to the unconditional jumps target
				if(jump.isUnconditional() && prevWasLabel) {
					Node jumpTarget = locationMap.get(jump.getLoc());
					
					
					if(jumpTarget == null) {
						List<Loc> waiting = lastLabels.stream()
							.map(Loc::new)
							.collect(Collectors.toList());

						waitingLabels.put(jump.getLoc(), waiting);

						lastLabels.clear();
						prevWasLabel = false;
					} else {
						handleAfterLabel(jumpTarget);
					}
				}
				
				continue;
			}

			AssemData<T> data = new AssemData<>(assem, flowDataConstructor.get());
			curr = graph.createNode(data);
			
			if(prevWasLabel) {
				handleAfterLabel(curr);
			} 
			if(prev != null && !prevWasUnconditionalJump && !prevWasRet){
				graph.addEdge(prev, curr);
			}
			
			prev = curr;
			prevWasUnconditionalJump = false;
			prevWasRet = graph.getDataForNode(prev).getAssem() instanceof RetAssem;
		}
		
		if(waitingJumps.size() != 0) 
			throw new InternalCompilerError("Still have jumps that need resolving: " + waitingJumps);

		if(waitingLabels.size() != 0) 
			throw new InternalCompilerError("Still have labels that need resolving: " + waitingLabels);
		
		return graph;
	}
	
	private void handleAfterLabel(Node nodeAtLoc) {
		for(String label : lastLabels) {
			Loc loc = new Loc(label);
			locationMap.put(loc, nodeAtLoc);
			resolveWaitingJumps(loc);
		}
		prevWasLabel = false;
		lastLabels.clear();
	}
	
	private void handleJump(JumpAssem assem) {
		Loc jumpTo = assem.getLoc();
		prevWasUnconditionalJump = assem.isUnconditional();
		
		Node jumpTarget = locationMap.get(jumpTo);

		if(jumpTarget != null) {
			graph.addEdge(curr, jumpTarget);
		} else {
			addToWaitingJumps(jumpTo, curr);
		}
	}
	
	private void addToWaitingJumps(Loc loc, Node node) {
		if(!waitingJumps.containsKey(loc)) {
			waitingJumps.put(loc, ArrayUtils.empty());
		}
		
		waitingJumps.get(loc).add(node);
	}
	
	private void resolveWaitingJumps(Loc loc) {
		Node jumpTo = locationMap.get(loc);
		
		if(jumpTo == null) throw new InternalCompilerError("Resolving jump nodes but found null for location");
		
		if(waitingJumps.containsKey(loc)) {
			waitingJumps.get(loc).forEach(n -> graph.addEdge(n, jumpTo));
			waitingJumps.remove(loc);
		}
		
		if(waitingLabels.containsKey(loc)) {
			waitingLabels.get(loc).forEach(l -> {
				locationMap.put(l, jumpTo);
				resolveWaitingJumps(l);
			});
			waitingLabels.remove(loc);
		}
	}

	private void handleLabel(LabelAssem assem) {
		lastLabels.add(assem.getName());
		prevWasLabel = true;
	}
	
	private boolean isJump(Assem assem) {
		return assem instanceof JumpAssem;
	}

	private boolean isLabel(Assem assem) {
		return assem instanceof LabelAssem;
	}

	public static class AssemData<T> {
		private Assem assem;
		private T flowData;
		
		public AssemData(Assem assem, T flowData) {
			this.assem = assem;
			this.flowData = flowData;
		}
		
		public Assem getAssem() {
			return assem;
		}
		
		public T getFlowData() {
			return flowData;
		}
		
		public void setFlowData(T flowData) {
			this.flowData = flowData;
		}
		
		@Override
		public String toString() {
			return assem.toString();
		}
	}
}
