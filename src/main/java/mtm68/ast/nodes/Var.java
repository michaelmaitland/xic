package mtm68.ast.nodes;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.util.FreshTempGenerator;
import mtm68.visit.FunctionInliner;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Var extends Expr {
	
	private String id;
	
	public Var(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(id);
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkVar(this);
		return copyAndSetType(type);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		String t = cv.newTemp(id);
		return copyAndSetIRExpr(inf.IRTemp(t));
	}
	
	@Override
	public Node renameVars(Map<String, String> varMap) {
		Var var = this.copy();
		
		if(!varMap.containsKey(id)) {
			varMap.put(id, FreshTempGenerator.getFreshTemp());
		}
		
		var.setId(varMap.get(id));
		
		return var;
	}
}
