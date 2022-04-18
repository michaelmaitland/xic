package mtm68.ast.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;

public class SymbolTable {
	
	Map<String, FunctionDecl> fDecls;
	Map<String, ClassDecl> cDecls;
	
	public SymbolTable(Interface i) {
		this.fDecls= new HashMap<>();
		this.cDecls= new HashMap<>();
		putFunctionDecls(i.getBody().getFunctionDecls());
		putClassDecls(i.getBody().getClassDecls());
	}
	
	public SymbolTable() {
		this.fDecls= new HashMap<>();
		this.cDecls= new HashMap<>();
	}
	
	private void putFunctionDecls(List<FunctionDecl> decls) {
		for(FunctionDecl decl : decls) {
			fDecls.put(decl.getId(), decl);
		}
	}

	private void putClassDecls(List<ClassDecl> decls) {
		for(ClassDecl decl : decls) {
			cDecls.put(decl.getId(), decl);
		}
	}

	public Map<String, FunctionDecl> getFunctionDecls() {
		return fDecls;
	}

	public Map<String, ClassDecl> getClassDecls() {
		return cDecls;
	}
	
	
}
