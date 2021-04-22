package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;

public abstract class Assem implements Cloneable, HasRegs {
	private String assem;
	
	@SuppressWarnings("unchecked")
	public <A extends Assem> A copy() {
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
