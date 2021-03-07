package mtm68.ast.nodes;

public class Use extends Node {
	
	private String id;

	public Use(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Use [id=" + id + "]";
	}

}
