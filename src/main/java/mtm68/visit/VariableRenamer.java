package mtm68.visit;

import java.util.HashMap;
import java.util.Map;

import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.stmts.Decl;
import mtm68.util.FreshTempGenerator;

public class VariableRenamer extends Visitor{
	private Map<String, String> newNames;
	
	public VariableRenamer() {
		newNames = new HashMap<>();
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.renameVars(this);
	}
	
	public Var updateVar(Var v) {
		Var newVar = v.copy();
		String id = newVar.getId();
		
		newVar.setId(getNewName(id));
		return newVar;
	}
	
	public Decl updateDecl(Decl d) {
		Decl newDecl = d.copy();
		String id = newDecl.getId();
		
		newDecl.setId(getNewName(id));
		return newDecl;
	}
	
	private String getNewName(String old) {
		if(!newNames.containsKey(old)) {
			newNames.put(old, FreshTempGenerator.getFreshTemp());
		}
		
		return newNames.get(old);
	}
}
