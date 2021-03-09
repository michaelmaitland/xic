package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
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

	@Override
	public void prettyPrint(SExpPrinter p) {
		//p.startList();
		p.printAtomNoSpace("(" + id + type.getPP() + ")");
		//p.printAtomNoSpace(type.getPP());
		//p.endList();
	}
	
	
}
