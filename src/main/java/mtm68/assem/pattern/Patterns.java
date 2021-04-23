package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;

public class Patterns {
	
	public static BinopPattern op(OpType opType, Pattern left, Pattern right) {
		return new BinopPattern(opType, left, right); 
	}

	public static BinopPattern add(Pattern left, Pattern right) {
		return new BinopPattern(OpType.ADD, left, right); 
	}

	public static BinopPattern mul(Pattern left, Pattern right) {
		return new BinopPattern(OpType.MUL, left, right); 
	}
	
	public static VarPattern var(String name) {
		return new VarPattern(name);
	}

	public static ConstantPattern anyConstant(String name) {
		return new AnyConstantPattern(name);
	}

	public static ConstantPattern smallConstant(String name) {
		return new SmallConstantPattern(name);
	}

	public static MemPattern mem(Pattern inner) {
		return new MemPattern(inner);
	}

	public static MovePattern move(Pattern dest, Pattern src) {
		return new MovePattern(dest, src);
	}
	
	public static IndexPattern index(String name) {
		return new IndexPattern(name);
	}
	
	public static ReturnPattern ret() {
		return new ReturnPattern();
	}

}
