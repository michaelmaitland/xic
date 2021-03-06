package mtm68.ast.nodes;

public class Var extends Expr {
	
	private String id;
	
	public Var(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}

}
