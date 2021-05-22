package mtm68.assem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mtm68.assem.ReplaceableReg.RegType;
import mtm68.assem.operand.Reg;
import mtm68.util.ArrayUtils;

public abstract class Assem implements Cloneable, HasReplaceableRegs {
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
	
	public Set<Reg> def() {
		return defReplaceable().stream()
				.map(ReplaceableReg::getInitialReg)
				.collect(Collectors.toSet());
	}

	public Set<Reg> use() {
		return useReplaceable().stream()
				.map(ReplaceableReg::getInitialReg)
				.collect(Collectors.toSet());
	}

	public Set<ReplaceableReg> defReplaceable() {
		return getReplaceableRegs().stream()
			.filter(r -> r.getRegType() == RegType.WRITE)
			.collect(Collectors.toSet());
	}

	public Set<ReplaceableReg> useReplaceable() {
		return getReplaceableRegs().stream()
			.filter(r -> r.getRegType() == RegType.READ)
			.collect(Collectors.toSet());
	}
}
