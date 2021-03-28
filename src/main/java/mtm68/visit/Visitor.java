package mtm68.visit;

import java.util.ArrayList;
import java.util.List;

import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Node;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;

public abstract class Visitor {

	private List<BaseError> typeErrors;

	public Visitor() {
		typeErrors = new ArrayList<>();
	}

	public Visitor enter(Node parent, Node n) {
		return this;
	}
	
	public abstract Node leave(Node n, Node old);

	public List<BaseError> getTypeErrors() {
		return typeErrors;
	}

	public BaseError getFirstError() {
		typeErrors.sort(BaseError.getComparator());
		return typeErrors.get(0);
	}
	
	public boolean hasError() {
		return typeErrors.size() > 0;
	}
	
	public void reportError(HasLocation location, String description) {
		typeErrors.add(new SemanticError(location, description));
	}
}
