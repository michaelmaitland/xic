package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;

public class Patterns {
	
	public static BinopPattern op(OpType opType, Pattern left, Pattern right) {
		return new BinopPattern(opType, left, right); 
	}
	
	public static VarPattern var() {
		return new VarPattern();
	}
	
	public static ConstantPattern constant(long value) {
		return new ConstantPattern(value);
	}

	public static ConstantPattern anyConstant() {
		return new ConstantPattern();
	}

	public static MemPattern mem(Pattern inner) {
		return new MemPattern(inner);
	}

	public static MovePattern move(Pattern dest, Pattern src) {
		return new MovePattern(dest, src);
	}
	
	public static IndexPattern index() {
		return new IndexPattern();
	}

}
