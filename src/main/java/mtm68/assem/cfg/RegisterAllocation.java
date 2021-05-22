package mtm68.assem.cfg;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.assem.Assem;
import mtm68.assem.CompUnitAssem;
import mtm68.assem.FuncDefnAssem;
import mtm68.assem.MoveAssem;
import mtm68.assem.RegisterAllocator;
import mtm68.assem.ReplaceableReg;
import mtm68.assem.SeqAssem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.cfg.Liveness.LiveData;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.FreshRegGenerator;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;
import mtm68.util.SetUtils;

/**
 * Performs optimal register allocation using Chaitin's algorithm.
 * @author Scott
 */
public class RegisterAllocation implements RegisterAllocator{
	
	private static final boolean SHOW_GRAPHS = false;
	private static final boolean CHECK_INVARIANTS = false;
	private static final boolean PRINT_SPILLS = true;
	private static final boolean PRINT_PROGRAM_REWRITE = false;
	
	private Map<String, RealReg> colors;
	private Map<String, FunctionSpillData> funcData;
	private Map<String, String> tempToFuncMap;
	
	private int k;
	private Graph<String> interferenceGraph;
	private Graph<AssemData<LiveData>> liveGraph;
	private Stack<Node> selectStack;
	private Map<Node, Integer> degreeMap;
	
	// Worklists
	private Set<Node> simplifyWorklist;
	private Set<Node> spillWorklist;
	private Set<Node> freezeWorklist;
	
	// Move sets
	private Set<Move> coalescedMoves;
	private Set<Move> constrainedMoves;
	private Set<Move> frozenMoves;
	private Set<Move> worklistMoves;
	private Set<Move> activeMoves;

	private Map<String, Node> nodeMap;
	private List<Node> initial;
	private Set<Node> precolored;
	private Set<Edge> adjSet;
	private Map<Node, List<Node>> adjList;
	private Map<Node, Set<Move>> moveList;
	private Map<Node, Node> alias;
	private Set<Node> spilledNodes;
	private Set<Node> coloredNodes;
	private Set<Node> coalescedNodes;
	private Map<String, String> colorMap;
	private Set<String> spilledTemps;
	
	public RegisterAllocation(Set<RealReg> colors) {
		this.colors = colors.stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));
	}
	
	@Override
	public CompUnitAssem allocateRegisters(CompUnitAssem program) {
		tempToFuncMap = new HashMap<>();
		funcData = new HashMap<>();
		
		List<FuncDefnAssem> result = ArrayUtils.empty();
		for(FuncDefnAssem func : program.getFunctions()) {
			spilledTemps = new HashSet<>();

			String funcName = func.getName();
			funcData.put(funcName, new FunctionSpillData());
			
			List<Assem> funcAssems = func.getBodyAssem().getAssems();

			funcAssems.stream()
				.map(Assem::getReplaceableRegs)
				.flatMap(List::stream)
				.filter(ReplaceableReg::isAbstract)
				.map(ReplaceableReg::getName)
				.forEach(t -> tempToFuncMap.put(t, funcName));
			
			SeqAssem newFuncBody = new SeqAssem(doRegisterAllocation(funcAssems));
			FuncDefnAssem newFuncDefn = new FuncDefnAssem(funcName, func.getNumArgs(), newFuncBody);  

			FunctionSpillData spillData = funcData.get(funcName);
			newFuncDefn.setNumSpilledTemps(spillData.numSpilledTemps());
			newFuncDefn.setCalleeRegs(spillData.getCalleeSavedAsList());
			
			result.add(newFuncDefn);
		}
		
		CompUnitAssem newProg = program.copy();
		newProg.setFunctions(result);
		return newProg;
	}
	
	private List<Assem> doRegisterAllocation(List<Assem> assems) {
		init();
		build(assems);
		makeWorklists();

		if(CHECK_INVARIANTS) checkInvariants();
		
		while(!(simplifyWorklist.isEmpty() && worklistMoves.isEmpty()
				&& freezeWorklist.isEmpty() && spillWorklist.isEmpty())) {
			
			if(!simplifyWorklist.isEmpty()) simplify();
			else if(!worklistMoves.isEmpty()) coalesce();
			else if(!freezeWorklist.isEmpty()) freeze();
			else if(!spillWorklist.isEmpty()) selectSpill();
		}
		
		assignColors();
		if(!spilledNodes.isEmpty()) {
			List<Assem> newAssems = rewriteProgram(assems);
			return doRegisterAllocation(newAssems);
		}
		
		return substitution(assems);
	}
	
	public Map<String, String> getColorMap() {
		return colorMap;
	}
	
	private void init() {
		simplifyWorklist = new HashSet<>();
		spillWorklist = new HashSet<>();
		freezeWorklist = new HashSet<>();
		selectStack = new Stack<>();

		coalescedMoves = new HashSet<>();
		constrainedMoves = new HashSet<>();
		frozenMoves = new HashSet<>();
		worklistMoves = new HashSet<>();
		activeMoves = new HashSet<>();

		nodeMap = new HashMap<>();
		degreeMap = new HashMap<>();
		adjSet = new HashSet<>();
		adjList = new HashMap<>();
		moveList = new HashMap<>();
		spilledNodes = new HashSet<>();
		coloredNodes = new HashSet<>();
		coalescedNodes = new HashSet<>();
		colorMap = new HashMap<>();
		alias = new HashMap<>();

		// For precolored nodes
		for(String color : colors.keySet()) {
			colorMap.put(color, color);
		}

		k = colors.size();
	}
	
	private void build(List<Assem> assems) {
		Liveness liveness = new Liveness();
		liveness.performLiveVariableAnalysis(assems);
		
		liveGraph = liveness.getLiveGraph();
		interferenceGraph = liveness.getInterferenceGraph();
		
		if(SHOW_GRAPHS) showGraphs(liveness);

		initial = ArrayUtils.elems(interferenceGraph.getNodes()).stream()
				.map(interferenceGraph::getDataForNode)
				.filter(r -> !RealReg.isRealReg(r))
				.map(t -> new Node(t, NodeWorklist.INITIAL))
				.collect(Collectors.toList());
		
		precolored = colors.keySet().stream()
				.map(t -> new Node(t, NodeWorklist.PRECOLORED))
				.collect(Collectors.toSet());

		// The stack point and base pointer can't be used as colors
		// but they can interfere with other nodes and are considered
		// precolored.
		precolored.add(new Node("rbp", NodeWorklist.PRECOLORED));
		precolored.add(new Node("rsp", NodeWorklist.PRECOLORED));

		Stream.concat(initial.stream(), precolored.stream()).forEach(node -> {
			degreeMap.put(node, 0);
			adjList.put(node, ArrayUtils.empty());
			nodeMap.put(node.getId(), node);
		});
		
		List<AssemData<LiveData>> nodeData = liveGraph.getNodes().stream()
				.map(liveGraph::getDataForNode)
				.collect(Collectors.toList());

		for(AssemData<LiveData> data : nodeData) {
			Assem assem = data.getAssem();

			Set<String> live = data.getFlowData().getLiveOut();
			Set<String> defined = regsToStrs(assem.def());
			
			if(assem instanceof MoveAssem) {
				MoveAssem moveAssem = (MoveAssem) assem;

				Dest dest = moveAssem.getDest();
				Src src = moveAssem.getSrc();
				
				if(dest instanceof Reg && src instanceof Reg) {
					Reg destReg = (Reg) dest;
					Reg srcReg = (Reg) src;
					
					Node destNode = nodeMap.get(destReg.getId());
					Node srcNode = nodeMap.get(srcReg.getId());
					
					Move move = new Move(destNode, srcNode, MoveSet.WORKLIST);

					addToMoveList(destNode, move);
					addToMoveList(srcNode, move);
					addToWorklistMoves(move);
					
					live.remove(srcNode.getId());
				}
			}
			
			for(String d : defined) {
				for(String l : live) {
					addEdge(d, l);
				}
			}
		}
	}

	private void makeWorklists() {
		Iterator<Node> iterator = initial.iterator();
		while(iterator.hasNext()) {
			Node node = iterator.next();

			if(degree(node) >= k) {
				addToSpillWorklist(node);
			} 
			else if(isMoveRelated(node)) {
				addToFreezeWorklist(node);
			}
			else {
				addToSimplifyWorklist(node);
			}

			iterator.remove();
		}
	}

	private void simplify() {
		Node node = SetUtils.poll(simplifyWorklist);
		addToSelectStack(node);
		
		Set<Node> adjacent = adjacent(node);
		for(Node adj : adjacent) {
			decrementDegree(adj);
		}
	}


	private void coalesce() {
		Move move = SetUtils.poll(worklistMoves);
		Node x = getAlias(move.getDest());
		Node y = getAlias(move.getSrc());
		
		Node u, v;
		if(precolored(y)) {
			u = y;
			v = x;
		} else {
			u = x;
			v = y;
		}
		
		Edge uv = new Edge(u, v);
		
		if(u.equals(v)) {
			addToCoalescedMoves(move);
			addWorkList(u);
		} else if (precolored(v) || adjSet.contains(uv)) {
			addToConstrainedMoves(move);
			addWorkList(u);
			addWorkList(v);
		} else {
			Set<Node> uAdj = adjacent(u);
			Set<Node> vAdj = adjacent(v);

			boolean allAdjOk = vAdj.stream()
					.allMatch(t -> ok(t, u));
			
			if((precolored(u) && allAdjOk) ||
					!precolored(u) && conservative(SetUtils.union(uAdj, vAdj))) {
				addToCoalescedMoves(move);
				combine(u, v);
				addWorkList(u);
			} else {
				addToActiveMoves(move);
			}
		}
	}

	private void freeze() {
		Node node = SetUtils.poll(freezeWorklist);
		addToSimplifyWorklist(node);
		freezeMoves(node);
	}

	private void selectSpill() {
		Node bestSpill = null;
		int bestDegree = 0;
		
		for(Node spill : spillWorklist) {
			// Don't spill a temp that's already been spilled
			if(spilledTemps.contains(spill.getId())) {
				continue;
			}

			int deg = degree(spill);
			if(deg > bestDegree) {
				bestDegree = deg;
				bestSpill = spill;
			}
		}
		
		// If we can't find a temp that hasn't been spilled before,
		// then just spill it again (although this should never get hit).
		if(bestSpill == null) {
			bestSpill = SetUtils.poll(spillWorklist);
		} else {
			spillWorklist.remove(bestSpill);
		}
		
		addToSimplifyWorklist(bestSpill);
		freezeMoves(bestSpill);
	}

	private void assignColors() {
		while(!selectStack.empty()) {
			Node node = selectStack.pop();
			Set<String> okColors = SetUtils.copy(colors.keySet()); 
			
			for(Node adj : adjList.get(node)) {
				Node alias = getAlias(adj);
				if(coloredNodes.contains(alias) || precolored(alias)) {
					String temp = alias.getId(); 
					okColors.remove(colorMap.get(temp));
				}
			}
			
			if(okColors.isEmpty()) {
				addToSpilledNodes(node);
			} else {
				addToColoredNodes(node);

				String color = okColors.iterator().next();
				String temp = node.getId(); 
				
				// Record used callee saved register
				RealReg reg = colors.get(color); 
				if(RealReg.isCalleeSaved(reg)) {
					funcData.get(tempToFuncMap.get(temp)).addCalleeSaved(reg);
				}
				
				colorMap.put(temp, color);
			}
		}
		
		for(Node node : coalescedNodes) {
			String aliasTemp = getAlias(node).getId();
			String temp = node.getId();
			colorMap.put(temp, colorMap.get(aliasTemp));
		}
	}
	

	private List<Assem> rewriteProgram(List<Assem> assems) {
		if(PRINT_SPILLS) {
			System.out.println("Nodes spilled, rewriting.");
			System.out.println();

			System.out.println("Previously spilled temps: " + spilledTemps);
			System.out.println("Spilled Nodes (" + spilledNodes.size() + ")\n=========");
			spilledNodes.forEach(System.out::println);
			System.out.println();
		}

		Map<String, Mem> memLocs = new HashMap<>();
		for(Node spilled : spilledNodes) {
			String temp = spilled.getId(); 
			String func = tempToFuncMap.get(temp);

			FunctionSpillData spillData = funcData.get(func);
			spillData.addSpill(temp);
			
			memLocs.put(temp, spillData.getMemLocFor(temp));
		}
		
		List<Assem> result = ArrayUtils.empty();
		for(Assem assem : assems) {
			Assem newAssem = assem.copy();

			Set<ReplaceableReg> uses = newAssem.useReplaceable().stream()
					.filter(ReplaceableReg::isAbstract)
					.collect(Collectors.toSet());

			Set<ReplaceableReg> defs = newAssem.defReplaceable().stream()
					.filter(ReplaceableReg::isAbstract)
					.collect(Collectors.toSet());
			
			replaceRegs(result, uses, memLocs, true);
			result.add(newAssem);
			replaceRegs(result, defs, memLocs, false);
		}
			
		
		if(PRINT_PROGRAM_REWRITE) {
			System.out.println("Original\n========");
			assems.forEach(System.out::println);
			System.out.println();
			
			System.out.println("New program\n=======");
			result.forEach(System.out::println);
			System.out.println();
		}

		return result;
	}

	private void replaceRegs(List<Assem> result, Set<ReplaceableReg> regs, Map<String, Mem> memLocs, boolean readFromStack) {
			for(ReplaceableReg reg : regs) {
				String regName = reg.getName();
				if(!memLocs.containsKey(regName)) continue;

				Reg newTemp = FreshRegGenerator.getFreshAbstractReg();
				spilledTemps.add(newTemp.getId());

				tempToFuncMap.put(newTemp.getId(), tempToFuncMap.get(regName));
				Mem stackLoc = memLocs.get(regName);
				
				if(readFromStack) {
					result.add(new MoveAssem(newTemp, stackLoc));
				} else {
					result.add(new MoveAssem(stackLoc, newTemp));
				}
				
				reg.replace(newTemp);
			}
		
	}
	
	private List<Assem> substitution(List<Assem> assems) {
		List<Assem> result = ArrayUtils.empty();
		
		for(Assem assem : assems) {
			Assem newAssem = assem.copy();
			
			List<ReplaceableReg> regs = newAssem.getReplaceableRegs();
			
			for(ReplaceableReg reg : regs) { 
				if(!reg.isAbstract()) continue;

				String colorName = colorMap.get(reg.getName());
				RealReg color = colors.get(colorName);

				reg.replace(color);
			}
			
			if(unnecessaryMove(newAssem)) continue;

			result.add(newAssem);
		}
		
		return result;
	}
	
	
	//-------------------------------------------------------------------------------- 
	// Invariants
	//-------------------------------------------------------------------------------- 
	
	private void checkInvariants() {
		checkDegreeInvariant();
		checkSimplifyInvariant();
		checkFreezeInvariant();
		checkSpillInvariant();
	}
	
	private void checkDegreeInvariant() {
		Set<Node> degreeNodes = new HashSet<>(simplifyWorklist);
		degreeNodes.addAll(freezeWorklist);
		degreeNodes.addAll(spillWorklist);
		
		for(Node u : degreeNodes) {
			Set<Node> union = new HashSet<>(precolored);
			union.addAll(degreeNodes);

			int size = SetUtils.intersect(new HashSet<>(adjList.get(u)), union).size();
			
			if(degree(u) != size) {
				throw new InternalCompilerError("Degree invariant violated for " + u + ". " + degree(u) + " != " + size);
			}
		}
	}
	
	private void checkSimplifyInvariant() {
		for(Node node : simplifyWorklist) {
			Set<Move> intersect = SetUtils.intersect(moveList.getOrDefault(node, SetUtils.empty()),
					SetUtils.union(activeMoves, new HashSet<>(worklistMoves)));

			if(degree(node) < k && intersect.isEmpty()) continue;
			throw new InternalCompilerError("Simplify invariant violated for " + node);
		}
	}

	private void checkFreezeInvariant() {
		for(Node node : freezeWorklist) {
			Set<Move> intersect = SetUtils.intersect(moveList.getOrDefault(node, SetUtils.empty()),
					SetUtils.union(activeMoves, new HashSet<>(worklistMoves)));

			if(degree(node) < k && !intersect.isEmpty()) continue;
			throw new InternalCompilerError("Freeze invariant violated for " + node);
		}
	}

	private void checkSpillInvariant() {
		for(Node node : spillWorklist) {
			if(degree(node) >= k) continue;
			throw new InternalCompilerError("Spill invariant violated for " + node);
		}
	}

	//-------------------------------------------------------------------------------- 

	private void freezeMoves(Node node) {
		for(Move move : nodeMoves(node)) {
			Node x = move.getDest();
			Node y = move.getSrc();

			Node v = null;

			if(getAlias(y).equals(getAlias(node))) {
				v = getAlias(x);
			} else {
				v = getAlias(y);
			}
			
			activeMoves.remove(move);
			addToFrozenMoves(move);
			
			if(v.getWorklist() == NodeWorklist.FREEZE && nodeMoves(v).isEmpty()) {
				freezeWorklist.remove(v);
				addToSimplifyWorklist(v);
			}
		}
	}

	private boolean unnecessaryMove(Assem newAssem) {
		if(newAssem instanceof MoveAssem) {
			MoveAssem move = (MoveAssem) newAssem;
			return move.getDest().equals(move.getSrc());
		}
		return false;
	}
	
	private void addEdge(String t1, String t2) {
		Node u = nodeMap.get(t1); 
		Node v = nodeMap.get(t2); 
		
		addEdge(u, v);
	}

	private void addEdge(Node u, Node v) {
		Edge uv = new Edge(u, v);
		
		if(!adjSet.contains(uv) && !u.equals(v)) {
			Edge vu = new Edge(v, u);
			SetUtils.unionMutable(adjSet, SetUtils.elems(uv, vu));
			
			if(!precolored(u)) {
				adjList.get(u).add(v);
				degreeMap.put(u, degreeMap.get(u) + 1);
			}

			if(!precolored(v)) {
				adjList.get(v).add(u);
				degreeMap.put(v, degreeMap.get(v) + 1);
			}
		}
	}
	
	private Set<Node> adjacent(Node node) {
		return adjList.get(node).stream()
				.filter(n -> n.getWorklist() != NodeWorklist.SELECT_STACK &&
								n.getWorklist() != NodeWorklist.COALESCED)
				.collect(Collectors.toSet());
	}
	
	private Set<String> regsToStrs(Set<Reg> regs) {
		return regs.stream()
			.map(Reg::getId)
			.collect(Collectors.toSet());
	}
	
	
	private Set<Move> nodeMoves(Node node) {
		if(!moveList.containsKey(node)) return SetUtils.empty();

		Set<Move> union = Stream.concat(activeMoves.stream(), worklistMoves.stream())
				.collect(Collectors.toSet());

		return SetUtils.intersect(moveList.get(node), union);
	}

	private boolean isMoveRelated(Node node) {
		return !nodeMoves(node).isEmpty();
	}

	private void decrementDegree(Node node) {
		// Do we want this here?
		if(precolored(node)) return;

		int d = degree(node);
		degreeMap.put(node, d - 1);
		
		if(d == k) {
			Set<Node> movesToEnable = adjacent(node);
			movesToEnable.add(node);

			enableMoves(movesToEnable);
			spillWorklist.remove(node);
			
			if(isMoveRelated(node)) {
				addToFreezeWorklist(node);
			} else {
				addToSimplifyWorklist(node);
			}
		}
		
	}
	
	private void enableMoves(Set<Node> nodes) {
		for(Node node : nodes) {
			for(Move move : nodeMoves(node)) {
				if(move.getMoveSet() == MoveSet.ACTIVE) {
					activeMoves.remove(move);
					addToWorklistMoves(move);
				}
			}
		}
	}
	
	private void combine(Node u, Node v) {
		if(v.getWorklist() == NodeWorklist.FREEZE) {
			freezeWorklist.remove(v);
		} else {
			spillWorklist.remove(v);
		}
		
		addToCoalescedNodes(v);
		alias.put(v, u);
		SetUtils.unionMutable(moveList.get(u), moveList.get(v));
		
		enableMoves(SetUtils.elems(v));
		
		for(Node t : adjacent(v)) {
			addEdge(t, u);
			decrementDegree(t);
		}
		
		if(degree(u) >= k && u.getWorklist() == NodeWorklist.FREEZE) {
			freezeWorklist.remove(u);
			addToSpillWorklist(u);
		}
	}
		
	private Node getAlias(Node node) {
		if(node.getWorklist() == NodeWorklist.COALESCED) {
			return getAlias(alias.get(node));
		}
		
		return node;
	}
	
	private void addWorkList(Node node) {
		if(!precolored(node) && !isMoveRelated(node) && degree(node) < k) {
			freezeWorklist.remove(node);
			addToSimplifyWorklist(node);
		}
	}
	
	private boolean ok(Node t, Node r) {
		return degree(t) < k || precolored(t) || adjSet.contains(new Edge(t, r));
	}
	
	private boolean conservative(Set<Node> nodes) {
		int count = 0;
		
		for(Node node : nodes) {
			if(degree(node) >= k || precolored(node)) count++;
		}
		
		return count < k;
	}
	
	//-------------------------------------------------------------------------------- 
	// Worklist management 
	//-------------------------------------------------------------------------------- 

	private void addToSimplifyWorklist(Node spill) {
		spill.setWorklist(NodeWorklist.SIMPLIFY);
		simplifyWorklist.add(spill);
	}

	private void addToSpillWorklist(Node node) {
		node.setWorklist(NodeWorklist.SPILL);
		spillWorklist.add(node);
	}

	private void addToFreezeWorklist(Node node) {
		node.setWorklist(NodeWorklist.FREEZE);
		freezeWorklist.add(node);
	}

	private void addToSelectStack(Node node) {
		node.setWorklist(NodeWorklist.SELECT_STACK);
		selectStack.push(node);
	}

	private void addToSpilledNodes(Node node) {
		node.setWorklist(NodeWorklist.SPILLED);
		spilledNodes.add(node);
	}
	
	private void addToColoredNodes(Node node) {
		node.setWorklist(NodeWorklist.COLORED);
		coloredNodes.add(node);
	}

	private void addToCoalescedNodes(Node node) {
		node.setWorklist(NodeWorklist.COALESCED);
		coalescedNodes.add(node);
	}

	private void addToWorklistMoves(Move move) {
		move.setMoveSet(MoveSet.WORKLIST);
		worklistMoves.add(move);
	}

	private void addToCoalescedMoves(Move move) {
		move.setMoveSet(MoveSet.COALESCED);
		coalescedMoves.add(move);
	}

	private void addToActiveMoves(Move move) {
		move.setMoveSet(MoveSet.ACTIVE);
		activeMoves.add(move);
	}

	private void addToConstrainedMoves(Move move) {
		move.setMoveSet(MoveSet.CONSTRAINED);
		constrainedMoves.add(move);
	}

	private void addToFrozenMoves(Move move) {
		move.setMoveSet(MoveSet.FROZEN);
		frozenMoves.add(move);
	}

	private void addToMoveList(Node node, Move move) {
		if(!moveList.containsKey(node)) {
			moveList.put(node, SetUtils.empty());
		}
		
		moveList.get(node).add(move);
	}

	//-------------------------------------------------------------------------------- 
	
	private boolean precolored(Node node) {
		return RealReg.isRealReg(node.getId());
	}
	
	private int degree(Node n) {
		return degreeMap.get(n);
	}

	private void showGraphs(Liveness liveness) {
		try {
			Writer writer = new PrintWriter(System.out);
			liveness.showLiveGraph(writer);
			System.out.println();
			liveness.showInterferenceGraph(writer);
			System.out.println();
		} catch(Exception e) {
		}
	}
	
	private static class Node {
		private String id;
		private NodeWorklist worklist;
		
		private Node(String id, NodeWorklist worklist) {
			super();
			this.id = id;
			this.worklist = worklist;
		}

		private Node(String id) {
			this(id, NodeWorklist.INITIAL);
		}

		public String getId() {
			return id;
		}
		
		public void setWorklist(NodeWorklist worklist) {
			this.worklist = worklist;
		}
		
		public NodeWorklist getWorklist() {
			return worklist;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
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
			Node other = (Node) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	private static class Edge {
		private Node from;
		private Node to;

		public Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
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
			Edge other = (Edge) obj;
			if (from == null) {
				if (other.from != null)
					return false;
			} else if (!from.equals(other.from))
				return false;
			if (to == null) {
				if (other.to != null)
					return false;
			} else if (!to.equals(other.to))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return from + " - " + to;
		}
		
	}
	
	private enum NodeWorklist {
		PRECOLORED,
		INITIAL,
		SIMPLIFY,
		FREEZE,
		SPILL,
		SPILLED,
		COALESCED,
		COLORED,
		SELECT_STACK
	}
	
	private static class Move {
		private Node dest;
		private Node src;
		private MoveSet moveSet;

		public Move(Node dest, Node src, MoveSet moveSet) {
			super();
			this.dest = dest;
			this.src = src;
			this.moveSet = moveSet;
		}
		
		public void setMoveSet(MoveSet moveSet) {
			this.moveSet = moveSet;
		}
		
		public Node getDest() {
			return dest;
		}
		
		public Node getSrc() {
			return src;
		}
		
		public MoveSet getMoveSet() {
			return moveSet;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dest == null) ? 0 : dest.hashCode());
			result = prime * result + ((src == null) ? 0 : src.hashCode());
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
			Move other = (Move) obj;
			if (dest == null) {
				if (other.dest != null)
					return false;
			} else if (!dest.equals(other.dest))
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return dest + " <- " + src;
		}
	}
	
	private enum MoveSet {
		COALESCED,
		CONSTRAINED,
		FROZEN,
		WORKLIST,
		ACTIVE
	}
	
	private static class FunctionSpillData {
		private Map<String, Mem> memMap;
		private Set<RealReg> calleeSaved;
		
		public FunctionSpillData() {
			memMap = new HashMap<>();
			calleeSaved = SetUtils.empty();
		}
		
		public void addSpill(String temp) {
			int size = memMap.size();
			Mem mem = new Mem(RealReg.RBP, Constants.WORD_SIZE * -(size + 1));
			memMap.put(temp, mem);
		}
		
		public Mem getMemLocFor(String temp) {
			return memMap.get(temp);
		}
		
		public int numSpilledTemps() {
			return memMap.size();
		}
		
		public void addCalleeSaved(RealReg reg) {
			calleeSaved.add(reg);
		}

		public List<RealReg> getCalleeSavedAsList(){
			return calleeSaved.stream().collect(Collectors.toList());
		}
	}
}
