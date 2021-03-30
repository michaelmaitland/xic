package mtm68.visit;

import mtm68.ast.nodes.Node;
import mtm68.exception.FatalTypeException;

public class NodeToIRNodeConverter extends Visitor {

	public <R extends IRNode, N extends Node> R performConvertToIR(N root) {
		try {
			return root.accept(this);
		} catch(FatalTypeException e) {
		}
		return root;
	}

	@Override
	public IRNode leave(Node n, Node old) {
		return n.convertToIR(this);
	}
}
