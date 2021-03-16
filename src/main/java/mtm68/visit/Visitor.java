package mtm68.visit;

import mtm68.ast.nodes.Node;

public abstract class Visitor {

	@SuppressWarnings("unchecked")
	public <N extends Node> N visit(N n) {
		Visitor v2 = this.enter(n);
		Node n2 = n.visitChildren(v2);
		return (N) leave(n2, n);
	}

	public Visitor enter(Node n) {
		return this;
	}
	
	public abstract Node leave(Node parent, Node old);
}
