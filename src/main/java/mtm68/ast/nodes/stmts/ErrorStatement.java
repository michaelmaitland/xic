package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class ErrorStatement extends Statement {
	
	private String errorMsg;

	public ErrorStatement(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ErrorStatement [errorMsg=" + errorMsg + "]";
	}

	//TODO Need more info!
	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(errorMsg);	
	}
	
	
}
