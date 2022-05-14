package mtm68.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.symbol.SymbolTable;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;
import mtm68.util.ArrayUtils;

public class SymbolCollector extends Visitor{

	private SymbolTable useSymTable;
	private SymbolTable progSymTable;
	
	private List<BaseError> errors;
	
	public SymbolCollector(SymbolTable initSymbolTable) {
		this.useSymTable = initSymbolTable;
		progSymTable = new SymbolTable();
		errors = new ArrayList<>();
	}
	
	public SymbolCollector() {
		this(new SymbolTable());
	}

	@Override
	public Node leave(Node parent, Node n) {
		Node newN = n.extractFunctionDecl(this);
		Node newN2 = n.extractFields(this);
		return newN2.extractClassDecl(this);
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
		// Do not save methods as part of functions
		if(decl.isMethod()) {
			return;
		}

		String funcName = decl.getId();
		if(progSymTable.containsFunc(funcName)) {
			reportError(decl, "Function " + decl.getId() + " declared multiple times in source file.");
		}
		progSymTable.putFunc(decl.getId(), decl);
	}
	
	public void addFields(ClassDefn classDefn) {
		List<String> decls = classDefn.getBody()
									  .getFields()
									  .stream()
									  .map(SimpleDecl::getId)
									  .collect(Collectors.toList());
		
		// TODO multiple declaration error checking 
		
		progSymTable.putFields(classDefn.getId(), decls);
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
		combineFields(merged.getFields(), toMerge.getFields());
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
	
	private void combineFields(Map<String, List<String>> merged, Map<String, List<String>> toMerge) {
		
		for(String className : toMerge.keySet()) {
			List<String> fields = toMerge.get(className);
			if(!merged.containsKey(className)) {
				merged.put(className, ArrayUtils.elems(fields));
			} else {
				List<String> fieldsMerged = merged.get(className);
				fieldsMerged.addAll(fields);
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
