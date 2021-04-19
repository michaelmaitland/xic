package mtm68.assem.operand;

public class Imm implements Ref, Src {
	
	private int value;

	public Imm(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
}
