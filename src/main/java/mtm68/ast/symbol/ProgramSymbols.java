package mtm68.ast.symbol;

import java.util.List;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.util.ArrayUtils;

public class ProgramSymbols {
	
	private List<FunctionDecl> funcDecls;

	private List<ClassDecl> classDecls;
	
	private Map<String, List<String>> classToFieldsName;
	
	public ProgramSymbols(List<FunctionDecl> funcDecls, List<ClassDecl> classDecls) {
		super();
		this.funcDecls = funcDecls;
		this.classDecls = classDecls;
	}
	
	public ProgramSymbols() {
		this(ArrayUtils.empty(), ArrayUtils.empty());
	}

	public List<FunctionDecl> getFuncDecls() {
		return funcDecls;
	}
	
	public List<ClassDecl> getClassDecls() {
		return classDecls;
	}
}
