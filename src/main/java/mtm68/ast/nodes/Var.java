package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.ThisAugmenter;
import mtm68.visit.TypeChecker;
import mtm68.visit.VariableRenamer;
import mtm68.visit.Visitor;

public class Var extends Expr {
	
	private String id;
	
	public Var(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(id);
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkVar(this);
		return copyAndSetType(type);
	}
	
	@Override
	public Node augmentWithThis(ThisAugmenter ta) {
		// Change x to this.x if its a field.
		if(ta.isField(this)) {
			return new FieldAccess(this);
		} else {
			return this;
		}
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		String t = cv.newTemp(id);
		return copyAndSetIRExpr(inf.IRTemp(t));
	}
	
	@Override
	public Node renameVars(VariableRenamer vr) {
		return vr.updateVar(this);
	}
}
