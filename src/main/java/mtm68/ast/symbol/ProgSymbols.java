package mtm68.ast.symbol;

import java.util.List;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;

public class ProgSymbols {
	
	private List<FunctionDecl> funcDecls;

	private List<ClassDecl> classDecls;
	
	public ProgSymbols(List<FunctionDecl> funcDecls, List<ClassDecl> classDecls) {
		super();
		this.funcDecls = funcDecls;
		this.classDecls = classDecls;
	}
	
	public ProgSymbols() {}

	public List<FunctionDecl> getFuncDecls() {
		return funcDecls;
	}
	
	public List<ClassDecl> getClassDecls() {
		return classDecls;
	}

	
}
