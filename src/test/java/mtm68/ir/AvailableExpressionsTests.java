package mtm68.ir;

import static mtm68.ir.IRTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import mtm68.assem.Assem;
import mtm68.assem.CmpAssem;
import mtm68.assem.CqoAssem;
import mtm68.assem.IDivAssem;
import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.MulAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.SetccAssem;
import mtm68.assem.SetccAssem.CC;
import mtm68.assem.cfg.Liveness;
import mtm68.assem.op.AddAssem;
import mtm68.assem.operand.RealReg;
import mtm68.ir.cfg.AvailableExprs;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class AvailableExpressionsTests {
	
	@Test
	void test2XGetEs() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), constant(2))
			);
		
		perform(func);
	}

	@Test
	void testXGetsESameTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t1"), constant(2))
			);
		
		// Second node should contain both CONST 1 and CONST 2
		perform(func);
	}
	
	@Test
	void testXGetsEKillTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), temp("y")),
				move(temp("x"), constant(2))
			);
		
		// Second node should contain both CONST 1 and CONST 2
		perform(func);
	}
	

	@Test
	void testXGetsEKill() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t2"), op(OpType.ADD, temp("t1"), constant(1))),
				move(temp("t1"), constant(2))
			);
		
		// Second node should contain CONST 1 and CONST 2
		// because it killed the BINOP and TEMP 1
		perform(func);
	}
	
	@Test
	void testXGetsEKillNested() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t2"), temp("t1")),
				move(temp("t1"), constant(2))
			);
		
		// Second node should contain both CONST 1 and CONST 2
		perform(func);
	}

	@Test
	void testMemOnlyOnLeftKillsAllMem() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(mem(temp("t1")), mem(temp("t2"))),
				move(mem(temp("t2")), constant(3))
			);
		
		// Third node out should have no mem
		perform(func);
	}
	
	@Test
	void testMemE1GetsMemE2() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(mem(temp("t1")), mem(temp("t2")))
			);
		
		// Second node out should have [e1], [e2], and all subexprs
		perform(func);
	}
	
	@Test
	void testXGetsFGenEAndAllSubexprs() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("y"), temp("x")),
				move(temp("t1"), constant(1)),
				move(temp("t2"), mem(temp("t3"))),
				move(temp("t4"), temp("t5")),
				call("f", 1, temp("t2"), temp("4")),
				move(temp("x"), temp("_RET0"))
			);
		
		perform(func);
	}
	
	@Test
	void testIf() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				cjump(op(OpType.EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		// Second and third nodes should have the same in and out as the out from the first
		perform(func);
	}
	

	@Test
	void testIfKillsNothing() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t2"), temp("t1")),
				move(temp("t1"), temp("t3")),
				cjump(op(OpType.EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		// Second and third nodes should have the same in and out as the out from the first
		perform(func);
	}

	private void perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);
		Map<String, IRFuncDefn> funcs = new HashMap<>();
		funcs.put("f", func);
		IRCompUnit comp = new IRCompUnit("test.xi", funcs);

		AvailableExprs ae = new AvailableExprs();
		ae.performAvaliableExpressionsAnalysis(comp, new IRNodeFactory_c());
		
		ae.getGraph();

		ae.showGraph(new PrintWriter(System.out));
	}
}
