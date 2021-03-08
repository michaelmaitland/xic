package mtm68.ast.types;

public class ArrayType implements Type {

	private Type type;
	
	public ArrayType(Type type) {
		assert type != null;
		this.type = type;
	}

	@Override
	public TypeType getTypeType() {
		return TypeType.ARRAY;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString() + "[]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayType other = (ArrayType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String getPP() {
		return "([] " + type.getPP() + ")";
	}
}