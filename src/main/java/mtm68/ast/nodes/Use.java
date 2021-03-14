package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Use extends Node {
	
	private String id;

	public Use(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Use [id=" + id + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("use " + id);
		p.endList();
	}

}
