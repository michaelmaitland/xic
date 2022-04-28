package mtm68.ast.nodes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRClassDefn;
import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Program extends Node implements Root {
	
	private List<Use> useStmts;
	private ProgramBody body;
	private IRCompUnit irCompUnit;

	public Program(List<Use> useStmts, ProgramBody body) {
		this.useStmts = useStmts;
		this.body = body;
	}
	
	public Program(List<Use> useStmts, List<FunctionDefn> fDefns) {
		this.useStmts = useStmts;
		this.body = new ProgramBody(fDefns, null);
	}

	public List<Use> getUseStmts() {
		return useStmts;
	}
	
	public ProgramBody getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "Program [useStmts=" + useStmts + ", body=" + body+ "]";
	}
	
	public IRCompUnit getIrCompUnit() {
		return irCompUnit;
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();

		// Use Statements
		p.startUnifiedList();
		for(Use use : useStmts) use.prettyPrint(p);
		p.endList();
		
		body.prettyPrint(p);
		
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<Use> newUseStmts = acceptList(useStmts, v);
		ProgramBody newBody = body.accept(v);

		if(newUseStmts != useStmts || newBody != body) {
			Program prog = copy();
			prog.useStmts = newUseStmts;
			prog.body = newBody;
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
		Map<String, IRFuncDefn> irFuncDefns = body.getFunctionDefns().stream()
			.map(FunctionDefn::getIRFuncDefn)
			.collect(Collectors.toMap(IRFuncDefn::name, v -> v));
		
		Map<String, IRClassDefn> irClassDefns = body.getClassDefns().stream()
			.map(ClassDefn::getIRClassDefn)
			.collect(Collectors.toMap(IRClassDefn::getClassName, v -> v));

		IRCompUnit compUnit = inf.IRCompUnit(cv.getProgramName(), irFuncDefns, irClassDefns);
		
		Program newProgram = copy();
		newProgram.irCompUnit = compUnit;

		/** Not part of IR rep */
		return newProgram;
	}
}
