package mtm68.ir;

import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static mtm68.util.NodeTestUtil.arbitraryCondition;
import static mtm68.util.NodeTestUtil.arrayWithElems;
import static mtm68.util.NodeTestUtil.assertInstanceOf;
import static mtm68.util.NodeTestUtil.assertInstanceOfAndReturn;
import static mtm68.util.NodeTestUtil.boolLit;
import static mtm68.util.NodeTestUtil.charLit;
import static mtm68.util.NodeTestUtil.emptyArray;
import static mtm68.util.NodeTestUtil.emptyBlock;
import static mtm68.util.NodeTestUtil.intLit;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExp;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.If;
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
		assertEquals(4, seq.stmts().size());
		assertInstanceOf(IRMove.class, seq.stmts().get(0)); 
		assertInstanceOf(IRMove.class, seq.stmts().get(1)); 
		assertInstanceOf(IRCJump.class, seq.stmts().get(2)); 
		assertInstanceOf(IRLabel.class, seq.stmts().get(3)); 
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
		assertEquals("x", t.name());
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
	public void testFunctionCallNoArgs() {
		ProcedureCall stmt = new ProcedureCall(new FExpr("f", empty()));
		ProcedureCall newStmt = doConversion(stmt);
		
		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIRStmt());
		IRCall call = assertInstanceOfAndReturn(IRCall.class, exp.expr());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
	}
	
	@Test
	public void testFunctionCallOneArg() {
		ProcedureCall stmt = new ProcedureCall(new FExpr("f", elems(intLit(0L))));
		ProcedureCall newStmt = doConversion(stmt);

		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIRStmt());
		IRCall call = assertInstanceOfAndReturn(IRCall.class, exp.expr());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
	}

	@Test
	public void testFunctionCallMultiArg() {
		ProcedureCall stmt = new ProcedureCall(new FExpr("f",
						elems(intLit(0L), arbitraryCondition())));
		ProcedureCall newStmt = doConversion(stmt);

		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIRStmt());
		IRCall call = assertInstanceOfAndReturn(IRCall.class, exp.expr());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
	}
	
	//-------------------------------------------------------------------------------- 
	// Return
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private <N extends Node> N doConversion(N node) {
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter(new IRNodeFactory_c());
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
