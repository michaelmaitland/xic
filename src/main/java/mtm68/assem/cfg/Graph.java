package mtm68.assem.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class Graph<T> {

	private int currNodeId;
	private List<Node> nodes;
	private Map<Node, T> dataMap;
	private Map<T, Node> invDataMap;

	public Graph() {
		currNodeId = 0;

		nodes = ArrayUtils.empty();
		dataMap = new HashMap<>();
		invDataMap = new HashMap<>();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public Node createNode(T data) {
		Node node = new Node(this, currNodeId++, data.toString());
		nodes.add(node);
		dataMap.put(node, data);
		invDataMap.put(data, node);
		return node;
	}

	public T getDataForNode(Node n) {
		return dataMap.get(n);
	}

	public Node getNodeForData(T data) {
		return invDataMap.get(data);
	}

	public boolean nodeExists(T data) {
		return invDataMap.containsKey(data);
	}

	public void addEdge(Node from, Node to) {
		from.addSucc(to);
		to.addPred(from);
	}

	public void removeEdge(Node from, Node to) {
		from.removeSucc(to);
		to.removePred(from);
	}

	public void show(Writer writer, String name, boolean directed, Function<T, String> getNodeRep) throws IOException {
		writer.append(directed ? "digraph" : "graph");
		writer.append(' ');
		writer.append(name + " {\n");

		Set<Edge> seenEdges = SetUtils.empty();

		for (Node curr : nodes) {
			String currStr = quote(getNodeRep.apply(dataMap.get(curr)));
			for (Node succ : curr.succ()) {
				if (!directed) {
					Edge forward = new Edge(curr, succ);
					Edge backward = new Edge(succ, curr);

					if (seenEdges.contains(forward) || seenEdges.contains(backward))
						continue;

					seenEdges.add(forward);
					seenEdges.add(backward);
				}
				writer.append('\t');
				writer.append(currStr);
				writer.append(directed ? " -> " : " -- ");
				writer.append(quote(getNodeRep.apply(dataMap.get(succ))));
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
			return Stream.concat(succ.stream(), pred.stream()).collect(Collectors.toSet());
		}

		public int degree() {
			return adj().size();
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
