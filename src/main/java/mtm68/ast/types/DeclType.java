package mtm68.ast.types;

import java.util.ArrayList;
import java.util.List;

import mtm68.ast.nodes.Expr;

public class DeclType {
	
	private Type type;
	private List<Expr> indices;
	
	public DeclType(Type type) {
		this(type, new ArrayList<Expr>());
	}

	public DeclType(Type type, List<Expr> indices) {
		this.type = type;
		this.indices = indices;
	}
	
	public boolean canAddMoreIndices() {
		return typeDepth(type) == indices.size();
	}
	
	public void addArrayLayer() {
		type = Types.ARRAY(type);
	}
	
	private int typeDepth(Type type) {
		switch(type.getTypeType()) {
		case ARRAY: return 1 + typeDepth(((ArrayType)type).getType());
		case BOOL: return 0;
		case INT: return 0;
		}
		return 0;
	}
	
	public void addIndexExpr(Expr expr) {
		indices.add(expr);
	}
	
	public Type getType() {
		return type;
	}
	
	public List<Expr> getIndices() {
		return indices;
	}

	@Override
	public String toString() {
		return "DeclType [type=" + type + ", indices=" + indices + "]";
	}

}
