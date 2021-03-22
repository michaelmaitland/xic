package mtm68.ast.types;

import java.util.List;

import mtm68.util.ArrayUtils;

public class Types {
	
	public static final Type INT = new IntType();
	public static final Type BOOL = new BoolType();
	public static final Type UNIT = new UnitType();
	
	private Types() {}
	
	public static Type ARRAY(Type type) {
		return new ArrayType(type);
	}

	public static Type TVEC(Type...types) {
		return new TypeVector(types);
	}

	public static Type TVEC(List<Type> types) {
		if(types.size() == 0) return UNIT;
		if(types.size() == 1) return types.get(0);
		return new TypeVector(types);
	}
	
	public static List<Type> toList(Type type) {
		if(type.equals(UNIT)) return ArrayUtils.empty();
		if(!(type instanceof TypeVector)) return ArrayUtils.singleton(type);
		return ((TypeVector)type).getTypes();
	}

	public static Type addArrayDims(Type type, int numDimensions) {
		if(numDimensions == 0) return type;
		return addArrayDims(ARRAY(type), numDimensions - 1);
	}

}
