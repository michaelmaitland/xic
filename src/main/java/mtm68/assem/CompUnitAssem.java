package mtm68.assem;

import java.util.List;
import java.util.stream.Collectors;

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
	public List<ReplaceableReg> getReplaceableRegs() {
		return functions.stream()
			.map(FuncDefnAssem::getReplaceableRegs)
			.flatMap(List::stream)
			.collect(Collectors.toList());
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
