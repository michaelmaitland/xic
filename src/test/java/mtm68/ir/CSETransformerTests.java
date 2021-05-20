package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.EQ;
import static mtm68.ir.IRTestUtils.call;
import static mtm68.ir.IRTestUtils.cjump;
import static mtm68.ir.IRTestUtils.constant;
import static mtm68.ir.IRTestUtils.label;
import static mtm68.ir.IRTestUtils.mem;
import static mtm68.ir.IRTestUtils.move;
import static mtm68.ir.IRTestUtils.op;
import static mtm68.ir.IRTestUtils.temp;
import static mtm68.util.TestUtils.assertInstanceOf;
import static mtm68.util.TestUtils.assertInstanceOfAndReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.ir.cfg.CSETransformer;
import mtm68.util.ArrayUtils;

public class CSETransformerTests {
	
	@Test
	void constsAndTempDontGen() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), temp("t3"))
			);
		List<IRStmt> res = perform(func);
		
		assertEquals(2, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		IRTemp t1 = assertInstanceOfAndReturn(IRTemp.class, m1.target());
		assertEquals("t1", t1.name());
		assertInstanceOf(IRConst.class, m1.source());
		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		IRTemp t2 = assertInstanceOfAndReturn(IRTemp.class, m2.target());
		assertEquals("t2", t2.name());
		IRTemp t3 = assertInstanceOfAndReturn(IRTemp.class, m2.source());
		assertEquals("t3", t3.name());
	}

	@Test
	void testXGetsESameTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, constant(1), constant(2))),
				move(temp("t1"), op(ADD, constant(3), constant(4)))
			);
		
		List<IRStmt> res = perform(func);
		
		assertEquals(2, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		IRTemp t1 = assertInstanceOfAndReturn(IRTemp.class, m1.target());
		assertEquals("t1", t1.name());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		IRTemp t2 = assertInstanceOfAndReturn(IRTemp.class, m2.target());
		assertEquals("t1", t2.name());
		assertInstanceOf(IRBinOp.class, m2.source());
	}
	
	@Test
	void testXGetsESameTempSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, op(ADD, constant(1), constant(2)), constant(3))),
				move(temp("t1"), op(ADD, constant(4), constant(5)))
			);
		
		// Second node should contain both binops and nested binop
		List<IRStmt> res = perform(func);
		
		assertEquals(2, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		IRTemp t1 = assertInstanceOfAndReturn(IRTemp.class, m1.target());
		assertEquals("t1", t1.name());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		IRTemp t2 = assertInstanceOfAndReturn(IRTemp.class, m2.target());
		assertEquals("t1", t2.name());
		assertInstanceOf(IRBinOp.class, m2.source());
	}
	
	@Test
	void testXGetsEKillTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), op(ADD, temp("y"), constant(1))),
				move(temp("y"), constant(2)),
				move(temp("z"), op(ADD, temp("y"), constant(1)))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(3, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		IRTemp t1 = assertInstanceOfAndReturn(IRTemp.class, m1.target());
		assertEquals("x", t1.name());
		assertInstanceOf(IRBinOp.class, m1.source());
		
		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		IRTemp t2 = assertInstanceOfAndReturn(IRTemp.class, m2.target());
		assertEquals("y", t2.name());
		assertInstanceOf(IRConst.class, m2.source());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		IRTemp t3 = assertInstanceOfAndReturn(IRTemp.class, m3.target());
		assertEquals("z", t3.name());
		assertInstanceOf(IRBinOp.class, m3.source());

	}
	
	@Test
	void testXGetsEReuseMulti() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), op(ADD, temp("y"), constant(1))),
				move(temp("z"), op(ADD, temp("y"), constant(1))),
				move(temp("w"), op(ADD, temp("y"), constant(1)))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(5, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRTemp.class, m2.target());
		assertInstanceOf(IRTemp.class, m2.source());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRTemp.class, m3.target());
		assertInstanceOf(IRTemp.class, m3.source());
		
		IRMove m4 = assertInstanceOfAndReturn(IRMove.class, res.get(3));
		assertInstanceOf(IRTemp.class, m4.target());
		assertInstanceOf(IRTemp.class, m4.source());
		
		IRMove m5 = assertInstanceOfAndReturn(IRMove.class, res.get(4));
		assertInstanceOf(IRTemp.class, m5.target());
		assertInstanceOf(IRTemp.class, m5.source());
	}
	
	@Test
	void testXGetsEReuse() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), op(ADD, temp("y"), constant(1))),
				move(temp("z"), op(ADD, temp("y"), constant(1)))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(3, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRTemp.class, m2.target());
		assertInstanceOf(IRTemp.class, m2.source());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRTemp.class, m3.target());
		assertInstanceOf(IRTemp.class, m3.source());
	}
	
	@Test
	void testMemOnlyOnLeftKillsAllMem() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(temp("t1")), mem(temp("t2"))),
				move(mem(temp("t3")), constant(3))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(2, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRMem.class, m1.target());
		assertInstanceOf(IRMem.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRMem.class, m2.target());
		assertInstanceOf(IRConst.class, m2.source());
		
	}
	
	@Test
	void testMemOnlyOnRightDoesNotKill() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(temp("t3")), op(ADD, constant(1), constant(2))),
				move(temp("t1"), mem(temp("t2"))),
				move(temp("t4"), op(ADD, constant(1), constant(2)))
			);
		
		// second node out should not kill the mem from n0
		
		List<IRStmt> res = perform(func);
		assertEquals(4, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRMem.class, m2.target());
		assertInstanceOf(IRTemp.class, m2.source());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRTemp.class, m3.target());
		assertInstanceOf(IRMem.class, m3.source());
		
		IRMove m4 = assertInstanceOfAndReturn(IRMove.class, res.get(3));
		assertInstanceOf(IRTemp.class, m4.target());
		assertInstanceOf(IRTemp.class, m4.source());
	}
	
	@Test
	void testMemE1GetsMemE2Out() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, constant(1), constant(1))),
				move(mem(temp("t2")), mem(temp("t3"))),
				move(mem(temp("t5")), mem(temp("t3")))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(4, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRTemp.class, m2.target());
		assertInstanceOf(IRMem.class, m2.source());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRMem.class, m3.target());
		assertInstanceOf(IRTemp.class, m3.source());
		
		IRMove m4 = assertInstanceOfAndReturn(IRMove.class, res.get(3));
		assertInstanceOf(IRMem.class, m4.target());
		assertInstanceOf(IRTemp.class, m4.source());
	}
	
	@Test
	void testMemE1GetsMemE2OutSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(op(ADD, temp("t2"), temp("t3"))), mem(op(ADD, constant(1), temp("t4")))),
				move(temp("t1"), op(ADD, constant(1), temp("t4")))
			);
		
		// Second node out should have [e1], [e2], and all subexprs
		List<IRStmt> res = perform(func);
		assertEquals(3, res.size());
		
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		assertInstanceOf(IRMem.class, m2.target());
		IRMem m = assertInstanceOfAndReturn(IRMem.class, m2.source());
		assertInstanceOf(IRTemp.class, m.expr());
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRTemp.class, m3.target());
		assertInstanceOf(IRTemp.class, m3.source());
	}
	
	@Test
	void testXGetsFGenEArgsAreTemps() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, temp("t2"), temp("4")),
				move(temp("x"), temp("_RET0"))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(2, res.size());
	}
	
	@Test
	void testXGetsFGenEArgIsBinop() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, temp("t2"), temp("4"))),
				move(temp("x"), temp("_RET0"))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(2, res.size());
	}
	
	@Test
	void testXGetsFGenEArgsWithNestedSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, op(ADD,constant(1),temp("t2")), temp("4"))),
				move(temp("x"), temp("_RET0"))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(2, res.size());
	}
	
	
	@Test
	void testXGetsFGenEMultiArg() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, temp("t2"), temp("t4")), op(ADD, temp("t3"), temp("t5"))),
				move(temp("x"), temp("_RET0")),
				move(temp("y"), op(ADD, temp("t2"), temp("t4")))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(4, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		assertInstanceOf(IRTemp.class, m1.target());
		assertInstanceOf(IRBinOp.class, m1.source());

		IRCallStmt c = assertInstanceOfAndReturn(IRCallStmt.class, res.get(1));
		assertInstanceOf(IRTemp.class, c.args().get(0));
		assertInstanceOf(IRBinOp.class, c.args().get(1));
		
		IRMove m3 = assertInstanceOfAndReturn(IRMove.class, res.get(2));
		assertInstanceOf(IRTemp.class, m3.target());
		assertInstanceOf(IRTemp.class, m3.source());
		
		IRMove m4 = assertInstanceOfAndReturn(IRMove.class, res.get(3));
		assertInstanceOf(IRTemp.class, m4.target());
		assertInstanceOf(IRTemp.class, m4.source());
	}
	
	@Test
	void testIfGenE() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				cjump(op(EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(5, res.size());
		assertInstanceOf(IRCJump.class, res.get(0));
		assertInstanceOf(IRLabel.class, res.get(1));
		assertInstanceOf(IRMove.class, res.get(2));
		assertInstanceOf(IRLabel.class, res.get(3));
		assertInstanceOf(IRMove.class, res.get(4));
	}
	

	@Test
	void testIfKillsNothing() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t2"), temp("t1")),
				move(temp("t1"), temp("t3")),
				cjump(op(EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		List<IRStmt> res = perform(func);
		assertEquals(7, res.size());
		assertInstanceOf(IRMove.class, res.get(0));
		assertInstanceOf(IRMove.class, res.get(1));
		assertInstanceOf(IRCJump.class, res.get(2));
		assertInstanceOf(IRLabel.class, res.get(3));
		assertInstanceOf(IRMove.class, res.get(4));
		assertInstanceOf(IRLabel.class, res.get(5));
		assertInstanceOf(IRMove.class, res.get(6));
	}

	private List<IRStmt> perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);
		Map<String, IRFuncDefn> funcs = new HashMap<>();
		funcs.put("f", func);
		IRCompUnit comp = new IRCompUnit("test.xi", funcs);

		CSETransformer c = new CSETransformer(comp, new IRNodeFactory_c());
		IRCompUnit res = c.doCSE();
		return ((IRSeq)(res.functions().get("f").body())).stmts();
	}
}
