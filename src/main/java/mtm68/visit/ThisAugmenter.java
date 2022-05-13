package mtm68.visit;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.Decl;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.While;
import mtm68.ast.types.ObjectType;
import mtm68.ast.types.Type;
import mtm68.util.ArrayUtils;
import mtm68.util.Debug;

public class ThisAugmenter extends Visitor {

	private Optional<ClassDefn> currentClass;
	
	private Stack<List<String>> varContext;
	
	public ThisAugmenter() {
		this.currentClass = Optional.empty(); 
	}

	public ThisAugmenter(ClassDefn currentClass){
		this.currentClass= Optional.of(currentClass);
	}
	
	public <N extends Node> N performAugment(N root) {
		try {
			return root.accept(this);
		} catch(InternalCompilerError e) {
			if(Debug.DEBUG_ON) {
				e.printStackTrace();
			}
		}
		return root;
	}

	@Override
	public Visitor enter(Node parent, Node n) {
		if (n instanceof ClassDefn)
			currentClass = Optional.of((ClassDefn) n);
		
		if(isScopeNode(n) || parent instanceof If) varContext.push(ArrayUtils.empty());

		return this;
	}

	@Override
	public Node leave(Node parent, Node n) {
		Node newN = n.augmentWithThis(this);
		
		if (n instanceof ClassDefn) currentClass = Optional.empty();

		if(isScopeNode(n) || parent instanceof If) varContext.pop();

		return newN;
	}
	
	private boolean isScopeNode(Node node) {
		return node instanceof Block
				|| node instanceof While
				|| node instanceof FunctionDefn;
	}
	
	/**
	 * Whether or not Var refers to a field that is defined in the 
	 * current context ClassDefn.
	 */
	public boolean isField(Var var) {
		
		// If its in scope from a decl then its not a field
		if(isInScope(var.getId())) return false;
		
		if(currentClass.isPresent()) return false;
	
		ClassDefn defn = currentClass.get();
		List<SimpleDecl> fields = defn.getBody().getFields();
		for(SimpleDecl field : fields) {
			if(field.getId().equals(var.getId())) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isInScope(String id) {
		Iterator<List<String>> stackIterator = varContext.iterator();
		while (stackIterator.hasNext()) {
			List<String> context = stackIterator.next();
			if (context.contains(id))
				return true;
		}
		return false;
	}
	
	/**
	 * The ObjectType corresponding to the current context ClassDefn.
	 */
	public Type getCurrentClassType() {
		if(!currentClass.isPresent()) 
			throw new InternalCompilerError("No current class in ThisAugmenter context");
		else 
			return new ObjectType(currentClass.get().getId());
	}

	public void addBinding(Decl decl) {
		varContext.peek().add(decl.getId());
	}
}
