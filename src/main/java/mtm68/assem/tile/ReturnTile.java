package mtm68.assem.tile;

import java.util.List;

import mtm68.assem.Assem;
import mtm68.assem.MoveAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.FreshRegGenerator;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;

public class ReturnTile extends Tile {

	public ReturnTile(Pattern pattern, float cost) {
		super(pattern, cost);
	}

	@Override
	public Assem getTiledAssem(Reg resultReg, PatternResults results) {
		List<Assem> assems = ArrayUtils.empty();
		
		Reg retSpaceReg = null; 

		for(int i = 0; ; i++) {
			String name = "ret" + i;
			if(!results.containsKey(name)) break; 
			
			Reg src = results.get(name, Reg.class);
			
			if(i >= 2 && retSpaceReg == null) {
				retSpaceReg = FreshRegGenerator.getFreshAbstractReg();

				MoveAssem move = new MoveAssem(retSpaceReg, new Mem(RealReg.RBP, tiler.getRetSpaceOff())); 
				assems.add(move);
			}
			
			Dest dest = getDestForReturn(retSpaceReg, i);
			
			assems.add(new MoveAssem(dest, src));
		}

		assems.add(new RetAssem());

		return new SeqAssem(assems);
	}
	
	private Dest getDestForReturn(Reg retSpaceReg, int returnIdx) {
		if(returnIdx == 0) return RealReg.RAX;
		if(returnIdx == 1) return RealReg.RDX;
		
		return new Mem(retSpaceReg, -Constants.WORD_SIZE * (returnIdx - 2));
	}

}
