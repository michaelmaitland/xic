package mtm68.assem;

import java.util.List;

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
	public List<ReplaceableReg> getReplaceableRegs() {
		return assem.getReplaceableRegs();
	}

	@Override
	public String toString() {
		return assem.toString();
	}
}
