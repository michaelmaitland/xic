package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.util.StringUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class CharLiteral extends Literal<Character>{
	
	public CharLiteral(Character value) {
		super(value);
		setType(Types.INT);
	}
	
	@Override
	public String toString() {
		return "'" + value + "'";
	}
	
	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom("'" + StringUtils.preserveNewlines(value.toString()) + "'");
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
	public Node convertToIR(NodeToIRNodeConverter cv) {
		return copyAndSetIRExpr(new IRConst(value));
	}
}
