package mtm68.ast.types;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

public class DeclType { 
	private Type type;
	private List<Expr> indices;
	
	public DeclType(Type type, List<Expr> indices, int numEmptyBrackets) {
		this(Types.addArrayDims(type, indices.size() + numEmptyBrackets), indices);
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
	
	public String getTypePP() {		
		return addIndicesToPP(type.getPP(), indices, 0);
	}
	
	
	/**
	 * Returns typePP with all elements of indices properly placed in their brackets
	 * 
	 * I.e. typePP = "([] ([] int)"; indices = [3, 4] => "([] ([] int 4) 3)"
	 * 
	 * @param typePP               the pretty print of the type
	 * @param indices              list of arr indices
	 * @param n                    counter (should be initialized to 0)
	 * @return		               typePP properly indicized
	 */
	public static String addIndicesToPP(String typePP, List<Expr> indices, int n) {
		if(n == indices.size()) {
			return typePP;
		}
		
		StringWriter indexOut = new StringWriter();
		PrintWriter writer = new PrintWriter(indexOut);
		SExpPrinter indexPrinter = new CodeWriterSExpPrinter(writer);

		String outerBracket = typePP.substring(0, 4);
		String nestedType = typePP.substring(4, typePP.length()-1);
		
		indices.get(n).prettyPrint(indexPrinter);
		indexPrinter.flush();
		String indexPP = indexOut.toString();
		
		writer.close();
		
		return  outerBracket + addIndicesToPP(nestedType, indices, n+1) + " " + indexPP + ")";
	}
}
