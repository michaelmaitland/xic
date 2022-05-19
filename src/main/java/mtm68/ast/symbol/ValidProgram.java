package mtm68.ast.symbol;

import mtm68.ast.nodes.Program;

public class ValidProgram {
	
	private String programName;
	private Program program;
	private ProgramSymbols progSymbols;

	public ValidProgram(String programName, Program program, ProgramSymbols symTable) {
		this.programName = programName;
		this.program = program;
		this.progSymbols = symTable;
	}

	public String getProgramName() {
		return programName;
	}

	public Program getProgram() {
		return program;
	}

	public ProgramSymbols getProgSymbols() {
		return progSymbols;
	}
}
