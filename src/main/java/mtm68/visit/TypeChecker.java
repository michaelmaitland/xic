package mtm68.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.types.ContextType;
import mtm68.ast.types.HasResult;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.TypingContext;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;

public class TypeChecker extends Visitor {
	TypingContext context;
	
	private List<SemanticError> typeErrors;

	public TypeChecker(Map<String, ContextType> initSymTable) {
		this.context = new TypingContext(initSymTable);
		typeErrors = new ArrayList<>();
	}
	
	public TypeChecker() {
		this.context = new TypingContext();
		typeErrors = new ArrayList<>();
	}

	@Override
	public Visitor enter(Node n) {
		if(isScopeNode(n)) context.enterScope();

		return this;
	}

	@Override
	public Node leave(Node n, Node old) {
		// TODO: if n == old, we need to make a copy of n before modifying it. It should then return the modified copy
		if(isScopeNode(n)) context.leaveScope();

		return n.typeCheck(this);
	}

	public void typeCheck(HasType actual, Type expected) {
		if(!expected.equals(actual.getType())){
			reportError(actual, "Expected type: " + expected + ", but got: " + actual.getType());
		}
	}
	
	public void checkResultIsUnit(HasResult result) {
		if(result.getResult() != Result.UNIT) {
			reportError(result, "Statement cannot return here");
		}
	}
	
	public List<SemanticError> getTypeErrors() {
		return typeErrors;
	}

	public SemanticError getFirstError() {
		typeErrors.sort(BaseError.getComparator());
		return typeErrors.get(0);
	}
	
	public boolean hasError() {
		return typeErrors.size() > 0;
	}
	
	public void reportError(HasLocation location, String description) {
		typeErrors.add(new SemanticError(location, description));
	}
	
	private boolean isScopeNode(Node node) {
		return node instanceof Block;
	}
}
