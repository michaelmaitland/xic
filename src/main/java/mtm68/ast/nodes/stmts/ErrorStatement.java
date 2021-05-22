package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Node;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		/* No IR info needed */
		return this;
	}
}
