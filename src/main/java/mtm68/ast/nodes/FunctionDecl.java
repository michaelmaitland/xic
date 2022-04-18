package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.Type;
import mtm68.visit.SymbolCollector;
import mtm68.visit.NodeToIRNodeConverter;
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
	
	@Override
	public Node visitChildren(Visitor v) {
		List<SimpleDecl> newArgs = acceptList(this.args, v);
		if(newArgs != args) {
			FunctionDecl decl = copy();
			decl.args = newArgs;
			return decl;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}
	
	@Override
	public Node extractFunctionDecl(SymbolCollector sc) {
		sc.addFunctionDecl(this);
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		/* There is no IR conversion that needs to be done */
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionDecl other = (FunctionDecl) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (returnTypes == null) {
			if (other.returnTypes != null)
				return false;
		} else if (!returnTypes.equals(other.returnTypes))
			return false;
		return true;
	}
}
