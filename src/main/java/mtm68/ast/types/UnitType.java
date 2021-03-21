package mtm68.ast.types;

import mtm68.util.ArrayUtils;

public class UnitType extends TypeVector {
	
	public UnitType() {
		super(ArrayUtils.empty());
	}
	
	@Override
	public String toString() {
		return "unit";
	}
}
