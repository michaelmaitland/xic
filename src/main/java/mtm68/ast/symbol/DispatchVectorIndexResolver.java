package mtm68.ast.symbol;

import java.util.HashMap;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;

public class DispatchVectorIndexResolver {

	private ProgramSymbols symbols;
	private Map<String, ClassDecl> classNameToDecl;
	private Map<String, Map<String, Integer>> classNameToFuncIdToIndex;

	public DispatchVectorIndexResolver(ProgramSymbols symbols) {
		this.symbols = symbols;
		this.classNameToFuncIdToIndex = new HashMap<>();

		classNameToDecl = new HashMap<>();
		for (ClassDecl cDecl : symbols.getClassDecls()) {
			classNameToDecl.put(cDecl.getId(), cDecl);
		}

		gen();
	}
	
	/**
	 * Given a class definition A, return the index of method f in the dispatch
	 * vector of A with the property that all superclasses and subclasses of A
	 * return the same index for a given method name.
	 */
	public Integer getMethodIndex(String dispatchVectorClass, String methodName) {
		if(classNameToFuncIdToIndex.containsKey(dispatchVectorClass)) {
			Map<String, Integer> funcIdToIndex= classNameToFuncIdToIndex.get(dispatchVectorClass);
			if(funcIdToIndex.containsKey(methodName)) {
				return funcIdToIndex.get(methodName);
			}
		}
		return null;
	}
	
	public Map<String, Integer> getIndicies(String dispatchVectorClass) {
		if(classNameToFuncIdToIndex.containsKey(dispatchVectorClass)) {
			return classNameToFuncIdToIndex.get(dispatchVectorClass);
		}
		return null;
	}

	private void gen() {
		for (ClassDecl cDecl : symbols.getClassDecls()) {
			gen(cDecl);
		}
	}

	private void gen(ClassDecl cDecl) {
		// get indicies from super type if any
		Map<String, Integer> funcIdToIndex;
		if (cDecl.getSuperType() != null) {
			gen(classNameToDecl.get(cDecl.getSuperType()));
			Map<String, Integer> superClassFuncIdToIndex = classNameToFuncIdToIndex.get(cDecl.getSuperType());
			funcIdToIndex = new HashMap<String, Integer>(superClassFuncIdToIndex);
		} else {
			funcIdToIndex = new HashMap<>();
		}

		// add funcs that are not already in the map using the next
		// index 
		int index = funcIdToIndex.size();
		for (FunctionDecl fDecl : cDecl.getMethodDecls()) {
			if (!funcIdToIndex.containsKey(fDecl.getId())) {
				funcIdToIndex.put(fDecl.getId(), index);
				index++;

			}
		}
		
		classNameToFuncIdToIndex.put(cDecl.getId(), funcIdToIndex);
	}
}
