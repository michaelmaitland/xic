package mtm68.assem.operand;

public abstract class Reg implements Ref, Acc, Src, Dest {
	protected String id;

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
