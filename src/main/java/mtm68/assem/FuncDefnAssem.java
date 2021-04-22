package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;

public class FuncDefnAssem extends Assem {
	private String name;
	private SeqAssem assem;

	public FuncDefnAssem(String name, SeqAssem assem) {
		super();
		this.name = name;
		this.assem = assem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SeqAssem getAssem() {
		return assem;
	}

	public void setAssem(SeqAssem assem) {
		this.assem = assem;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return assem.getAbstractRegs();
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		SeqAssem newAssem = (SeqAssem)assem.copyAndSetRealRegs(toSet);
		return new FuncDefnAssem(name, newAssem);
	}
}
