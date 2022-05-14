package mtm68.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRClassDefn;
import edu.cornell.cs.cs4120.ir.IRData;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.SymbolCollector;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ClassDefn extends Node {
	
	private String id;
	private ClassDecl classDecl;
	private String superType;
	private ClassBody body;
	private IRClassDefn irClassDefn;
	
	public ClassDefn(String id, String superType, ClassBody body) {
		this.id = id;
		this.superType = superType;
		this.body = body;
		
		List<FunctionDecl> fDecls = body.getMethodDefns()
									    .stream()
									    .map(FunctionDefn::getFunctionDecl)
									    .collect(Collectors.toList());
		this.classDecl = new ClassDecl(id, superType, fDecls);
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
	
	public ClassDecl getClassDecl() {
		return classDecl;
	}

	public void setIRClassDefn(IRClassDefn irClassDefn) {
		this.irClassDefn = irClassDefn;
	}
	
	public IRClassDefn getIRClassDefn() {
		return irClassDefn;
	}
	
	@Override
	public Node extractFields(SymbolCollector sc) {
		sc.addFields(this);
		return this;
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
		ClassDecl newClassDecl = classDecl.accept(v);
		
		if(newBody != body || newClassDecl != classDecl) {
			ClassDefn defn = copy();
			defn.body = newBody;
			defn.classDecl = newClassDecl;
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
		
		// Discard IRFunctionDefn and reconstruct with correct naming
		List<IRFuncDefn> methods = body.getMethodDefns().stream().map(fDefn -> {
			FunctionDecl functionDecl = fDefn.getFunctionDecl();
			String methodName = cv.saveAndGetMethodSymbol(functionDecl, id);

			IRSeq seq = cv.constructFuncDefnSeq(functionDecl, fDefn.getBody());
			return inf.IRFuncDefn(methodName, seq, functionDecl.getArgs().size());
		}).collect(Collectors.toList());
		
		IRData dv = cv.constructDispatchVector(this);
		IRClassDefn irClassDefn = inf.IRClassDefn(id, methods, dv);
		
		ClassDefn newDefn = copy();
		newDefn.setIRClassDefn(irClassDefn);
		return newDefn;
	}
}
