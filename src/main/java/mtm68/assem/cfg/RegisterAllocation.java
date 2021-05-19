package mtm68.assem.cfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import mtm68.assem.Assem;
import mtm68.assem.CompUnitAssem;
import mtm68.assem.FuncDefnAssem;
import mtm68.assem.MoveAssem;
import mtm68.assem.ReplaceableReg;
import mtm68.assem.SeqAssem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.cfg.Graph.Edge;
import mtm68.assem.cfg.Graph.Node;
import mtm68.assem.cfg.Liveness.LiveData;
import mtm68.assem.operand.FreshRegGenerator;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;
import mtm68.util.SetUtils;

public class RegisterAllocation {
	
	private Map<String, RealReg> colors;
	private Map<String, FunctionSpillData> funcData;
	private Map<String, String> tempToFuncMap;
	
	private int k;
	private Graph<String> interferenceGraph;
	private Graph<AssemData<LiveData>> liveGraph;
	private List<Node> simplifyWorklist;
	private List<Node> spillWorklist;
	private Stack<Node> selectStack;
	private Map<Node, Integer> degreeMap;
	
	private Set<Edge> adjSet;
	private Map<Node, List<Node>> adjList;
	private Set<Node> spilledNodes;
	private Set<Node> coloredNodes;
	private Map<String, String> colorMap;
	
	public RegisterAllocation(Set<RealReg> colors) {
		this.colors = colors.stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));
	}
	
	public CompUnitAssem doRegisterAllocation(CompUnitAssem program) {
		tempToFuncMap = new HashMap<>();
		funcData = new HashMap<>();
		
		List<FuncDefnAssem> result = ArrayUtils.empty();
		for(FuncDefnAssem func : program.getFunctions()) {
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
			
//			assems.addAll(funcAssems);
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
		
		while(!(simplifyWorklist.isEmpty() && spillWorklist.isEmpty())) {

			if(!simplifyWorklist.isEmpty()) simplify();
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
		simplifyWorklist = new LinkedList<>();
		spillWorklist = new LinkedList<>();
		selectStack = new Stack<>();
		degreeMap = new HashMap<>();
		
		adjSet = new HashSet<>();
		adjList = new HashMap<>();
		spilledNodes = new HashSet<>();
		coloredNodes = new HashSet<>();
		colorMap = new HashMap<>();
		
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
		
		try {
//			Writer writer = new PrintWriter(System.out);
//			liveness.showLiveGraph(writer);
//			System.out.println();
//			liveness.showInterferenceGraph(writer);
//			System.out.println();
		} catch(Exception e) {
		}

		for(Node node : interferenceGraph.getNodes()) {
			degreeMap.put(node, 0);
			adjList.put(node, ArrayUtils.empty());
		}
		
		for(Node node : liveGraph.getNodes()) {
			AssemData<LiveData> data = liveGraph.getDataForNode(node);
			Set<String> live = data.getFlowData().getLiveOut();

			Set<String> defined = regsToStrs(data.getAssem().def());
			
			for(String d : defined) {
				for(String l : live) {
					addEdge(d, l);
				}
			}
		}
	}
	
	private void selectSpill() {
		Node spill = spillWorklist.remove(0);
		simplifyWorklist.add(spill);
	}

	private void assignColors() {
		while(!selectStack.empty()) {
			Node node = selectStack.pop();
			Set<String> okColors = SetUtils.copy(colors.keySet()); 
			
			for(Node adj : adjList.get(node)) {
				if(coloredNodes.contains(adj) || precolored(adj)) {
					String color = interferenceGraph.getDataForNode(adj);
					okColors.remove(colorMap.get(color));
				}
			}
			
			if(okColors.isEmpty()) {
				spilledNodes.add(node);
			} else {
				coloredNodes.add(node);
				String color = okColors.iterator().next();
				String temp = interferenceGraph.getDataForNode(node);
				
				// Record used callee saved register
				RealReg reg = colors.get(color); 
				if(RealReg.isCalleeSaved(reg)) {
					funcData.get(tempToFuncMap.get(temp)).addCalleeSaved(reg);
				}
				
				colorMap.put(temp, color);
			}
		}
	}
	
	private List<Assem> rewriteProgram(List<Assem> assems) {
		System.out.println("Nodes spilled, rewriting.");
		System.out.println();

		System.out.println("Spilled Nodes\n=========");
		spilledNodes.forEach(System.out::println);
		System.out.println();

		Map<String, Mem> memLocs = new HashMap<>();
		for(Node spilled : spilledNodes) {
			String temp = interferenceGraph.getDataForNode(spilled);
			String func = tempToFuncMap.get(temp);

			FunctionSpillData spillData = funcData.get(func);
			spillData.addSpill(temp);
			
			memLocs.put(temp, spillData.getMemLocFor(temp));
		}
		
		List<Assem> result = ArrayUtils.empty();
		for(Assem assem : assems) {
			Assem newAssem = assem.copy();

			Map<String, ReplaceableReg> uses = newAssem.useReplaceable().stream()
					.filter(ReplaceableReg::isAbstract)
					.collect(Collectors.toMap(ReplaceableReg::getName, t -> t));

			Map<String, ReplaceableReg> defs = newAssem.defReplaceable().stream()
					.filter(ReplaceableReg::isAbstract)
					.collect(Collectors.toMap(ReplaceableReg::getName, t -> t));
			
			for(String use : uses.keySet()) {
				if(!memLocs.containsKey(use)) continue;

				Reg newTemp = FreshRegGenerator.getFreshAbstractReg();
				tempToFuncMap.put(newTemp.getId(), tempToFuncMap.get(use));
				Mem stackLoc = memLocs.get(use);
				
				result.add(new MoveAssem(newTemp, stackLoc));
				
				uses.get(use).replace(newTemp);
			}
			
			result.add(newAssem);


			for(String def : defs.keySet()) {
				if(!memLocs.containsKey(def)) continue;

				Reg newTemp = FreshRegGenerator.getFreshAbstractReg();
				tempToFuncMap.put(newTemp.getId(), tempToFuncMap.get(def));
				Mem stackLoc = memLocs.get(def);
				
				result.add(new MoveAssem(stackLoc, newTemp));
				
				defs.get(def).replace(newTemp);
			}
		}
		
		System.out.println("Original\n========");
		assems.forEach(System.out::println);
		System.out.println();
		
		System.out.println("New program\n=======");
		result.forEach(System.out::println);
		System.out.println();

		return result;
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
			
			result.add(newAssem);
		}

		return result;
	}
	
	private void addEdge(String t1, String t2) {
		Node u = interferenceGraph.getNodeForData(t1); 
		Node v = interferenceGraph.getNodeForData(t2); 
		
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
				.filter(n -> !selectStack.contains(n))
				.collect(Collectors.toSet());
	}
	
	private Set<String> regsToStrs(Set<Reg> regs) {
		return regs.stream()
			.map(Reg::getId)
			.collect(Collectors.toSet());
	}
	
	private void makeWorklists() {
		for(Node node : interferenceGraph.getNodes()) {
			if(precolored(node)) continue;
			
			if(degree(node) < k) {
				simplifyWorklist.add(node);
			} else {
				spillWorklist.add(node);
			}
		}
	}
	
	private void simplify() {
		Node node = simplifyWorklist.remove(0);
		selectStack.push(node);
		
		for(Node adj : adjacent(node)) {
			decrementDegree(adj);
		}
	}
	
	private void decrementDegree(Node node) {
		int d = degree(node);
		degreeMap.put(node, degreeMap.get(node) - 1);
		
		if(d == k) {
			spillWorklist.remove(node);
			simplifyWorklist.add(node);
		}
		
	}
	
	private boolean precolored(Node node) {
		String regId = interferenceGraph.getDataForNode(node);
		return RealReg.isRealReg(regId);
	}
	
	private int degree(Node n) {
		return degreeMap.get(n);
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
