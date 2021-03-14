package mtm68.ast.nodes;

import java.math.BigInteger;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class IntLiteral extends Literal<BigInteger> {
	
	public IntLiteral(BigInteger value) {
		super(value);
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(value.toString()); 
	}
	
}
