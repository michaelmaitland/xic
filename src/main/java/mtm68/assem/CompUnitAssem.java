package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class CompUnitAssem extends Assem {

	private final String name;
	
	private List<FuncDefnAssem> functions;
	
	public CompUnitAssem(String name, List<FuncDefnAssem> functions) {
		this.name = name;
		this.functions = functions;
	}

	public List<FuncDefnAssem> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FuncDefnAssem> functions) {
		this.functions = functions;
	}

	public String getName() {
		return name;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> regs = ArrayUtils.empty();
		for(FuncDefnAssem func : functions) {
			regs.addAll(func.getAbstractRegs());
		}
		
		return regs;
	}
	
	@Override
	public List<AbstractReg> getMutatedAbstractRegs() {
		List<AbstractReg> regs = ArrayUtils.empty();
		for(FuncDefnAssem func : functions) {
			regs.addAll(func.getMutatedAbstractRegs());
		}
		
		return regs;
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		List<FuncDefnAssem> newFuncs = ArrayUtils.empty();

		int numSet = 0;
		for(FuncDefnAssem func : functions) {
			int numToSet = func.getAbstractRegs().size();
			FuncDefnAssem newFunc = (FuncDefnAssem)func.copyAndSetRealRegs(toSet.subList(numSet, numSet + numToSet));
			newFuncs.add(newFunc);
			numSet += numToSet;
		}
		
		return new CompUnitAssem(name, newFuncs);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(FuncDefnAssem fDefn : functions) {
			sb.append(fDefn.toString() + "\n");
		}
		return sb.toString();
	}
}
