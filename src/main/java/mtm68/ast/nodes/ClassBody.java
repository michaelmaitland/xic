package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.Decl;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.util.ArrayUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassBody extends Node {
	
	private List<FunctionDefn> methodDefns;
	private List<SimpleDecl> fields;
	
	public ClassBody(List<FunctionDefn> methodDefns, List<SimpleDecl> fields) {
		this.methodDefns = methodDefns;
		this.fields = fields;
	}
	
	public ClassBody() {
		this(ArrayUtils.empty(), ArrayUtils.empty());
	}
	
	public List<FunctionDefn> getMethodDefns() {
		return methodDefns;
	}

	public List<SimpleDecl> getFields() {
		return fields;
	}

	public void addMethod(FunctionDefn methodDefn) {
		methodDefn.getFunctionDecl().setIsMethod(true);
		methodDefns.add(methodDefn);
	}
	
	public void addField(SimpleDecl field) {
		fields.add(field);
	}

	@Override
	public String toString() {
		return "ClassBody [methodDefns=" + methodDefns + ", fields=" + fields + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		
		// Method Definitions
		p.startList();
		for(FunctionDefn methodDefn : methodDefns) methodDefn.prettyPrint(p);
		p.endList();

		// Fields
		p.startList();
		for(Decl field : fields) field.prettyPrint(p);
		p.endList();

		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<FunctionDefn> newMethodDefns = acceptList(methodDefns, v);
		List<SimpleDecl> newFields = acceptList(fields, v);
		
		if(newMethodDefns != methodDefns || newFields != fields) {
			ClassBody defn = copy();
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
		return this;
	}
}
