package mtm68.ast.nodes.stmts;

public class ErrorStatement extends Statement {
	
	private String errorMsg;

	public ErrorStatement(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ErrorStatement [errorMsg=" + errorMsg + "]";
	}
}
