package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class SimpleDecl extends Decl {
	
	private Type type;
	
	public SimpleDecl(String id, Type type) {
		super(id);
		this.type = type;
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SimpleDecl [type=" + type + ", id=" + id + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		p.printAtom (type.getPP());
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkDecl(this);

		SimpleDecl decl = new SimpleDecl(id, type);
		decl.result = Result.UNIT;

		return decl;
	}
	
	
}
