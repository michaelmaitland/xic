package mtm68.assem.visit;

import java.nio.file.Path;

import mtm68.assem.Assem;
import mtm68.assem.LabelAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.op.AddAssem;
import mtm68.assem.operand.RealReg;

public class AssemToFileBuilder {
	
	public static void main(String[] args) {
		SeqAssem seq = new SeqAssem(new LabelAssem("_Imain_paai"), new AddAssem(RealReg.RAX, RealReg.RSP));
		System.out.println(assemToFileString(seq));
	}
	
	public static String assemToFileString(Assem compAssem) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getDirectives());
		
		//TODO: Generate SeqAssem or list of assems from top-level comp assem
		SeqAssem seq = (SeqAssem) compAssem;	
		sb.append(getAssemBlock(seq));
		
		return sb.toString();
	}
	
	public static String getDirectives() {
		StringBuilder sb = new StringBuilder();
		sb.append(".intel_syntax noprefix" + "\n");
		sb.append(".text" + "\n");
		sb.append(".globl _Imain_paai" + "\n");
		sb.append(".type _Imain_paai, @function" + "\n");
		
		return sb.toString();
	}
	
	public static String getAssemBlock(SeqAssem seq) {
		StringBuilder sb = new StringBuilder();
		
		for(Assem assem : seq.getAssems()) {
			//if(!(assem instanceof LabelAssem)) sb.append("\t");
			sb.append(assem.toString() + "\n");
		}
		
		return sb.toString();
	}
}
