package mtm68.assem.operand;

public class Loc {
	private String name;

	public Loc(String name) {
		this.name = name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
