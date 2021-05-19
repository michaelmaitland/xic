package mtm68.assem.operand;

import mtm68.assem.pattern.PatternMatch;
import mtm68.util.Constants;

public abstract class Reg extends AssemOp implements Ref, Acc, Src, Dest, PatternMatch {

	protected String id;

	public String getId() {
		return id;
	}
	
	public boolean isResultReg() {
		return id.startsWith(Constants.RET_PREFIX);
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Reg other = (Reg) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
