package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.MUL;
import static mtm68.ir.IRTestUtils.constant;
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
import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.ir.cfg.CopyPropTransformer;
import mtm68.util.ArrayUtils;

public class CopyPropTests {
	
	@Test
	void dontUseDontReplace() throws IOException {
		
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
	void reachReplace() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), temp("y")),
				move(temp("z"), op(ADD, op(MUL, constant(2), temp("x")), constant(1)))
			);
		List<IRStmt> res = perform(func);
		
		assertEquals(2, res.size());
		IRMove m1 = assertInstanceOfAndReturn(IRMove.class, res.get(0));
		IRTemp t1 = assertInstanceOfAndReturn(IRTemp.class, m1.target());
		assertEquals("x", t1.name());
		IRTemp t2 = assertInstanceOfAndReturn(IRTemp.class, m1.source());
		assertEquals("y", t2.name());

		IRMove m2 = assertInstanceOfAndReturn(IRMove.class, res.get(1));
		IRTemp t3 = assertInstanceOfAndReturn(IRTemp.class, m2.target());
		assertEquals("z", t3.name());

		IRBinOp b1 = assertInstanceOfAndReturn(IRBinOp.class, m2.source());
		IRBinOp b2 = assertInstanceOfAndReturn(IRBinOp.class, b1.left());
		IRTemp t4  = assertInstanceOfAndReturn(IRTemp.class, b2.right());
		assertEquals("y", t4.name());
	}	

	private List<IRStmt> perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);
		Map<String, IRFuncDefn> funcs = new HashMap<>();
		funcs.put("f", func);
		IRCompUnit comp = new IRCompUnit("test.xi", funcs);

		CopyPropTransformer c = new CopyPropTransformer(comp, new IRNodeFactory_c());
		IRCompUnit res = c.doCopyProp();
		return ((IRSeq)(res.functions().get("f").body())).stmts();
	}
}
