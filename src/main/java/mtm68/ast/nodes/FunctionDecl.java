package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FunctionDecl extends Node {
	
	private String id;
	private List<SimpleDecl> args;
	private List<Type> returnTypes;

	public FunctionDecl(String id, List<SimpleDecl> args, List<Type> returnTypes) {
		this.id = id;
		this.args = args;
		this.returnTypes = returnTypes;
	}

	@Override
	public String toString() {
		return "FunctionDecl [id=" + id + ", args=" + args + ", returnTypes=" + returnTypes + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(id);
		
		//Args
		p.startList();
		for(SimpleDecl arg : args) arg.prettyPrint(p);
		p.endList(); 
		
		//Return Types
		p.startList();
		String typeString = "";
		for(Type type: returnTypes) typeString += type.getPP() + " "; 
		p.printAtom(typeString.trim());
		p.endList();
	}
	
	public String getId() {
		return id;
	}
	
	public List<SimpleDecl> getArgs() {
		return args;
	}
	
	public List<Type> getReturnTypes() {
		return returnTypes;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<SimpleDecl> args = visitList(this.args, v);

		// TODO check if need to copy
		return new FunctionDecl(id, args, returnTypes);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
