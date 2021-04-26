package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class MulAssem extends Assem {
	
	private Src src;

	public MulAssem(Src src) {
		super();
		this.src = src;
	}
	
	@Override
	public String toString() {
		return "mul " + src;
	}
	
	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		Src newSrc = (Src)src.copyAndSetRealRegs(toSet);

		IDivAssem newIDiv= copy();
		newIDiv.setSrc(newSrc);
		return newIDiv;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		if(src instanceof AbstractReg) {
			return ArrayUtils.singleton((AbstractReg)src);
		} else {
			return ArrayUtils.empty();
		}
	}

}
