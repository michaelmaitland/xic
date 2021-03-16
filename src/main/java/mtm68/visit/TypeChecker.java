package mtm68.visit;

import mtm68.ast.nodes.Node;

public class TypeChecker extends Visitor {

	// State (ie symbold table)

	@Override
	public Visitor enter(Node n) {
		return this;
	}

	@Override
	public Node leave(Node n, Node old) {
		// TODO: if n == old, we need to make a copy of n before modifying it. It should then return the modified copy
		return n.typeCheck(this);
	}
}
