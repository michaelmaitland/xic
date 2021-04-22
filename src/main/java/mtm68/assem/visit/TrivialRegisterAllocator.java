package mtm68.assem.visit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.assem.Assem;
import mtm68.assem.CompUnitAssem;
import mtm68.assem.FuncDefnAssem;
import mtm68.assem.OneOpAssem;
import mtm68.assem.ThreeOpAssem;
import mtm68.assem.TwoOpAssem;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class TrivialRegisterAllocator {
	
	private static final List<RealReg> SHUTTLE_REGS = RealReg.getCalleeSaveRegIds();
	
	/**
	 * Does register allocation for each function in the CompUnitAssem. Each
	 * FuncDefAssem can be a mix of abstract or real registers. The list returned
	 * contains the assembly for all the functions where all Regs are RealRegs.
	 */
	public  List<Assem> allocate(CompUnitAssem assem) {
		List<Assem> allAssems = ArrayUtils.empty();

		Map<String, FuncDefnAssem> funcs = assem.getFunctions();
		for(FuncDefnAssem func : funcs.values()) {
			List<Assem> funcAssems = allocateForFunc(func);
			allAssems.addAll(funcAssems);
		}
		
		return allAssems;
	}

	private List<Assem> allocateForFunc(FuncDefnAssem func) {
		List<Assem> insts = func.getAssem().getAssems();
		Map<String, Integer> regsToLoc = assignAbstrRegsToStackLocations(insts);
		return assignAbstrRegsToRealRegs(insts, regsToLoc);
	}

	private Map<String, Integer> assignAbstrRegsToStackLocations(List<Assem> insts) {
		// TODO: consider sorting by temp name
		Set<String> abstrRegIds = getAbstractRegIds(insts);
		return getAbstrRegsToStackLocs(abstrRegIds);
	}
	
	private Set<String> getAbstractRegIds(List<Assem> insts) {
		Set<String> abstrRegIds = new HashSet<>();
		for(Assem inst : insts) {
			abstrRegIds.addAll(inst.getAbstractRegs()
								 .stream()
								 .map(AbstractReg::getId)
								 .collect(Collectors.toList())
			);
		}
		return abstrRegIds;
	}
	
	private Map<String, Integer> getAbstrRegsToStackLocs(Set<String> abstrRegIds) {
		Map<String, Integer> abstrRegsToStackLocs = new HashMap<>();
		int size = 0;
		
		// stack location = [rbp - 8 * l]
		for(String abstrRegId : abstrRegIds) {
			abstrRegsToStackLocs.put(abstrRegId, size * 8);
			size++;
		}
		
		return abstrRegsToStackLocs;
	}

	private List<Assem> assignAbstrRegsToRealRegs(List<Assem> insts, Map<String, Integer> regsToLoc) {
		List<Assem> realAssem = ArrayUtils.empty();
		for(Assem inst : insts) {
			if(inst instanceof OneOpAssem) {
				realAssem.add(assignAbstRegsToRealRegs((OneOpAssem)inst));

			} else if (inst instanceof TwoOpAssem) {
				realAssem.add(assignAbstRegsToRealRegs((TwoOpAssem)inst));

			} else if (inst instanceof ThreeOpAssem) {
				realAssem.add(assignAbstRegsToRealRegs((ThreeOpAssem)inst));

			} else {
				throw new InternalCompilerError("Unknown Assem type: " + inst);
			}
		}
		return realAssem;
	}

	private Assem assignAbstRegsToRealRegs(OneOpAssem inst) {
		if(inst.getReg() instanceof AbstractReg) {
			RealReg repl = SHUTTLE_REGS.get(0);
			OneOpAssem copy = inst.copy();
			copy.setReg(repl);
			return copy;
		}
		return inst;
	}

	private Assem assignAbstRegsToRealRegs(TwoOpAssem inst) {
		
		List<RealReg> availRegs = SHUTTLE_REGS;

		Dest newDest = inst.getDest();
		if(inst.getDest() instanceof AbstractReg) {
			newDest = availRegs.get(0);
		} else {
			// its a real register, and we don't want
			// to accidently set src as the same reg
			availRegs.remove(newDest);
		}
		
		Src newSrc = inst.getSrc();
		if(inst.getSrc() instanceof AbstractReg) {
			newSrc = availRegs.get(1);
		}
		
		if(newDest != inst.getDest() || newSrc != inst.getSrc()) {
			TwoOpAssem copy = inst.copy();
			copy.setDest(newDest);
			copy.setSrc(newSrc);
			return copy;	
		}
		
		return inst;
	}

	private Assem assignAbstRegsToRealRegs(ThreeOpAssem inst) {
		return null;
	}
}
