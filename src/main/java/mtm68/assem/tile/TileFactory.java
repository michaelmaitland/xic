package mtm68.assem.tile;

import static mtm68.assem.pattern.Patterns.*;
import static mtm68.assem.tile.TileCosts.*;

import java.util.List;
import java.util.function.BiFunction;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.Assem;
import mtm68.assem.CmpAssem;
import mtm68.assem.JEAssem;
import mtm68.assem.MoveAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.Setcc;
import mtm68.assem.Setcc.CC;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Loc;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.RealReg.RealRegId;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;
import mtm68.util.Constants;

public class TileFactory {
	
	//--------------------------------------------------------------------------------
	// Constant
	//--------------------------------------------------------------------------------
	
	public static Tile constTile() {
		Pattern pattern = anyConstant("c");
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				return new MoveAssem(resultReg, results.get("c", Imm.class));
			}
		};
	}

	//--------------------------------------------------------------------------------
	// Mem
	//--------------------------------------------------------------------------------
	
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

	//--------------------------------------------------------------------------------
	// Move
	//--------------------------------------------------------------------------------
	
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
	
	public static Tile moveArg() {
		Pattern pattern = move(var("t"), regex("arg", Constants.ARG_PREFIX + "[0-9]+"));

		return new Tile(pattern, NO_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				IRTemp argTemp = results.getExpr("arg");
				
				Integer argNum = Integer.parseInt(argTemp.name().replace(Constants.ARG_PREFIX, ""));
				List<RealReg> argRegs = RealRegId.getArgRegs();
				
				Src src = null;
				if(argNum < argRegs.size()) {
					src = argRegs.get(argNum);
				}
				else {
					int extra = argNum - argRegs.size() + 1;
					src = new Mem(RealReg.RBP, Constants.WORD_SIZE * (extra + 1));
				}
				
				return new MoveAssem(t, src);
			}
		};
	}

	//--------------------------------------------------------------------------------
	// Return
	//--------------------------------------------------------------------------------
	
	public static Tile returnBasic() {
		Pattern pattern = ret(); 
		
		return new ReturnTile(pattern, RETURN_COST);
	}

	//--------------------------------------------------------------------------------
	// CJump
	//--------------------------------------------------------------------------------

	public static Tile cjumpBasic() {
		Pattern pattern = cjump(var("t")); 
		
		// TODO: Assign cost properly
		return new Tile(pattern, 1.0f) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t, new Imm(1L)),
						new JEAssem(new Loc(jumpLoc))
					);
			}
		};
	}

	//--------------------------------------------------------------------------------
	// Binop
	//--------------------------------------------------------------------------------
	public static Tile addBasic() {
		Pattern pattern = add(var("t1"), var("t2"));
		
		return new Tile(pattern, BINOP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new LeaAssem(resultReg, new Mem(t1, t2));
			}
		};
	}

	public static Tile addConstant() {
		Pattern pattern = add(var("t1"), smallConstant("c"));
		
		return new Tile(pattern, BINOP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Imm c = results.get("c", Imm.class);
				return new LeaAssem(resultReg, new Mem(t1, c));
			}
		};
	}

	public static Tile binopBasic(OpType opType, BiFunction<Dest, Src, Assem> assemConstructor) {
		return binopBasic(opType, assemConstructor, BINOP_COST); 
	}
	
	public static Tile binopBasic(OpType opType, BiFunction<Dest, Src, Assem> assemConstructor, float assemCost) {
		Pattern pattern = op(opType, var("t1"), var("t2"));
		
		return new Tile(pattern, MOVE_COST + assemCost) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new SeqAssem(
						new MoveAssem(resultReg, t1),
						assemConstructor.apply(resultReg, t2)
						);
			}
		};
	}

	public static Tile binopCompareBasic(OpType opType, CC cc) {
		Pattern pattern = op(opType, var("t1"), var("t2"));
		
		return new Tile(pattern, BINOP_COST + MOVE_COST + SETCC_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new Setcc(cc),
						new MoveAssem(resultReg, RealReg.RAX));
			}
		};
	}
}
