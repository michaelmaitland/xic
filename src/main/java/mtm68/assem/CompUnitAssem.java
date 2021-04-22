package mtm68.assem;

import java.util.List;
import java.util.Map;

import mtm68.assem.operand.AbstractReg;
import mtm68.util.ArrayUtils;

public class CompUnitAssem extends Assem {

	private final String name;
	
	private Map<String, FuncDefnAssem> functions;
	
	public CompUnitAssem(String name, Map<String, FuncDefnAssem> functions) {
		this.name = name;
		this.functions = functions;
	}

	public Map<String, FuncDefnAssem> getFunctions() {
		return functions;
	}

	public void setFunctions(Map<String, FuncDefnAssem> functions) {
		this.functions = functions;
	}

	public String getName() {
		return name;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> regs = ArrayUtils.empty();
		for(FuncDefnAssem func : functions.values()) {
			regs.addAll(func.getAbstractRegs());
		}
		
		return regs;
	}
}
