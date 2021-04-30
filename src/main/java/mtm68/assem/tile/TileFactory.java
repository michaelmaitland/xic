package mtm68.assem.tile;

import static mtm68.assem.pattern.Patterns.*;
import static mtm68.assem.tile.TileCosts.*;

import java.util.List;
import java.util.function.BiFunction;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.Assem;
import mtm68.assem.CqoAssem;
import mtm68.assem.CmpAssem;
import mtm68.assem.IDivAssem;
import mtm68.assem.JumpAssem;
import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.MoveAssem;
import mtm68.assem.MulAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.SetccAssem;
import mtm68.assem.SetccAssem.CC;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.op.XorAssem;
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
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;

/**
 * Factory class for constructing tiles.
 * 
 * @author Scott
 */
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
	
	public static Tile memBasic() {
		Pattern pattern = mem("t");
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Mem t = results.get("t", Mem.class);
				return new MoveAssem(resultReg, t);
			}
		};
	}

	//--------------------------------------------------------------------------------
	// Move
	//--------------------------------------------------------------------------------
	
	public static Tile moveBasic() {
		Pattern pattern = move(temp("t1"), var("t2"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new MoveAssem(t1, t2);
			}
		};
	}
	
	public static Tile moveConst() {
		Pattern pattern = move(temp("t"), anyConstant("c"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				Imm c = results.get("c", Imm.class);
				return new MoveAssem(t, c);
			}
		};
	}
	
	public static Tile moveConstIntoMem() {
		Pattern pattern = move(mem("m"), anyConstant("c"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Mem m = results.get("m", Mem.class);
				Imm c = results.get("c", Imm.class);
				return new MoveAssem(m, c);
			}
		};
	}

	public static Tile moveFromMem() {
		Pattern pattern = move(temp("t1"), mem("m"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Mem m = results.get("m", Mem.class);
				return new MoveAssem(t1, m);
			}
		};
	}
	
	public static Tile moveIntoMem() {
		Pattern pattern = move(mem("m"), var("t"));
		
		return new Tile(pattern, MOVE_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Mem m = results.get("m", Mem.class);
				Reg t = results.get("t", Reg.class);
				return new MoveAssem(m, t);
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
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t = results.get("t", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t, new Imm(1L)),
						new JumpAssem(JumpType.JE, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpLessThan() {
		Pattern pattern = cjump(op(OpType.LT, var("t1"), var("t2"))); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new JumpAssem(JumpType.JL, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpLessThanEqual() {
		Pattern pattern = cjump(op(OpType.LEQ, var("t1"), var("t2"))); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new JumpAssem(JumpType.JLE, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpNotEqual() {
		Pattern pattern = cjump(op(OpType.NEQ, var("t1"), var("t2"))); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new JumpAssem(JumpType.JNE, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpGreaterThan() {
		Pattern pattern = cjump(op(OpType.GT, var("t1"), var("t2"))); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new JumpAssem(JumpType.JG, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpGreaterThanEqual() {
		Pattern pattern = cjump(op(OpType.GEQ, var("t1"), var("t2"))); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, t2),
						new JumpAssem(JumpType.JGE, new Loc(jumpLoc))
					);
			}
		};
	}
	
	public static Tile cjumpIfZero() {
		Pattern pattern = cjump(op(OpType.XOR, var("t1"), specificConstant("c", 1) )); 
		
		return new Tile(pattern, COMPARE_COST + JUMP_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				String jumpLoc = ((IRCJump)baseNode).trueLabel();
				return new SeqAssem(
						new CmpAssem(t1, new Imm(1)),
						new JumpAssem(JumpType.JZ, new Loc(jumpLoc))
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
	
	public static Tile binopDivOrMod(OpType divOrMod) {
		if(divOrMod != OpType.DIV && divOrMod != OpType.MOD) throw new IllegalArgumentException(divOrMod + " must be either DIV or MOD");

		Pattern pattern = op(divOrMod, var("t1"), var("t2"));
		
		return new Tile(pattern, 2 * MOVE_COST + DIV_COST + CDQ_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				
				Reg resultFrom = divOrMod == OpType.DIV ? RealReg.RAX : RealReg.RDX;

				return new SeqAssem(
						new MoveAssem(RealReg.RAX, t1),
						new CqoAssem(),
						new IDivAssem(t2),
						new MoveAssem(resultReg, resultFrom)
						);
			}
		};
	}

	public static Tile binopHighMul() {
		Pattern pattern = op(OpType.HMUL, var("t1"), var("t2"));
		
		return new Tile(pattern, 2 * MOVE_COST + MUL_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				
				return new SeqAssem(
						new MoveAssem(RealReg.RAX, t1),
						new MulAssem(t2),
						new MoveAssem(resultReg, RealReg.RDX)
						);
			}
		};
	}

	public static List<Tile> binopBasic(OpType opType, BiFunction<Dest, Src, Assem> assemConstructor) {
		return binopBasic(opType, assemConstructor, BINOP_COST); 
	}
	
	public static List<Tile> binopBasic(OpType opType, BiFunction<Dest, Src, Assem> assemConstructor, float assemCost) {
		Pattern regRegPattern = op(opType, var("t1"), var("t2"));
		Tile regRegTile = new Tile(regRegPattern, MOVE_COST + assemCost) {
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
		
		Pattern regConstPattern = op(opType, var("t1"), anyConstant("c"));
		Tile regConstTile = new Tile(regConstPattern, MOVE_COST + assemCost) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Imm c = results.get("c", Imm.class);
				return new SeqAssem(
						new MoveAssem(resultReg, t1),
						assemConstructor.apply(resultReg, c));
			}
		};
		
		Pattern regMemPattern = op(opType, var("t1"), mem("m"));
		Tile regMemTile = new Tile(regMemPattern, assemCost) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Mem m = results.get("m", Mem.class);
				return new SeqAssem(
						new MoveAssem(resultReg, t1),
						assemConstructor.apply(resultReg, m));
			}
		};
		
		return ArrayUtils.elems(regRegTile, regConstTile, regMemTile);
	}

	public static Tile binopCompareBasic(OpType opType, CC cc) {
		Pattern pattern = op(opType, var("t1"), var("t2"));
		
		return new Tile(pattern, BINOP_COST + MOVE_COST + SETCC_COST) {
			@Override
			public Assem getTiledAssem(Reg resultReg, PatternResults results) {
				Reg t1 = results.get("t1", Reg.class);
				Reg t2 = results.get("t2", Reg.class);
				return new SeqAssem(
						new XorAssem(RealReg.RAX, RealReg.RAX),
						new CmpAssem(t1, t2),
						new SetccAssem(cc),
						new MoveAssem(resultReg, RealReg.RAX));
			}
		};
	}
}
