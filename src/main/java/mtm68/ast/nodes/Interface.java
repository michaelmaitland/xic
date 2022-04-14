package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Interface extends Node implements Root {
	
	private List<Use> uses;
	private InterfaceBody body;

	public Interface(InterfaceBody body) {
		this(null, body);
	}

	public Interface(List<Use> uses, InterfaceBody body) {
		this.uses = uses;
		this.body = body;
	}

	public List<Use> getUses() {
		return uses;
	}
	
	public InterfaceBody getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "Interface [uses=" + uses + ", body=" + body + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();

		for(Use use : uses) {
			p.startList();
			use.prettyPrint(p);
			p.endList();
		}

		body.prettyPrint(p);
		
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<Use> newUses = acceptList(uses, v);
		InterfaceBody newBody = body.accept(v);

		if(newUses != uses || newBody != body ) {
			Interface i = copy();
			i.uses = uses;
			i.body = body;
			return i;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv,  IRNodeFactory inf) {
		// TODO
		return this;
	}
}
