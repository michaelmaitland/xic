package mtm68.ast.symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;

public class DispatchVectorGenerator {

	private SymbolTable symbolTable; 
	
	public DispatchVectorGenerator(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public Map<String, DispatchVector> get() {
		Map<String, DispatchVector> dvs = new HashMap<>();
		Map<String, ClassDecl> classes = symbolTable.getClassDecls();
		
		for(String className : classes.keySet()) {
			DispatchVector dv = get(className, dvs, classes);
			dvs.put(className, dv);
		}
		
		return dvs;
	}
	
	private DispatchVector get(String className, Map<String, DispatchVector> dvs, Map<String, ClassDecl> classes) {
		if(dvs.containsKey(className)) {
			return dvs.get(className);
		}
		
		DispatchVector dv = new DispatchVector();
		ClassDecl cDecl = classes.get(className);
		if(cDecl.getSuperType() != null) {
			DispatchVector superDv = get(cDecl.getSuperType(), dvs, classes);
			dv = superDv.copy();
		} 
		addMethods(dv, cDecl.getMethodDecls());
		
		return dv;
	}
	
	private void addMethods(DispatchVector dv, List<FunctionDecl> methods) {
		for(FunctionDecl decl : methods) {
			if(!dv.contains(decl.getId())) {
				dv.add(decl.getId());
			}
		}
	}
}
