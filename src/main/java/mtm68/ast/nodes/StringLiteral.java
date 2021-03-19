package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.util.StringUtils;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class StringLiteral extends Literal<String>{

	public StringLiteral(String value) {
		super(value);
		setType(Types.ARRAY(Types.INT));
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom("\"" + StringUtils.preserveNewlines(value) + "\""); 
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
