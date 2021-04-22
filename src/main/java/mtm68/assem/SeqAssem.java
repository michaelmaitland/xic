package mtm68.assem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class SeqAssem extends Assem {

	private List<Assem> assems;

	public SeqAssem(List<Assem> assems) {
		this.assems = flattenSeqs(assems);
	}
	
	public SeqAssem(Assem...assems) {
		this(ArrayUtils.elems(assems));
	}
	
	private List<Assem> flattenSeqs(List<Assem> assems) {
		List<Assem> result = new ArrayList<>();
		for(Assem assem : assems) {
			if(assem instanceof SeqAssem) {
				result.addAll(flattenSeqs(((SeqAssem)assem).getAssems()));
			}
			else if (assem != null) {
				result.add(assem);
			}
		}
		return result;
	}
	
	public List<Assem> getAssems() {
		return assems;
	}

	public void appendAssems(List<Assem> assems) {
		assems.addAll(assems);
	}
	
	@Override
	public String toString() {
		return assems.stream()
				.map(Assem::toString)
				.collect(Collectors.joining("\n"));
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return assems.stream()
					 .map(Assem::getAbstractRegs)
					 .flatMap(List::stream)
					 .collect(Collectors.toList());
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		List<Assem> newAssems = ArrayUtils.empty();

		int numSet = 0;
		for(Assem assem : assems) {
			int numToSet = assem.getAbstractRegs().size();
			Assem newAssem = (Assem)assem.copyAndSetRealRegs(toSet.subList(numSet, numToSet));
			newAssems.add(newAssem);
			numSet += numToSet;
		}
		
		return new SeqAssem(newAssems);
	}
}
