package mtm68.ast.nodes.stmts;

import mtm68.ast.nodes.Node;
import mtm68.ast.types.HasResult;
import mtm68.ast.types.Result;

public abstract class Statement extends Node implements HasResult {
	
	protected Result result;
	
	@Override
	public Result getResult() {
		return result;
	}
}