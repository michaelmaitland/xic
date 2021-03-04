package mtm68.ast.types;

public class IntType implements Type {

	@Override
	public TypeType getTypeType() {
		return TypeType.INT;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntType;
	}
}
