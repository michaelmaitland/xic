package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.SymbolCollector;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassDecl extends Node {
	
	private String id;
	private String superType;
	private List<FunctionDecl> methodDecls;

	public ClassDecl(String id, String superType, List<FunctionDecl> methodDecls) {
		this.id = id;
		this.superType = superType;
		this.methodDecls = methodDecls;
	}
	
	public ClassDecl(String id, List<FunctionDecl> methodDecls) {
		this.id = id;
		this.methodDecls= methodDecls;
	}

	public String getId() {
		return id;
	}
	
	public String getSuperType() {
		return superType;
	}

	public List<FunctionDecl> getMethodDecls() {
		return methodDecls;
	}

	@Override
	public String toString() {
		return "ClassDecl [id=" + id + ", superType=" + superType 
				+ ", methodDecls=" + methodDecls + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(id);
		
		// Super Type
		p.printAtom(superType);

		// Methods
		p.startList();
		for(FunctionDecl m : methodDecls) m.prettyPrint(p);
		p.endList(); 
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<FunctionDecl> newMethodDecls = acceptList(this.methodDecls, v);

		if(newMethodDecls != methodDecls) {
			ClassDecl decl = copy();
			decl.methodDecls = newMethodDecls;
			return decl;
		} 

		return this;
	}
	
	public Node extractClassDecl(SymbolCollector sc) {
		sc.addClassDecl(this);
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
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
		ClassDecl other = (ClassDecl) obj;
		if (methodDecls == null) {
			if (other.methodDecls!= null)
				return false;
		} else if (!methodDecls.equals(other.methodDecls))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (superType == null) {
			if (other.superType!= null)
				return false;
		} else if (!superType.equals(other.superType))
			return false;

		return true;
	}
}
