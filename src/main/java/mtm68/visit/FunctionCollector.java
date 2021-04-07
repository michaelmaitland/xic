package mtm68.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.ContextType;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;

public class FunctionCollector extends Visitor{

	private Map<String, FunctionDecl> useFuncTable;
	private Map<String, FunctionDecl> progFuncTable;
	
	private List<BaseError> errors;
	
	public FunctionCollector(Map<String, FunctionDecl> initSymbolTable) {
		this.useFuncTable = initSymbolTable;
		progFuncTable = new HashMap<>();
		errors = new ArrayList<>();
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.extractFunctionDecl(this);
	}
	
	public Map<String, FunctionDecl> visit(Node root){
		root.accept(this);
		return getCombinedFuncTable();
	}

	public void addFunctionDecl(FunctionDecl decl) {
		String funcName = decl.getId();
		if(progFuncTable.containsKey(funcName)) {
			reportError(decl, decl.getId() + " declared multiple times in source file.");
		}
		progFuncTable.put(decl.getId(), decl);
	}
	
	public Map<String, FunctionDecl> getCombinedFuncTable(){
		Map<String, FunctionDecl> mergedTable = new HashMap<>();
		mergedTable.putAll(useFuncTable);
		for(FunctionDecl decl : progFuncTable.values()) {
			String funcName = decl.getId();
			if(mergedTable.containsKey(funcName)) {
				if(!decl.equals(mergedTable.get(funcName)))
					reportError(decl, decl.getId() + " has mismatched type decl with interface decl.");
			}
			else
				mergedTable.put(funcName, decl);
		}
		return mergedTable;
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
