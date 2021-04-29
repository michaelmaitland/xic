package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class CmpAssem extends OperAssem {

	public CmpAssem(Dest dest, Src src) {
		super("cmp", dest, src);
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.concat(
				ReplaceableReg.fromSrc(src, this::setSrc), 
				ReplaceableReg.fromSrc(dest, this::setDest));
	}
}
