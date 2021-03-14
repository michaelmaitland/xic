package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class IntLiteral extends Literal<Long> {
	
	public IntLiteral(Long value) {
		super(value);
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(value.toString()); 
	}
	
}
