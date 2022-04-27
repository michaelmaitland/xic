package mtm68.visit;

import java.util.List;
import java.util.Stack;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.symbol.SymbolTable;
import mtm68.ast.types.ObjectType;
import mtm68.ast.types.Type;
import mtm68.util.Debug;

public class ThisAugmenter extends Visitor {

	private Stack<ClassDefn> context;
	
	// TODO we should really be using the symbol table to determine
	// what is a field
	private SymbolTable symbolTable;
	
	public ThisAugmenter() {
		this(new Stack<>(), new SymbolTable());
	}

	public ThisAugmenter(SymbolTable symTable) {
		this(new Stack<>(), symTable);
	}

	public ThisAugmenter(Stack<ClassDefn> context, SymbolTable symbolTable) {
		this.context = context;
		this.symbolTable = symbolTable;
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
			context.push((ClassDefn) n);

		return this;
	}

	@Override
	public Node leave(Node parent, Node n) {
		Node newN = n.augmentWithThis(this);
		
		if (n instanceof ClassDefn) context.pop();

		return newN;
	}

	/**
	 * Whether or not var is a field that is defined in the 
	 * current context ClassDefn.
	 */
	public boolean isField(Var var) {
		if(context.isEmpty()) return false;
	
		// TODO: instead of this O(n) search, the symbol table 
		// should have fields lookup in O(1) time for each class
		ClassDefn defn = context.peek();
		List<SimpleDecl> fields = defn.getBody().getFields();
		for(SimpleDecl field : fields) {
			if(field.getId().equals(var.getId())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * The ObjectType corresponding to the current context ClassDefn.
	 */
	public Type getCurrentClassType() {
		if(context.isEmpty()) 
			throw new InternalCompilerError("No current class in ThisAugmenter context");
		else 
			return new ObjectType(context.peek().getId());
	}

}
