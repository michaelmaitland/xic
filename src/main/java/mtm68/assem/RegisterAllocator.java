package mtm68.assem;

public interface RegisterAllocator {

	/**
	 * Return a new program where all abstract registers have been replaced
	 * with hardware registers.
	 * 
	 * @param program
	 * @return
	 */
	CompUnitAssem allocateRegisters(CompUnitAssem program);

}
