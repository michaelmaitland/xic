package mtm68.visit;

import mtm68.ast.nodes.Node;
import mtm68.exception.FatalTypeException;

public class NodeToIRNodeConverter extends Visitor {

	int labelCounter;
	
	public NodeToIRNodeConverter() {
		this.labelCounter = 0;
	}

	public String getFreshLabel() {
		labelCounter++;
		return labelCounter + "";
	}
	
	public <N extends Node> N performConvertToIR(N root) {
		try {
			return root.accept(this);
		} catch(FatalTypeException e) {
		}
		return root;
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.convertToIR(this);
	}
}
