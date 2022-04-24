package mtm68.ast.symbol;

import java.util.HashMap;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;

public class DispatchVectorClassResolver {

	private ProgramSymbols symbols; 
	private	Map<String, ClassDecl> classNameToDecl;
	private Map<String, Map<String, String>> classIdToFuncIdToClassId;
	
	public DispatchVectorClassResolver(ProgramSymbols symbols) {
		this.symbols = symbols;
		classIdToFuncIdToClassId = new HashMap<>();

		classNameToDecl = new HashMap<>();
		for(ClassDecl cDecl : symbols.getClassDecls()) {
			classNameToDecl.put(cDecl.getId(), cDecl);
		}
		
		gen();
	}
	
	/**
	 * Given a method call o.m(), this function returns the class name that owns the
	 * function m that should be invoked.
	 * 
	 * Example: 
	 * Consider {@code class A { f() {}; g() {}; } }
	 * and      {@code class B extends A { g() {}; } }.
	 * If we call new B().f() we invoke the function f that belongs to A. But if
	 * we call new B().g() we invoke the function g that belongs to B.
	 */
	public String getClassDefiningMethod(String dispatchVectorClass, String methodName) {
		if(classIdToFuncIdToClassId.containsKey(dispatchVectorClass)) {
			Map<String, String> funcIdToClassId = classIdToFuncIdToClassId.get(dispatchVectorClass);
			if(funcIdToClassId.containsKey(methodName)) {
				return funcIdToClassId.get(methodName);
			}
		}
		return null;
	}
	
	public Map<String, String> getMethods(String dispatchVectorClass) {
		if(classIdToFuncIdToClassId.containsKey(dispatchVectorClass)) {
			return classIdToFuncIdToClassId.get(dispatchVectorClass);
		}
		return null;
	}
	
	private void gen() {
		for(ClassDecl cDecl : symbols.getClassDecls()) {
			Map<String, String> funcIdToClassId = new HashMap<>();
			gen(funcIdToClassId, cDecl);
			classIdToFuncIdToClassId.put(cDecl.getId(), funcIdToClassId);
		}
	}
	
	private void gen(Map<String, String> funcIdToClassId, ClassDecl cDecl) {
		for(FunctionDecl fDecl : cDecl.getMethodDecls()) {
			if(!funcIdToClassId.containsKey(fDecl.getId())) {
				funcIdToClassId.put(fDecl.getId(), cDecl.getId());
			} else if(cDecl.getSuperType() != null) {
				gen(funcIdToClassId, classNameToDecl.get(cDecl.getSuperType()));
			}
		}
	}
}
