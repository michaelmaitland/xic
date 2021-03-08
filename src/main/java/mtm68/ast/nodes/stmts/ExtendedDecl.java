package mtm68.ast.nodes.stmts;

import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.types.DeclType;
import mtm68.ast.types.Type;

public class ExtendedDecl extends Decl implements SingleAssignLHS {

	private DeclType type;
	
	public ExtendedDecl(String id, DeclType type) {
		super(id);
		this.type = type;
	}

	public DeclType getExtendedType() {
		return type;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public Optional<Type> getType() {
		return Optional.of(type.getType());
	}

	@Override
	public String toString() {
		return "ExtendedDecl [type=" + type + ", id=" + id + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		p.startList();
		p.printAtom(type.getTypePP());
		p.endList();
		for(Expr index : type.getIndices()) index.prettyPrint(p);
		p.endList();
	}
	
	
}
