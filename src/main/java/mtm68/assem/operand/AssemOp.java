package mtm68.assem.operand;

public class AssemOp implements Cloneable {

	@SuppressWarnings("unchecked")
	public <A extends AssemOp> A copy() {
		try {
			return (A) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
