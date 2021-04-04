package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO: why not return this?
		return null;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {
		/** There is no conversion to be done */
		return this;
	}
}
