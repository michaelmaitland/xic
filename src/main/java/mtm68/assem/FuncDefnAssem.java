package mtm68.assem;

import java.util.List;

import edu.cornell.cs.cs4120.ir.visit.Tiler;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class FuncDefnAssem extends Assem {
	private String name;
	private int numArgs;
	private SeqAssem bodyAssem;
	
	private int numSpilledTemps;
	private List<RealReg> calleeRegs;

	public FuncDefnAssem(String name, int numArgs, SeqAssem bodyAssem) {
		super();
		this.name = name;
		this.numArgs = numArgs;
		this.bodyAssem = bodyAssem;
		
		this.numSpilledTemps = 0;
		this.calleeRegs = ArrayUtils.empty();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SeqAssem getBodyAssem() {
		return bodyAssem;
	}
	
	public SeqAssem getFlattenedAssem() {
		SeqAssem prologue = Tiler.getPrologue(name, numArgs, numSpilledTemps, calleeRegs);
		
		List<Assem> bodyAssems = bodyAssem.getAssems(); 
		List<Assem> result = ArrayUtils.empty();
		
		for(Assem assem : bodyAssems) {
			if(assem instanceof RetAssem) {
				SeqAssem epilogue = Tiler.getEpilogue(calleeRegs);
				result.add(epilogue);
			}
			
			result.add(assem);
		}
		
		SeqAssem body = new SeqAssem(result);
		
		return new SeqAssem(prologue, body);
	}

	public void setBodyAssem(SeqAssem assem) {
		this.bodyAssem = assem;
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return bodyAssem.getReplaceableRegs();
	}
	
	public void setCalleeRegs(List<RealReg> calleeRegs) {
		this.calleeRegs = calleeRegs;
	}
	
	public void setNumSpilledTemps(int numSpilledTemps) {
		this.numSpilledTemps = numSpilledTemps;
	}
	
	public int getNumArgs() {
		return numArgs;
	}

	@Override
	public String toString() {
		return bodyAssem.toString();
	}
}
