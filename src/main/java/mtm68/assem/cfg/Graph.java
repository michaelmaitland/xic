package mtm68.assem.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mtm68.util.ArrayUtils;

public class Graph<T> {
	
	private int currNodeId;
	private List<Node> nodes;
	private Map<Node, T> dataMap;
	
	public Graph() {
		currNodeId = 0;

		nodes = ArrayUtils.empty();
		dataMap = new HashMap<>();
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public Node createNode(T data, String prettyPrint) {
		Node node = new Node(this, currNodeId++, prettyPrint);
		nodes.add(node);
		dataMap.put(node, data);
		return node;
	}
	
	public T getDataForNode(Node n) {
		return dataMap.get(n);
	}
	
	public void addEdge(Node from, Node to) {
		from.addSucc(to);
		to.addPred(to);
	}

	public void removeEdge(Node from, Node to) {
		from.removeSucc(to);
		to.addSucc(to);
	}
	
	public void show(Writer writer, String name) throws IOException {
		writer.append("digraph " + name + " {\n");
		
		for(Node curr : nodes) {
			String currStr = quote(curr.prettyPrint); 
			for(Node succ : curr.succ()) {
				writer.append('\t');
				writer.append(currStr);
				writer.append(" -> ");
				writer.append(quote(succ.prettyPrint));
				writer.append('\n');
			}
		}
		
		writer.append("}");
		writer.flush();
	}
	
	private String quote(String str) {
		return "\"" + str + "\"";
	}
	
	public static class Edge {
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
	}
	
	public static class Node {
		private Graph graph;
		private int nodeId;
		private String prettyPrint;

		private Set<Node> succ;
		private Set<Node> pred;
		
		public Node(Graph graph, int nodeId, String prettyPrint) {
			this.graph = graph;
			this.nodeId = nodeId;
			this.prettyPrint = prettyPrint;
			
			succ = new HashSet<>();
			pred = new HashSet<>();
		}
		
		public Set<Node> succ() {
			return succ;
		}
		
		public Set<Node> pred() {
			return pred;
		}
		
		// TODO: Memoize
		public Set<Node> adj() {
			return Stream.concat(succ.stream(), pred.stream())
					.collect(Collectors.toSet());
		}
		
		public void addSucc(Node succ) {
			this.succ.add(succ);
		}

		public void removeSucc(Node succ) {
			this.succ.remove(succ);
		}

		public void addPred(Node pred) {
			this.pred.add(pred);
		}

		public void removePred(Node pred) {
			this.pred.remove(pred);
		}
		
		public int inDegree() {
			return pred.size();
		}

		public int outDegree() {
			return succ.size();
		}
		
		public boolean goesTo(Node n) {
			return succ.contains(n);
		}
		
		public boolean comesFrom(Node n) {
			return pred.contains(n);
		}
		
		public boolean adjacentTo(Node n) {
			return goesTo(n) || comesFrom(n);
		}
		
		
		@Override
		public String toString() {
			return prettyPrint;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + nodeId;
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
			if (nodeId != other.nodeId)
				return false;
			return true;
		}
	}
}
