package mtm68.ast.types;

public class EmptyArrayType implements Type {

	@Override
	public TypeType getTypeType() {
		throw new RuntimeException("dont know what type this is");
	}

	@Override
	public String getPP() {
		return "T[]";
	}

	@Override
	public String toString() {
		return "T[]";
	}
}
