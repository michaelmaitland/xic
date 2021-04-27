package mtm68.assem;

import java.util.List;

import mtm68.util.ArrayUtils;

public abstract class Assem implements Cloneable, HasReplaceableRegs {
	private String assem;

	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.empty();
	}
	
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
