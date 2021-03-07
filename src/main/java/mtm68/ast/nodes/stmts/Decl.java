package mtm68.ast.nodes.stmts;

public abstract class Decl extends Statement {
	
	protected String id;
	
	public Decl(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

}
