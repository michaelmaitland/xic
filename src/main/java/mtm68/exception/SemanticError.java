package mtm68.exception;

import mtm68.ast.nodes.HasLocation;

public class SemanticError extends BaseError {
	private HasLocation location;
	private String description;

	public SemanticError(HasLocation location, String description) {
		super(ErrorKind.SEMANTIC, location.getLine(), location.getColumn());
		this.location = location;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getFileErrorMessage() {
		return location.getLine() + ":" + location.getColumn() + " error:" + getDescription();
	}

}
