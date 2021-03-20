package mtm68.visit;

import java.util.Map;

import mtm68.ast.nodes.Node;
import mtm68.ast.types.ContextType;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Type;
import mtm68.ast.types.TypingContext;

public class TypeChecker extends Visitor {
	TypingContext context;

	public TypeChecker(Map<String, ContextType> initSymTable) {
		this.context = new TypingContext(initSymTable);
	}
	
	public TypeChecker() {
		this.context = new TypingContext();
	}

	@Override
	public Visitor enter(Node n) {
		return this;
	}

	@Override
	public Node leave(Node n, Node old) {
		// TODO: if n == old, we need to make a copy of n before modifying it. It should then return the modified copy
		return n.typeCheck(this);
	}

	public void typeCheck(HasType actual, Type expected) {
		if(!expected.equals(actual.getType())){
			// TODO: REPORT TYPE ERROR
		}
	}
}
