package mtm68.ast.symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.util.ArrayUtils;

public class ProgramSymbols {
	
	private List<FunctionDecl> funcDecls;

	private List<ClassDecl> classDecls;
	
	private Map<String, List<String>> classToFieldNames;
	
	public ProgramSymbols(List<FunctionDecl> funcDecls, List<ClassDecl> classDecls) {
		this(funcDecls, classDecls, new HashMap<>());
	}
	
	public ProgramSymbols(List<FunctionDecl> funcDecls, List<ClassDecl> classDecls, Map<String, List<String>> classToFieldNames) {
		super();
		this.funcDecls = funcDecls;
		this.classDecls = classDecls;
		this.classToFieldNames = classToFieldNames;
	}
	
	public ProgramSymbols() {
		this(ArrayUtils.empty(), ArrayUtils.empty(), new HashMap<>());
	}

	public List<FunctionDecl> getFuncDecls() {
		return funcDecls;
	}
	
	public List<ClassDecl> getClassDecls() {
		return classDecls;
	}
	
	public Map<String, List<String>> getClassFields() {
		return classToFieldNames;
	}
}
