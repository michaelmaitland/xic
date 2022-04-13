package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassDefn extends Node {
	
	private ClassDecl classDecl;
//	private ClassDefn superDefn; // TODO: do we need this? We shall see.
	private List<FunctionDefn> methodDefns;
	private List<Var> fields;
	
	public ClassDefn(ClassDecl classDecl, List<FunctionDefn> methodDefns,
			List<Var> fields) {
		this.classDecl = classDecl;
		this.methodDefns = methodDefns;
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "ClassDefn [classDecl=" + classDecl+ ", methodDefns=" + methodDefns 
				+ ", fields=" + fields + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		classDecl.prettyPrint(p);
		
		// Method Definitions
		p.startList();
		for(FunctionDefn methodDefn : methodDefns) methodDefn.prettyPrint(p);
		p.endList();

		// Fields
		p.startList();
		for(Var field : fields) field.prettyPrint(p);
		p.endList();

		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		ClassDecl newClassDecl = classDecl.accept(v);
		List<FunctionDefn> newMethodDefns = acceptList(methodDefns, v);
		List<Var> newFields = acceptList(fields, v);
		
		if(newClassDecl!= classDecl 
		|| newMethodDefns != methodDefns 
		|| newFields != fields) {
			ClassDefn defn = copy();
			defn.classDecl = newClassDecl;
			defn.methodDefns = newMethodDefns;
			defn.fields = newFields;
			return defn;
		}
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		// TODO
		return null;
	}
}
