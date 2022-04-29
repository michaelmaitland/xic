package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.ObjectType;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FieldAccess extends Expr {
	
	private Var obj;
	private Var field;

	public FieldAccess(Var obj, Var field) {
		this.obj = obj;
		this.field = field;
	}
	
	public FieldAccess(Var field) {
		this(new Var("this"), field);
	}

	public Var getObj() {
		return obj;
	}
	
	public Var getField() {
		return field;
	}
	
	@Override
	public String toString() {
		return "FieldAccess[obj=" + obj + ", field=" + field+ "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		obj.prettyPrint(p);
		p.printAtom(".");
		field.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Var newObj = obj.accept(v);
		Var newField = field.accept(v);
		if(newObj != obj || newField != field) {
			FieldAccess newFieldAccess = copy();
			newFieldAccess.obj = newObj;
			newFieldAccess.field = newField;
			return newFieldAccess;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		ObjectType objType = (ObjectType) obj.getType();
		int index = cv.getFieldIndex(objType, field);

		IRMem field = cv.getOffsetIntoArr(obj.getIRExpr(), inf.IRConst(index));

		return copyAndSetIRExpr(field);
	}
}
