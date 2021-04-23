package mtm68.assem.tile;

import static mtm68.assem.pattern.Patterns.*;
import static mtm68.assem.tile.TileCosts.*;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRReturn;
import mtm68.assem.Assem;
import mtm68.assem.MoveAssem;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.Reg;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;

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
	
	public static Tile moveBasic() {
		Pattern pattern = move(var("t1"), var("t2"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new MoveAssem(t1, t2);
			}
		};
	}

	public static Tile moveFromMem() {
		Pattern pattern = move(var("t1"), mem(var("t2")));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new MoveAssem(t1, new Mem(t2));
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
	
	public static Tile returnBasic() {
		Pattern pattern = ret(); 
		
		return new ReturnTile(pattern, RETURN_COST);
	}
}
