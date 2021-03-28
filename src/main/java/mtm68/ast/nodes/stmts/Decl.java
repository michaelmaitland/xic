package mtm68.ast.nodes.stmts;

import mtm68.ast.types.HasType;

public abstract class Decl extends Statement implements HasType {
	
	protected String id;
	
	public Decl(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
