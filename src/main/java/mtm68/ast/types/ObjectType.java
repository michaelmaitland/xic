package mtm68.ast.types;

public class ObjectType implements Type {

	private String name;
	
	public ObjectType(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}

	@Override
	public TypeType getTypeType() {
		return TypeType.OBJECT;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ObjectType;
	}
	
	@Override
	public String toString() {
		return "object " + name;
	}

	@Override
	public String getPP() {
		return this.toString();
	}
}
