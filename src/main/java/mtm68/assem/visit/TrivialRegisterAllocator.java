package mtm68.assem.visit;

import java.util.Arrays;
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
import mtm68.assem.MoveAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import static mtm68.assem.operand.RealReg.*;
import mtm68.util.ArrayUtils;

public class TrivialRegisterAllocator {
	
	private static final List<RealReg> SHUTTLE_REGS = Arrays.asList(R9, R10, R11);
	
	/**
	 * Does register allocation for each function in the CompUnitAssem. Each
	 * FuncDefAssem can be a mix of abstract or real registers. The list returned
	 * contains the assembly for all the functions where all Regs are RealRegs.
	 */
	public  List<Assem> allocate(CompUnitAssem assem) {
		List<Assem> allAssems = ArrayUtils.empty();

		List<FuncDefnAssem> funcs = assem.getFunctions();
		for(FuncDefnAssem func : funcs) {
			List<Assem> funcAssems = allocateForFunc(func);
			allAssems.addAll(funcAssems);
		}
		
		return allAssems;
	}

	private List<Assem> allocateForFunc(FuncDefnAssem func) {
		List<Assem> insts = func.getAssem().getAssems();
		Map<String, Mem> regsToLoc = assignAbstrRegsToStackLocations(insts);
		return assignAbstrRegsToRealRegs(insts, regsToLoc);
	}

	private Map<String, Mem> assignAbstrRegsToStackLocations(List<Assem> insts) {
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
	
	private Map<String, Mem> getAbstrRegsToStackLocs(Set<String> abstrRegIds) {
		Map<String, Mem> abstrRegsToStackLocs = new HashMap<>();
		int size = 0;
		
		// stack location = [rbp - 8 * l]
		for(String abstrRegId : abstrRegIds) {
			Mem mem = new Mem(RealReg.RBP, (size + 1) * - 8);
			abstrRegsToStackLocs.put(abstrRegId, mem);
			size++;
		}
		
		return abstrRegsToStackLocs;
	}

	private List<Assem> assignAbstrRegsToRealRegs(List<Assem> insts, Map<String, Mem> regsToLoc) {
		List<Assem> realAssem = ArrayUtils.empty();
		for(Assem inst : insts) {
			Assem newInst = assignAbstrRegsToRealRegs(inst, regsToLoc);
			realAssem.add(newInst);
		}
		return realAssem;
	}
	
	private Assem assignAbstrRegsToRealRegs(Assem inst, Map<String, Mem> regsToLoc) {
		
		List<AbstractReg> abstrRegs = inst.getAbstractRegs();
		if(abstrRegs.size() > 3) {
			throw new InternalCompilerError("Instruction may have at most 3 registers");
		}
		
		List<Assem> seq = ArrayUtils.empty();

		int i = 0;
		for(AbstractReg reg : abstrRegs) {
			// Move from stack to shuttle
			Mem stackOffset = regsToLoc.get(reg.getId());
			RealReg shuttle = SHUTTLE_REGS.get(i);
			MoveAssem fromStack = new MoveAssem(shuttle, stackOffset);
			seq.add(fromStack);
			i++;
		}
		
		// inst uses shuttle
		Assem newInst = (Assem)inst.copyAndSetRealRegs(SHUTTLE_REGS.subList(0, i));
		seq.add(newInst);
		
		i = 0;
		for(AbstractReg reg : abstrRegs) {
			// Move from shuttle back to stack
			Mem stackOffset = regsToLoc.get(reg.getId());
			RealReg shuttle = SHUTTLE_REGS.get(i);
			MoveAssem toStack = new MoveAssem(stackOffset, shuttle);
			seq.add(toStack);
			i++;
		}

		return new SeqAssem(seq);
	}
}
