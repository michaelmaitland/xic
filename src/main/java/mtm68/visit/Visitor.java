package mtm68.visit;

import mtm68.ast.nodes.Node;

public abstract class Visitor {

	public Visitor enter(Node n) {
		return this;
	}
	
	public abstract Node leave(Node n, Node old);
}
