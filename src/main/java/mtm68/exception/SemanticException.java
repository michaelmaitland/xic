package mtm68.exception;

import mtm68.ast.nodes.Node;

public class SemanticException extends Exception {

	private static final long serialVersionUID = -5534642248092480570L;
	private Node errorNode;

	public SemanticException(Node node, String msg) {
		super(msg);
		this.errorNode = node;
	}

	public Node getErrorNode() {
		return errorNode;
	}
}
