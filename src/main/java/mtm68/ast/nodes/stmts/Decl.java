package mtm68.ast.nodes.stmts;

import mtm68.ast.nodes.Node;
import mtm68.ast.types.HasType;
import mtm68.visit.VariableRenamer;

public abstract class Decl extends Statement implements HasType {
	
	protected String id;
	
	public Decl(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public Node renameVars(VariableRenamer vr) {
		return vr.updateDecl(this);
	}
}
