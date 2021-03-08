package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class StringLiteral extends Literal<String>{

	public StringLiteral(String value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom("\"" + value + "\""); 
	}
	

}
