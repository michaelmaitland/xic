package mtm68.assem.cfg;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import mtm68.assem.Assem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.cfg.Graph.Edge;
import mtm68.assem.cfg.Graph.Node;
import mtm68.assem.cfg.Liveness.LiveData;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class RegisterAllocation {
	
	private Map<String, RealReg> colors;
	
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
	private Map<Node, String> colorMap;
	
	public RegisterAllocation(Set<RealReg> colors) {
		this.colors = colors.stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));
	}
	
	public void doRegisterAllocation(List<Assem> assems) {
		init();
		build(assems);
		makeWorklists();
		
		while(!(simplifyWorklist.isEmpty() && spillWorklist.isEmpty())) {

			if(!simplifyWorklist.isEmpty()) simplify();
			else if(!spillWorklist.isEmpty()) selectSpill();
			
		}
		
		assignColors();
		if(!spilledNodes.isEmpty()) {
			List<Assem> newAssems = rewriteProgram();
			doRegisterAllocation(newAssems);
		}
	}
	
	public Map<Node, String> getColorMap() {
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

		k = colors.size();
	}
	
	private void build(List<Assem> assems) {
		Liveness liveness = new Liveness();
		liveness.performLiveVariableAnalysis(assems);
		
		liveGraph = liveness.getLiveGraph();
		interferenceGraph = liveness.getInterferenceGraph();
		
		try {
			Writer writer = new PrintWriter(System.out);
			liveness.showLiveGraph(writer);
			System.out.println();
			liveness.showInterferenceGraph(writer);
			System.out.println();
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
				if(coloredNodes.contains(adj)) {
					okColors.remove(colorMap.get(adj));
				}
			}
			
			if(okColors.isEmpty()) {
				spilledNodes.add(node);
			} else {
				coloredNodes.add(node);
				String color = okColors.iterator().next();
				colorMap.put(node, color);
			}
		}
	}
	
	private List<Assem> rewriteProgram() {
		return ArrayUtils.empty();
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
		return colors.containsKey(regId); 
	}
	
	private int degree(Node n) {
		return degreeMap.get(n);
	}

}
