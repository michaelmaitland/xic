package mtm68.assem.visit;

import java.util.List;

import mtm68.assem.Assem;

public class AssemToFileBuilder {
	
	public static String assemToFileString(String filename, List<Assem> assems) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getDirectives(filename));
		
		for(Assem assem : assems) {
			sb.append(assem + "\n");
		}
		
		return sb.toString();
	}
	
	public static String getDirectives(String filename) {
		StringBuilder sb = new StringBuilder();
		sb.append(".file \"" + filename + "\"\n");
		sb.append(".intel_syntax noprefix" + "\n");
		sb.append(".text" + "\n");
		sb.append(".globl _Imain_paai" + "\n");
		sb.append(".type _Imain_paai, @function" + "\n");
		
		return sb.toString();
	}
}
