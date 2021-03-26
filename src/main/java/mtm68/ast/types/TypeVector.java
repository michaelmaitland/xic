package mtm68.ast.types;

import java.util.List;
import java.util.stream.Collectors;

import mtm68.util.ArrayUtils;

public class TypeVector implements Type {
	
	private List<Type> types;
	
	public TypeVector(Type...types) {
		this.types = ArrayUtils.elems(types);
	}

	public TypeVector(List<Type> types) {
		this.types = types;
	}
	
	public List<Type> getTypes() {
		return types;
	}

	@Override
	public TypeType getTypeType() {
		return null;
	}

	@Override
	public String getPP() {
		String typeStrings = types.stream()
				.map(Type::getPP)
				.collect(Collectors.joining(","));
		return "(" + typeStrings + ")";
	}
	
	@Override
	public String toString() {
		String typeStrings = types.stream()
				.map(Object::toString)
				.collect(Collectors.joining(","));
		return "(" + typeStrings + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((types == null) ? 0 : types.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeVector other = (TypeVector) obj;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}
}