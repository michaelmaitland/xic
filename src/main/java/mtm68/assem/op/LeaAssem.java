package mtm68.assem.op;

import java.util.List;

import mtm68.assem.OperAssem;
import mtm68.assem.ReplaceableReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class LeaAssem extends OperAssem{
	
	public LeaAssem(Dest dest, Src src) {
		super("lea", dest, src);
	}

	@Override
	public String toString() {
		return "lea " + dest + ", " + src;
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.concat(
				ReplaceableReg.fromDest(dest, this::setDest), 
				ReplaceableReg.fromSrc(src, this::setSrc));
	}
}
