package mtm68.ast.nodes.stmts;

import mtm68.ast.types.Type;

public class SimpleDecl extends Decl {
	
	private Type type;
	
	public SimpleDecl(String id, Type type) {
		super(id);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SimpleDecl [type=" + type + ", id=" + id + "]";
	}
}
