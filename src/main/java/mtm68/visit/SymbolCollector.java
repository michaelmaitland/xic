package mtm68.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Node;
import mtm68.ast.symbol.SymbolTable;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;

public class SymbolCollector extends Visitor{

	private SymbolTable useSymTable;
	private SymbolTable progSymTable;
	
	private List<BaseError> errors;
	
	public SymbolCollector(SymbolTable initSymbolTable) {
		this.useSymTable = initSymbolTable;
		progSymTable = new SymbolTable();
		errors = new ArrayList<>();
	}

	@Override
	public Node leave(Node parent, Node n) {
		Node newN = n.extractFunctionDecl(this);
		return newN.extractClassDecl(this);
	}
	
	public SymbolTable visit(Node root){
		root.accept(this);
		return getCombinedSymTable();
	}
	
	
	public void addClassDecl(ClassDecl decl) {
		String className = decl.getId();
		if(progSymTable.containsClass(className)) {
			reportError(decl, "Class " + decl.getId() + " declared multiple times in source file.");
		}
		progSymTable.putClass(decl.getId(), decl);
	}

	public void addFunctionDecl(FunctionDecl decl) {
		String funcName = decl.getId();
		if(progSymTable.containsFunc(funcName)) {
			reportError(decl, "Function " + decl.getId() + " declared multiple times in source file.");
		}
		progSymTable.putFunc(decl.getId(), decl);
	}
	
	public SymbolTable getCombinedSymTable(){
		SymbolTable mergedTable = new SymbolTable();
		mergedTable.putAll(useSymTable);
		
		combineTables(mergedTable, progSymTable);
		
		return mergedTable;
	}

	/**
	 * Merges all symbols in toMerge into merged and reports any errors.
	 */
	private void combineTables(SymbolTable merged, SymbolTable toMerge) {
		combineFuncs(merged.getFunctionDecls(), toMerge.getFunctionDecls());
		combineClasses(merged.getClassDecls(), toMerge.getClassDecls());
	}
	
	private void combineFuncs(Map<String, FunctionDecl> merged, Map<String, FunctionDecl> toMerge) {
		for(FunctionDecl decl : toMerge.values()) {
			String funcName = decl.getId();
			if(merged.containsKey(funcName)) {
				if(!decl.equals(merged.get(funcName)))
					reportError(decl, "Function " + decl.getId() + " has mismatched type decl with interface decl.");
			}
			else {
				merged.put(funcName, decl);
			}
		}
	}
	
	private void combineClasses(Map<String, ClassDecl> merged, Map<String, ClassDecl> toMerge) {
		for(ClassDecl decl : toMerge.values()) {
			String className = decl.getId();
			if(merged.containsKey(className)) {
				if(!decl.equals(merged.get(className)))
					reportError(decl, "Class " + decl.getId() + " has mismatched type decl with interface decl.");
			}
			else {
				merged.put(className, decl);
			}
		}
	}

	public void reportError(HasLocation location, String description) {
		errors.add(new SemanticError(location, description));
	}
	
	public BaseError getFirstError() {
		errors.sort(BaseError.getComparator());
		return errors.get(0);
	}
	
	public boolean hasError() {
		return errors.size() > 0;
	}

	public List<BaseError> getErrors() {
		return errors;
	}
}
