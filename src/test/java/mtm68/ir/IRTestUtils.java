package mtm68.ir;

import static org.junit.jupiter.api.Assertions.*;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;

public class IRTestUtils {
	
	public static void assertCanonical(IRNode node) {
		CheckCanonicalIRVisitor v = new CheckCanonicalIRVisitor();
		assertTrue(v.visit(node), "Node " + node + " is not in canonical form");
	}

	public static IRMove move(String temp, IRExpr expr) {
		return new IRMove(temp(temp), expr);
	}

	public static IRMove move(String t1, String t2) {
		return move(t1, temp(t2));
	}
	
	public static IRLabel label(String name) {
		return new IRLabel(name);
	}
	
	public static IRConst constant(long value) {
		return new IRConst(value);
	}

	public static IRJump jump(String label) {
		return new IRJump(new IRName(label));
	}

	public static IRCJump cjump(IRExpr cond, String trueLabel, String falseLabel) {
		return new IRCJump(cond, trueLabel, falseLabel);
	}

	public static IRCJump cjump(String trueLabel, String falseLabel) {
		return cjump(constant(0L), trueLabel, falseLabel);
	}
	
	public static IRReturn ret() {
		return new IRReturn();
	}
	
	public static IRTemp temp(String name) {
		return new IRTemp(name);
	}

	public static IRCallStmt call(String name) {
		return new IRCallStmt(new IRName(name));
	}
	
	public static IRBinOp op(OpType type, IRExpr left, IRExpr right) {
		return new IRBinOp(type, left, right);
	}
}
