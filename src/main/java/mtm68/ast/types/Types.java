package mtm68.ast.types;

import java.util.List;

import mtm68.ast.nodes.Expr;
import mtm68.util.ArrayUtils;

public class Types {
	
	public static final Type INT = new IntType();
	public static final Type BOOL = new BoolType();
	public static final Type UNIT = new UnitType();
	public static final Type EMPTY_ARRAY = new EmptyArrayType();
	
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
	
	public static boolean isArray(Type type) {
		return type instanceof ArrayType || type instanceof EmptyArrayType;
	}

	public static Type getLeastUpperBound(Type t1, Type t2) {
		if (t1.equals(EMPTY_ARRAY)) {
			return t2;
		} else {
			return t1;
		}
	}

	public static boolean isArrayOfType(Type actual, Type expected) {
		if(actual instanceof EmptyArrayType) return true;

		if(actual instanceof ArrayType) {
			ArrayType arrType = (ArrayType)actual;
			return arrType.getType() == expected;
		}
		
		return false;
	}
}
