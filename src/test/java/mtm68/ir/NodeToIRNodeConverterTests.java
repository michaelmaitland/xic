package mtm68.ir;

import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static mtm68.util.ArrayUtils.singleton;
import static mtm68.util.NodeTestUtil.arbitraryCondition;
import static mtm68.util.NodeTestUtil.arrayWithElems;
import static mtm68.util.NodeTestUtil.assertInstanceOf;
import static mtm68.util.NodeTestUtil.assertInstanceOfAndReturn;
import static mtm68.util.NodeTestUtil.boolLit;
import static mtm68.util.NodeTestUtil.charLit;
import static mtm68.util.NodeTestUtil.emptyArray;
import static mtm68.util.NodeTestUtil.emptyBlock;
import static mtm68.util.NodeTestUtil.intLit;
import static mtm68.util.NodeTestUtil.stringLit;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.ArrayLength;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.ProcedureCall;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.Visitor;

public class NodeToIRNodeConverterTests {

	//-------------------------------------------------------------------------------- 
	// ArrayIndex
	//-------------------------------------------------------------------------------- 

	@Test
	public void testArrayIndex() {
		ArrayIndex ai = new ArrayIndex(arrayWithElems(intLit(0L)), intLit(0L));
		ArrayIndex newAi = doConversion(ai);
		
		IRESeq eseq = assertInstanceOfAndReturn(IRESeq.class, newAi.getIRExpr());
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, eseq.stmt());
		
		/*
		 * 3 for:  moving arr expr into temp
		 *		   moving index expr into temp
		 *         bounds check 
		 */
		assertEquals(3, seq.stmts().size());
		assertInstanceOf(IRMove.class, seq.stmts().get(0)); 
		assertInstanceOf(IRMove.class, seq.stmts().get(1)); 
		IRSeq boundsCheck = assertInstanceOfAndReturn(IRSeq.class, seq.stmts().get(2)); 
		assertInstanceOf(IRCJump.class, boundsCheck.stmts().get(0)); 
		assertInstanceOf(IRLabel.class, boundsCheck.stmts().get(1)); 

		assertInstanceOf(IRMem.class, eseq.expr());
	}

	//-------------------------------------------------------------------------------- 
	// ArrayInit
	//-------------------------------------------------------------------------------- 

	@Test
	public void testArrayInitEmpty() {
		ArrayInit ai = emptyArray();
		ArrayInit newAi = doConversion(ai);
		assertArrayInit(newAi, 0);
	}

	@Test
	public void testArrayInitOneElem() {
		ArrayInit ai = arrayWithElems(intLit(1L));
		ArrayInit newAi = doConversion(ai);
		assertArrayInit(newAi, 1);
	}
	
	@Test
	public void testArrayInitMultipleElem() {
		ArrayInit ai = arrayWithElems(intLit(1L), intLit(0L));
		ArrayInit newAi = doConversion(ai);
		assertArrayInit(newAi, 2);
	}
		
	private void assertArrayInit(ArrayInit converted, int numElems) {
		IRESeq eseq = assertInstanceOfAndReturn(IRESeq.class, converted.getIRExpr());
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, eseq.stmt());
		assertEquals(2 + numElems, seq.stmts().size()); // one to alloc, one to set length
		assertInstanceOf(IRMove.class, seq.stmts().get(0)); 
		IRMove moveLength = assertInstanceOfAndReturn(IRMove.class, seq.stmts().get(1)); 
		IRConst length = assertInstanceOfAndReturn(IRConst.class, moveLength.source());
		assertEquals(numElems, length.constant());
		assertInstanceOf(IRBinOp.class, eseq.expr());
	}

	//-------------------------------------------------------------------------------- 
	// ArrayLength
	//-------------------------------------------------------------------------------- 
	@Test
	public void testArrayLength() {
		ArrayLength arrLen = new ArrayLength(stringLit("hey"));
		ArrayLength newArrLen = doConversion(arrLen);
		
		assertInstanceOf(IRMem.class, newArrLen.getIRExpr());
	}
	
	@Test
	public void testArrayLengthWhenArrNotInit() {
		fail();
	}
	
	//-------------------------------------------------------------------------------- 
	// BoolLiteral
	//-------------------------------------------------------------------------------- 

	@Test
	void convertTrue() {
		BoolLiteral literal = boolLit(true);
		BoolLiteral newLiteral = doConversion(literal);
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIRExpr());
		assertTrue(c.isConstant());
		assertEquals(1,c.constant());
	}

	@Test
	void convertFalse() {
		BoolLiteral literal = boolLit(false);
		BoolLiteral newLiteral = doConversion(literal);
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIRExpr());

		assertTrue(c.isConstant());
		assertEquals(0, c.constant());
	}

	//-------------------------------------------------------------------------------- 
	// CharLiteral 
	//-------------------------------------------------------------------------------- 
	@Test
	void convertCharLiteral() {
		CharLiteral literal = charLit('a');
		CharLiteral newLiteral = doConversion(literal);

		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIRExpr());

		assertTrue(c.isConstant());
		assertEquals('a', c.constant());
	}

	//-------------------------------------------------------------------------------- 
	// FExp
	//-------------------------------------------------------------------------------- 
	
	//-------------------------------------------------------------------------------- 
	// FunctionDefn
	//-------------------------------------------------------------------------------- 
	
	
	@Test
	public void testFunctionDefnNoArgsNoRet() {
		FunctionDecl fDecl = new FunctionDecl("f", empty(), empty());
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_If_p", ir.name());
	}
	
	@Test
	public void testFunctionDefnNoArgsOneRet() {
		FunctionDecl fDecl = new FunctionDecl("f", empty(), singleton(Types.INT));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_If_i", ir.name());	
	}
	
	@Test
	public void testFunctionDefnNoArgsMultipleRet() {
		FunctionDecl fDecl = new FunctionDecl("f", empty(), elems(Types.BOOL, Types.INT));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_If_t2bi", ir.name());		
	}
	
	@Test
	public void testFunctionDefnMain() {
		FunctionDecl fDecl = new FunctionDecl("main",
				elems(new SimpleDecl("args", Types.ARRAY(Types.ARRAY(Types.INT)))),
				empty());
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_Imain_paai", ir.name());		
	}
	
	@Test
	public void testFunctionUnparseInt() {
		FunctionDecl fDecl = new FunctionDecl("unparseInt",
				elems(new SimpleDecl("n", Types.INT)),
				singleton(Types.ARRAY(Types.INT)));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_IunparseInt_aii", ir.name());		
	}
	
	@Test
	public void testFunctionParseInt() {
		FunctionDecl fDecl = new FunctionDecl("parseInt",
				elems(new SimpleDecl("str", Types.ARRAY(Types.INT))),
				elems(Types.INT, Types.BOOL));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_IparseInt_t2ibai", ir.name());		
	}
	
	@Test
	public void testFunctionEof() {
		FunctionDecl fDecl = new FunctionDecl("eof",
				empty(),
				singleton(Types.BOOL));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_Ieof_b", ir.name());		
	}
	
	@Test
	public void testFunctionGCD() {
		FunctionDecl fDecl = new FunctionDecl("gcd",
				elems(new SimpleDecl("a", Types.INT), new SimpleDecl("b", Types.INT)),
				singleton(Types.INT));
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_Igcd_iii", ir.name());		
	}
	
	@Test
	public void testFunctionMultipleUnderscores() {
		FunctionDecl fDecl = new FunctionDecl("multiple__underScores",
				empty(),
				empty());
		Block voidBlock = new Block(empty());
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		FunctionDefn newDefn = doConversion(fDefn);	
		
		IRFuncDefn ir = assertInstanceOfAndReturn(IRFuncDefn.class, newDefn.getIRFuncDefn());
		assertEquals("_Imultiple____underScores_p", ir.name());		
	}

	//-------------------------------------------------------------------------------- 
	// IntLiteral
	//-------------------------------------------------------------------------------- 
	@Test
	void convertIntLiteral() {
		IntLiteral literal = intLit(10L);
		IntLiteral newLiteral = doConversion(literal);
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIRExpr());

		assertTrue(c.isConstant());
		assertEquals(10L, c.constant());
	}
	//-------------------------------------------------------------------------------- 
	// Negate 
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// Not 
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// StringLiteral
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// Var
	//-------------------------------------------------------------------------------- 
	@Test
	public void testVar() {
		Var var = new Var("x");
		Var newVar = doConversion(var);

		IRTemp t = assertInstanceOfAndReturn(IRTemp.class, newVar.getIRExpr());
		assertEquals("_tx", t.name());
	}
	

	//-------------------------------------------------------------------------------- 
	// BinExpr (Add, And, Div, EqEq, GreaterThan, GreaterThanOrEqual,
	//			HighMult, LessThan, LessThanOrEqual, Mod, Mult,
	//			Or, Sub)
	//-------------------------------------------------------------------------------- 

	@Test
	public void testAdd() {
		Add add = new Add(intLit(0L), intLit(1L));
		Add newAdd = doConversion(add);

		IRBinOp b = assertInstanceOfAndReturn(IRBinOp.class, newAdd.getIRExpr());
		assertEquals(OpType.ADD, b.opType());
	}
	
	@Test
	public void testBinExprLeftRightSet() {
		Add add = new Add(intLit(0L), intLit(1L));
		Add newAdd = doConversion(add);

		IRBinOp b = assertInstanceOfAndReturn(IRBinOp.class, newAdd.getIRExpr());
		assertNotNull(b.left());
		assertNotNull(b.right());
	}

	//-------------------------------------------------------------------------------- 
	// Assign
	//-------------------------------------------------------------------------------- 

	@Test
	void testSingleAssign() {
		SingleAssign assign = new SingleAssign(
				new Var("x"), intLit(0L));
		SingleAssign newAssign = doConversion(assign);
		
		IRMove move = assertInstanceOfAndReturn(IRMove.class, newAssign.getIRStmt());
		assertTrue(move.target() instanceof IRTemp);
	}

	@Test
	void testSingleAssignToSimpleDecl() {
		SingleAssign assign = new SingleAssign(
				new SimpleDecl("x", Types.INT), intLit(0L));
		SingleAssign newAssign = doConversion(assign);
		
		IRMove move = assertInstanceOfAndReturn(IRMove.class, newAssign.getIRStmt());
		assertTrue(move.target() instanceof IRTemp);
	}

	@Test
	void testMultipleAssign() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		List<Optional<SimpleDecl>> decls = elems(
				Optional.of(new SimpleDecl("x", Types.INT)),
				Optional.of(new SimpleDecl("y", Types.INT))
			);
		FExpr rhs = new FExpr("f", empty()); 
		MultipleAssign assign = new MultipleAssign(decls, rhs);
		MultipleAssign newAssign = doConversion(funcAndProcEncodings, assign);
	
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newAssign.getIRStmt());
		assertEquals(3, seq.stmts().size());
		assertInstanceOf(IRCallStmt.class, seq.stmts().get(0));
		IRMove move1 = assertInstanceOfAndReturn(IRMove.class, seq.stmts().get(1));
		IRTemp ret1 = assertInstanceOfAndReturn(IRTemp.class, move1.source());
		assertEquals("RET_0", ret1.name());
		IRMove move2 = assertInstanceOfAndReturn(IRMove.class, seq.stmts().get(2));
		IRTemp ret2 = assertInstanceOfAndReturn(IRTemp.class, move2.source());
		assertEquals("RET_1", ret2.name());
	}

	@Test
	void testMultipleAssignWildcard() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		List<Optional<SimpleDecl>> decls = elems(
				Optional.empty()
			);
		FExpr rhs = new FExpr("f", empty()); 
		MultipleAssign assign = new MultipleAssign(decls, rhs);
		MultipleAssign newAssign = doConversion(funcAndProcEncodings, assign);
	
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newAssign.getIRStmt());
		assertEquals(1, seq.stmts().size());
		assertInstanceOf(IRCallStmt.class, seq.stmts().get(0));
	}

	@Test
	void testMultipleAssignWildcardAndRealDecl() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		List<Optional<SimpleDecl>> decls = elems(
				Optional.empty(),
				Optional.of(new SimpleDecl("y", Types.INT))
			);
		FExpr rhs = new FExpr("f", empty()); 
		MultipleAssign assign = new MultipleAssign(decls, rhs);
		MultipleAssign newAssign = doConversion(funcAndProcEncodings, assign);
	
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newAssign.getIRStmt());
		assertEquals(2, seq.stmts().size());
		assertTrue(!seq.stmts().isEmpty());
		assertInstanceOf(IRCallStmt.class, seq.stmts().get(0));
		IRMove move1 = assertInstanceOfAndReturn(IRMove.class, seq.stmts().get(1));
		IRTemp ret1 = assertInstanceOfAndReturn(IRTemp.class, move1.source());
		assertEquals("RET_1", ret1.name());
	}

	//-------------------------------------------------------------------------------- 
	// Block
	//-------------------------------------------------------------------------------- 

	@Test
	void testEmptyBlock() {
		Block block = new Block(empty());
		Block newBlock = doConversion(block);
		
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newBlock.getIRStmt());
		assertTrue(seq.stmts().isEmpty());
	}
	
	@Test
	void testBlockNoRet() {
		Block block = new Block(elems(
				new SimpleDecl("x", Types.INT),
				new SimpleDecl("y", Types.INT),
				new SimpleDecl("z", Types.INT)
				));
		Block newBlock = doConversion(block);
		
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newBlock.getIRStmt());
		assertEquals(3, seq.stmts().size());
	}
	
	@Test
	void testBlockOnlyRet() {
		Block block = new Block(empty(), new Return(empty()));
		Block newBlock = doConversion(block);
		
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newBlock.getIRStmt());
		assertEquals(1, seq.stmts().size());
	}
	
	@Test
	void testBlockStmtsAndRet() {
		Block block = new Block(elems(
				new SimpleDecl("x", Types.INT),
				new SimpleDecl("y", Types.INT),
				new SimpleDecl("z", Types.INT)
				), new Return(empty()));
		Block newBlock = doConversion(block);
		
		IRSeq seq = assertInstanceOfAndReturn(IRSeq.class, newBlock.getIRStmt());
		assertEquals(4, seq.stmts().size());

	}

	//-------------------------------------------------------------------------------- 
	// Decl
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// If
	//-------------------------------------------------------------------------------- 
	
	@Test
	public void testIfBranchNoElse() {
		If ifStmt = new If(arbitraryCondition(), emptyBlock());
		If newIfStmt = doConversion(ifStmt);
		
		assertInstanceOfAndReturn(IRSeq.class, newIfStmt.getIRStmt());
	}

	//-------------------------------------------------------------------------------- 
	// While
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// Procedure Call
	//-------------------------------------------------------------------------------- 
	
	@Test
	public void testProcedureCallNoArgs() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		ProcedureCall stmt = new ProcedureCall(new FExpr("f", empty()));
		ProcedureCall newStmt = doConversion(funcAndProcEncodings, stmt);
		
		IRCallStmt call = assertInstanceOfAndReturn(IRCallStmt.class, newStmt.getIRStmt());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
		
		assertEquals(0, call.args().size());
	}
	
	@Test
	public void testProcedureCallOneArg() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		ProcedureCall stmt = new ProcedureCall(new FExpr("f", elems(intLit(0L))));
		ProcedureCall newStmt = doConversion(funcAndProcEncodings, stmt);

		IRCallStmt call = assertInstanceOfAndReturn(IRCallStmt.class, newStmt.getIRStmt());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
		
		assertEquals(1, call.args().size());
	}

	@Test
	public void testProcedureCallMultiArg() {
		
		Map<String, String> funcAndProcEncodings = new HashMap<>();
		funcAndProcEncodings.put("f", "f");
		
		ProcedureCall stmt = new ProcedureCall(new FExpr("f",
						elems(intLit(0L), arbitraryCondition())));
		ProcedureCall newStmt = doConversion(funcAndProcEncodings, stmt);

		IRCallStmt call = assertInstanceOfAndReturn(IRCallStmt.class, newStmt.getIRStmt());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());

		assertEquals(2, call.args().size());
	}
	
	//-------------------------------------------------------------------------------- 
	// Return
	//-------------------------------------------------------------------------------- 
	@Test
	public void testSingleReturnValue() {
		Return ret = new Return(singleton(intLit(0L)));
		Return newRet = doConversion(ret);
		
		IRReturn irRet = assertInstanceOfAndReturn(IRReturn.class, newRet.getIRStmt());
		assertEquals(1, irRet.rets().size());
		assertInstanceOf(IRConst.class, irRet.rets().get(0));
	}
	
	@Test
	public void testMultipleReturnValue() {
		Return ret = new Return(elems(intLit(0L), intLit(1L)));
		Return newRet = doConversion(ret);
		
		IRReturn irRet = assertInstanceOfAndReturn(IRReturn.class, newRet.getIRStmt());
		assertEquals(2, irRet.rets().size());
	}

	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private <N extends Node> N doConversion(N node) {
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter(new IRNodeFactory_c());
		addLocs(node);
		return conv.performConvertToIR(node);
	}
	
	private <N extends Node> N doConversion(Map<String, String> funcAndProcEncodings, N node) {
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter(funcAndProcEncodings, new IRNodeFactory_c());
		addLocs(node);
		return conv.performConvertToIR(node);
	}

	private void addLocs(Node n) {
		n.accept(new Visitor() {
			@Override
			public Node leave(Node parent, Node n) {
				n.setStartLoc(new Location(0, 0));
				return n;
			}
		});
	}
}
