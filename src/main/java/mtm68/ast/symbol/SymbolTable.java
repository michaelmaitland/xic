package mtm68.ast.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;

public class SymbolTable {
	
	private Map<String, FunctionDecl> fDecls;
	private Map<String, ClassDecl> cDecls;
	private Map<String, List<String>> fields;
	
	public SymbolTable(Interface i) {
		this.fDecls = new HashMap<>();
		this.cDecls = new HashMap<>();
		this.fields = new HashMap<>();
		putFunctionDecls(i.getBody().getFunctionDecls());
		putClassDecls(i.getBody().getClassDecls());
	}

	public SymbolTable() {
		this.fDecls = new HashMap<>();
		this.cDecls = new HashMap<>();
		this.fields = new HashMap<>();
	}
	
	public boolean containsFunc(String id) {
		return fDecls.containsKey(id);
	}
	
	public void putFields(String className, List<String> fieldNames) {
		fields.put(className, fieldNames);
	}
	
	public void putFunc(String id, FunctionDecl fDecl) {
		fDecls.put(id, fDecl);
	}
	
	public void putAll(SymbolTable symTable) {
		putFunctionDecls(symTable.getFunctionDecls());
		putClassDecls(symTable.getClassDecls());
		putFields(symTable.getFields());
	}
	
	public boolean containsClass(String id) {
		return cDecls.containsKey(id);
	}
	
	public void putClass(String id, ClassDecl cDecl) {
		cDecls.put(id, cDecl);
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
	
	private void putFields(Map<String, List<String>> fields) {
		this.fields.putAll(fields);
	}
	
	private void putFunctionDecls(Map<String, FunctionDecl> fDecls) {
		fDecls.putAll(fDecls);
	}

	private void putClassDecls(Map<String, ClassDecl> cDecls) {
		cDecls.putAll(cDecls);
	}

	public Map<String, FunctionDecl> getFunctionDecls() {
		return fDecls;
	}

	public Map<String, ClassDecl> getClassDecls() {
		return cDecls;
	}
	
	public Map<String, List<String>> getFields() {
		return fields;
	}
	
	public ProgramSymbols toProgSymbols() {
		List<FunctionDecl> fs = new ArrayList<>(fDecls.values());
		List<ClassDecl> cs = new ArrayList<>(cDecls.values());
		return new ProgramSymbols(fs, cs);
	}
}
