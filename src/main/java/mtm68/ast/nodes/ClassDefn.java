package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassDefn extends Node {
	
	private String id;
	private String superType;
	private ClassBody body;
	
	public ClassDefn(String id, String superType, ClassBody body) {
		this.id = id;
		this.superType = superType;
		this.body = body;
	}
	
	public ClassDefn(String id, ClassBody body) {
		this(id, null, body);
	}

	public String getId() {
		return id;
	}

	public String getSuperType() {
		return superType;
	}

	public ClassBody getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "ClassDefn [id=" + id + ", superType=" + superType + ", body=" + body + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		p.printAtom(superType);
		body.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		ClassBody newBody = body.accept(v);
		
		if(newBody != body) {
			ClassDefn defn = copy();
			defn.body = newBody;
			return defn;
		}
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		// TODO
		return null;
	}
}
