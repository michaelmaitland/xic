package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class BoolLiteral extends Literal<Boolean>{

	public BoolLiteral(Boolean value) {
		super(value);
		setType(Types.BOOL);
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(value.toString()); 
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {
		long constVal = value ? 1L : 0L;
		
		return copyAndSetIRExpr(irFactory.IRConst(constVal));
	}
}
