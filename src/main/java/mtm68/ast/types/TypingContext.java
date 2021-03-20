package mtm68.ast.types;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mtm68.ast.nodes.stmts.SimpleDecl;

public class TypingContext {
	private final String RHO = "!!!";
	private Deque<Map<String, ContextType>> contextStack;

	public TypingContext() {
		contextStack = new ArrayDeque<>();
		contextStack.push(new HashMap<>());
	}

	public TypingContext(Map<String, ContextType> symTable) {
		contextStack = new ArrayDeque<>();
		contextStack.push(symTable);
	}

	/** Pushes new map onto context stack */
	public void enterScope() {
		contextStack.push(new HashMap<>());
	}

	/** Pops map off context stack */
	public void leaveScope() {
		contextStack.pop();
	}

	private ContextType getContextType(String id) {
		Iterator<Map<String, ContextType>> stackIterator = contextStack
				.iterator();
		while (stackIterator.hasNext()) {
			Map<String, ContextType> contextMap = stackIterator.next();
			if (contextMap.containsKey(id))
				return contextMap.get(id);
		}
		return null;
	}
	
	
	/** Returns whether a label has a binding in the context stack.
	 * 
	 * @param s  the label to check
	 * @return   true if binding is found, false otherwise
	 */
	public boolean isDefined(String s) {
		ContextType type = getContextType(s);
		return type != null;
	}

	/**
	 * Returns the type of passed identifier. Returns null if identifier is not
	 * in context
	 * 
	 * @param id var name
	 * @returns type if found, null otherwise
	 */
	public Type getIdType(String id) {
		ContextType type = getContextType(id);
		if (type != null)
			return type.getType();
		else return null;
	}

	/**
	 * Returns whether or not passed identifier is tied to a function
	 * declaration. Returns false if mapped to non-function or not found.
	 * 
	 * @param id function name
	 * @return true if function, false if not or undefined
	 */
	public boolean isFunctionDecl(String id) {
		ContextType type = getContextType(id);
		return type != null && type.isFunctionDecl();
	}
	
	/**
	 * Returns list of function argument types.
	 * 
	 * @param id function name
	 * @return list of arg types if function, null otherwise
	 */
	public List<Type> getArgTypes(String id) {
		ContextType type = getContextType(id);
		if (type != null)
			return type.getArgTypes();
		else return null;
	}

	/**
	 * Returns list of function return types given function name.
	 * 
	 * @param id function name
	 * @return list of return types if function, null otherwise
	 */
	public List<Type> getReturnTypes(String id) {
		ContextType type = getContextType(id);
		if (type != null)
			return type.getReturnTypes();
		else return null;
	}
	
	
	/** Returns whether or a not a function returns unit.
	 *  Assumes f is a defined function label.
	 *  
	 * @param f  the name of the function
	 * @return   true if function returns unit, false if not or undefined
	 */
	public boolean returnsUnit(String f) {
		List<Type> returnTypes = getReturnTypes(f);
		if(returnTypes == null) return false;
		else return returnTypes.isEmpty();
	}
	
	/** Returns whether or a not a function takes in unit as
	 *  its args. Assumes f is a defined function label.
	 *  
	 * @param f  the name of the function
	 * @return   true if function takes in unit, false if not or undefined
	 */
	public boolean takesInUnit(String f) {
		List<Type> argTypes = getArgTypes(f);
		if(argTypes == null) return false;
		else return argTypes.isEmpty();
	}
	
	
	/** Adds an identifier to type binding to current context. 
	 * 
	 * @param id the name of the variable to be added
	 * @param type the type bounded to the var
	 */
	public void addIdBinding(String id, Type type) {
		ContextType contextType = new ContextType(type);
		contextStack.peek().put(id, contextType);
	}

	/**
	 * Adds the passed list of simple decls to the current context. A new scope
	 * should be entered prior to calling this method. For a procedure, empty
	 * lists should be passed.
	 * 
	 * @param args the simple declarations to be added to the context
	 */
	public void addFuncBindings(List<SimpleDecl> args, List<Type> returnTypes) {
		for (SimpleDecl decl : args)
			contextStack.peek().put(decl.getId(),
					new ContextType(decl.getType()));
		contextStack.peek().put(RHO, new ContextType(returnTypes));
	}

	/**
	 * Adds function binding to current context
	 * 
	 * @param id          function name
	 * @param args        list of argument decls
	 * @param returnTypes list of return types
	 */
	public void addFuncDecl(String id, List<SimpleDecl> args, List<Type> returnTypes) {
		ContextType type = new ContextType(args, returnTypes);
		contextStack.peek().put(id, type);
	}

	/**
	 * Returns list of function return types assuming scope is function body.
	 * 
	 * @return list of return types
	 */
	public List<Type> getReturnTypeInScope() {
		return contextStack.peek().get(RHO).getReturnTypes();
	}

}
