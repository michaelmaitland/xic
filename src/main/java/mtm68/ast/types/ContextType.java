package mtm68.ast.types;

import java.util.ArrayList;
import java.util.List;

import mtm68.ast.nodes.stmts.SimpleDecl;

public class ContextType {
	private Type type;
	private List<Type> argTypes;
	private List<Type> returnTypes;
	private boolean isFunctionDecl;
	
	public ContextType(List<SimpleDecl> args, List<Type> returnTypes) {
		List<Type> argTypes = new ArrayList<>();
		for(SimpleDecl decl : args) argTypes.add(decl.getType());
		this.argTypes = argTypes;
		this.returnTypes = returnTypes;
		this.isFunctionDecl = true;
	}

	public ContextType(Type type) {
		this.type = type;
		this.isFunctionDecl = false;
	}

	public ContextType(List<Type> returnTypes) {
		this.returnTypes = returnTypes;
		this.isFunctionDecl = false;
	}

	public Type getType() {
		return type;
	}

	public List<Type> getArgTypes() {
		return argTypes;
	}

	public List<Type> getReturnTypes() {
		return returnTypes;
	}

	public boolean isFunctionDecl() {
		return isFunctionDecl;
	}
	
}
