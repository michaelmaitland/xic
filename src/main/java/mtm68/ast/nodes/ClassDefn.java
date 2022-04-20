package mtm68.ast.nodes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRClassDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassDefn extends Node {
	
	private String id;
	private String superType;
	private ClassBody body;
	private IRClassDefn irClassDefn;
	
	public ClassDefn(String id, String superType, ClassBody body) {
		this.id = id;
		this.superType = superType;
		this.body = body;
	}
	
	public ClassDefn(String id, ClassBody body) {
		this(id, null, body);
	}

	public String getId() {
		return id;
	}

	public String getSuperType() {
		return superType;
	}

	public ClassBody getBody() {
		return body;
	}

	public void setIrClassDefn(IRClassDefn irClassDefn) {
		this.irClassDefn = irClassDefn;
	}

	@Override
	public String toString() {
		return "ClassDefn [id=" + id + ", superType=" + superType + ", body=" + body + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		p.printAtom(superType);
		body.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		ClassBody newBody = body.accept(v);
		
		if(newBody != body) {
			ClassDefn defn = copy();
			defn.body = newBody;
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
		/* 
		 * The conversion to IR for a ClassDefn must build a VTable and keep a reference
		 * to it so allocations of this object type can put a pointer to the DV 
		 * in its representation.
		 */
		
		// TODO encode func names correctly. We can add a bool isMethod which the converter
		// can check to determine how the NodeToIRConverter saves the function.
		Map<String, String> symbols = body.getMethodDefns()
			.stream()
			.map(FunctionDefn::getFunctionDecl)
			.collect(Collectors.toMap(FunctionDecl::getId, d -> cv.saveAndGetMethodSymbol(d, id)));

		List<IRStmt> fields = body.getFields()
								  .stream()
								  .map(SimpleDecl::getIRStmt)
								  .collect(Collectors.toList());

		IRClassDefn irClassDefn = inf.IRClassDefn(id, superType, methods, fields);
		
		ClassDefn newDefn = copy();
		newDefn.setIrClassDefn(irClassDefn);
		return newDefn;
	}
}
