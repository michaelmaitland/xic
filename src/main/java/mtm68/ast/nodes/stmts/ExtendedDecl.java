package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.DeclType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
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
		List<Expr> indices = type.getIndices();
		List<Expr> newIndices = acceptList(indices, v);
		
		if(indices == newIndices) return this;
		
		DeclType declType = new DeclType(type.getType(), newIndices);
		return new ExtendedDecl(id, declType);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		List<Expr> indices = type.getIndices();
		List<Type> expectedTypes = indices.stream()
				.map(e -> Types.INT)
				.collect(Collectors.toList());
		tc.checkTypes(this, indices, expectedTypes);
		tc.checkDecl(this);
		
		ExtendedDecl decl = new ExtendedDecl(id, type);
		decl.result = Result.UNIT;

		return decl;
	}
	
	
}
