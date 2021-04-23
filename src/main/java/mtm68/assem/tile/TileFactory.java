package mtm68.assem.tile;

import static mtm68.assem.pattern.Patterns.*;

import mtm68.assem.Assem;
import mtm68.assem.MoveAssem;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.Reg;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;
import static mtm68.assem.tile.TileCosts.*;

public class TileFactory {
	
	public static Tile constTile() {
		Pattern pattern = anyConstant("c");
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				return new MoveAssem(resultReg, results.get("c", Imm.class));
			}
		};
	}
	
	public static Tile memAddTile() {
		Pattern pattern = mem(add(var("t"), smallConstant("c")));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				Imm c = results.get("c", Imm.class);
				return new MoveAssem(resultReg, new Mem(t, c));
			}
		};
	}

	public static Tile memBasic() {
		Pattern pattern = mem(var("t"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				return new MoveAssem(resultReg, new Mem(t));
			}
		};
	}
	
	public static Tile moveMemBaseAndIndex() {
		Pattern pattern = move(
				mem(add(var("t1"), mul(index("i"), var("t2")))), anyConstant("c"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				Imm i = results.get("i", Imm.class);
				Imm c = results.get("c", Imm.class);

				return new MoveAssem(new Mem(t1, t2, i), c);
			}
		};
	}
}
