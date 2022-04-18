package mtm68.ast.symbol;

import mtm68.ast.nodes.Program;

public class ValidProgram {
	
	String programName;
	Program program;
	ProgSymbols progSymbols;

	public ValidProgram(String programName, Program program, SymbolTable symTable) {
		this.programName = programName;
		this.program = program;
		this.progSymbols = symTable.toProgSymbols();
	}

	public String getProgramName() {
		return programName;
	}

	public Program getProgram() {
		return program;
	}

	public ProgSymbols getProgSymbols() {
		return progSymbols;
	}
}
