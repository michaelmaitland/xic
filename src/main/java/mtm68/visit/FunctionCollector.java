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

	private Map<String, ContextType> initSymbolTable;
	private Map<String, FunctionDecl> functionDecls;
	
	private List<BaseError> errors;
	
	public FunctionCollector(Map<String, ContextType> initSymbolTable) {
		this.initSymbolTable = initSymbolTable;
		functionDecls = new HashMap<>();
		errors = new ArrayList<>();
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.extractFunctionDecl(this);
	}

	public void addFunctionDecl(FunctionDecl decl) {
		String funcName = decl.getId();
		if(functionDecls.containsKey(funcName)) {
			reportError(decl, decl.getId() + " declared multiple times in source file.");
		}
		functionDecls.put(decl.getId(), decl);
	}
	
	public Map<String, ContextType> getContext(){
		Map<String, ContextType> mergedTable = new HashMap<>();
		mergedTable.putAll(initSymbolTable);
		for(FunctionDecl decl : functionDecls.values()) {
			String funcName = decl.getId();
			ContextType funcType = new ContextType(decl.getArgs(), decl.getReturnTypes());
			if(mergedTable.containsKey(funcName)) {
				if(!funcType.equals(mergedTable.get(funcName)))
					reportError(decl, decl.getId() + " has mismatched type decl with interface decl.");
			}
			else
				mergedTable.put(funcName, funcType);
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
