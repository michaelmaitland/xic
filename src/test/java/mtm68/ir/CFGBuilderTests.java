package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.util.ArrayUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;

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
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.ir.cfg.CFGBuilder;

public class CFGBuilderTests {
	
	@Test
	void exampleFromClass() {
		List<IRStmt> stmts = elems(
				label("l0"),
				cjump("l2", "l3"),
				label("l1"),
				move("x", "y"),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				jump("l1"),
				label("l3"),
				call("f"),
				ret()
			);
		
		CFGBuilder builder = new CFGBuilder();
		
		stmts.forEach(builder::visitStatement);
		printNodes(builder);
	}
	
	private void printNodes(CFGBuilder builder) {
		builder.getNodes().forEach(System.out::println);
	}
	
	private IRMove move(String temp, IRExpr expr) {
		return new IRMove(temp(temp), expr);
	}

	private IRMove move(String t1, String t2) {
		return move(t1, temp(t2));
	}
	
	private IRLabel label(String name) {
		return new IRLabel(name);
	}
	
	private IRConst constant(long value) {
		return new IRConst(value);
	}

	private IRJump jump(String label) {
		return new IRJump(new IRName(label));
	}

	private IRCJump cjump(String trueLabel, String falseLabel) {
		return new IRCJump(constant(0L), trueLabel, falseLabel);
	}
	
	private IRReturn ret() {
		return new IRReturn();
	}
	
	private IRTemp temp(String name) {
		return new IRTemp(name);
	}

	private IRCallStmt call(String name) {
		return new IRCallStmt(new IRName(name));
	}
	
	private IRBinOp op(OpType type, IRExpr left, IRExpr right) {
		return new IRBinOp(type, left, right);
	}

}
