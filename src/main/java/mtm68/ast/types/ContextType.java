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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argTypes == null) ? 0 : argTypes.hashCode());
		result = prime * result + (isFunctionDecl ? 1231 : 1237);
		result = prime * result
				+ ((returnTypes == null) ? 0 : returnTypes.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextType other = (ContextType) obj;
		if (argTypes == null) {
			if (other.argTypes != null)
				return false;
		} else if (!argTypes.equals(other.argTypes))
			return false;
		if (isFunctionDecl != other.isFunctionDecl)
			return false;
		if (returnTypes == null) {
			if (other.returnTypes != null)
				return false;
		} else if (!returnTypes.equals(other.returnTypes))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
