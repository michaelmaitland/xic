package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.DeclType;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ExtendedDecl extends Decl {

	private DeclType type;
	
	public ExtendedDecl(String id, DeclType type) {
		super(id);
		this.type = type;
	}

	public DeclType getExtendedType() {
		return type;
	}
	
	@Override
	public Type getType() {
		return getExtendedType().getType();
	}

	@Override
	public String toString() {
		return "ExtendedDecl [type=" + type + ", id=" + id + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		p.printAtom(type.getTypePP());
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
