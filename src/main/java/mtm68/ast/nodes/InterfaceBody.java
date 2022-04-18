package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.util.ArrayUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class InterfaceBody extends Node {
	
	private List<FunctionDecl> functionDecls;
	private List<ClassDecl> classDecls;

	public InterfaceBody(List<FunctionDecl> fDecls) {
		this(fDecls, null);
	}

	public InterfaceBody(List<FunctionDecl> functionDecls,
			List<ClassDecl> classDecls) {
		this.functionDecls = functionDecls;
		this.classDecls = classDecls;
	}
	
	public InterfaceBody() {
		this(ArrayUtils.empty(), ArrayUtils.empty());
	}
	
	public void addFunctionDecl(FunctionDecl fDecl) {
		functionDecls.add(fDecl);
	}

	public void addClassDecl(ClassDecl cDecl) {
		classDecls.add(cDecl);
	}

	public List<FunctionDecl> getFunctionDecls() {
		return functionDecls;
	}
	
	public List<ClassDecl> getClassDecls() {
		return classDecls;
	}

	@Override
	public String toString() {
		return "InterfaceBody [fDecls=" + functionDecls + ", cDecls=" + classDecls + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();
		p.startUnifiedList();

		for(FunctionDecl fDecl : functionDecls) {
			p.startList();
			fDecl.prettyPrint(p);
			p.endList();
		}
		
		for(ClassDecl cDecl : classDecls) {
			p.startList();
			cDecl.prettyPrint(p);
			p.endList();
		}
		
		p.endList();
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<FunctionDecl> newFunctionDecls = acceptList(functionDecls, v);
		List<ClassDecl> newClassDecls = acceptList(classDecls, v);

		if(newFunctionDecls != functionDecls  || newClassDecls != classDecls) {
			InterfaceBody i = copy();
			i.functionDecls = newFunctionDecls;
			i.classDecls = newClassDecls;
			return i;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv,  IRNodeFactory inf) {
		// TODO
		return this;
	}
}
