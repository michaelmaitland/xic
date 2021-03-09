package mtm68.ast.types;

public class Types {
	
	public static final Type INT = new IntType();
	public static final Type BOOL = new BoolType();
	
	private Types() {}
	
	public static Type ARRAY(Type type) {
		return new ArrayType(type);
	}

	public static Type addArrayDims(Type type, int numDimensions) {
		if(numDimensions == 0) return type;
		return addArrayDims(ARRAY(type), numDimensions - 1);
	}

}
