package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.util.ArrayUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ProgramBody extends Node implements Root {
	
	private List<FunctionDefn> functionDefns;
	private List<ClassDefn> classDefns;


	public ProgramBody(List<FunctionDefn> fDefns, List<ClassDefn> cDefns) {
		this.functionDefns = fDefns;
		this.classDefns = cDefns;
	}

	public ProgramBody() {
		this(ArrayUtils.empty(), ArrayUtils.empty());
	}
	
	public List<FunctionDefn> getFunctionDefns() {
		return functionDefns;
	}
	
	public static ProgramBody withFunctionDefn(FunctionDefn fd) {
		ProgramBody pb = new ProgramBody();
		pb.addFunctionDefn(fd);
		return pb;
	}
	
	public static ProgramBody withClassDefn(ClassDefn cd) {
		ProgramBody pb = new ProgramBody();
		pb.addClassDefn(cd);
		return pb;
	}
	
	public void addFunctionDefn(FunctionDefn fd) {
		functionDefns.add(fd);
	}

	public void addClassDefn(ClassDefn cd) {
		classDefns.add(cd);
	}

	public List<ClassDefn> getClassDefns() {
		return classDefns;
	}

	@Override
	public String toString() {
		return "ProgramBody [functionDefns =" + functionDefns + ", classDefns=" + classDefns + "]";
	}
	
	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();

		// Func Defns
		p.startUnifiedList();
		for(FunctionDefn defn : functionDefns) defn.prettyPrint(p);
		p.endList();
		
		// Class Defns
		p.startUnifiedList();
		for(ClassDefn defn : classDefns) defn.prettyPrint(p);
		p.endList();
		
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<FunctionDefn> newFunctionDefns = acceptList(functionDefns, v);
		List<ClassDefn> newClassDefns = acceptList(classDefns, v);

		if(newClassDefns != classDefns || newFunctionDefns != functionDefns) {
			ProgramBody prog = copy();
			prog.functionDefns = newFunctionDefns;
			prog.classDefns = newClassDefns;
			return prog;
		} 
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		// Defer to Program class to convert to IR.
		return this;
	}
}
