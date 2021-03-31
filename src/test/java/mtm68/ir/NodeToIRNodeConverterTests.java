package mtm68.ir;

import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static mtm68.util.NodeTestUtil.arbitraryCondition;
import static mtm68.util.NodeTestUtil.boolLit;
import static mtm68.util.NodeTestUtil.charLit;
import static mtm68.util.NodeTestUtil.intLit;
import static mtm68.util.NodeTestUtil.assertInstanceOfAndReturn;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExp;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRTemp;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.stmts.ProcedureCall;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.Visitor;

public class NodeToIRNodeConverterTests {

	//-------------------------------------------------------------------------------- 
	// ArrayIndex
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// ArrayInit
	//-------------------------------------------------------------------------------- 

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
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIrExpr());
		assertTrue(c.isConstant());
		assertEquals(1,c.constant());
	}

	@Test
	void convertFalse() {
		BoolLiteral literal = boolLit(false);
		BoolLiteral newLiteral = doConversion(literal);
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIrExpr());

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

		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIrExpr());

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
		
		IRConst c = assertInstanceOfAndReturn(IRConst.class, newLiteral.getIrExpr());

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

		IRTemp t = assertInstanceOfAndReturn(IRTemp.class, newVar.getIrExpr());
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

		IRBinOp b = assertInstanceOfAndReturn(IRBinOp.class, newAdd.getIrExpr());
		assertEquals(OpType.ADD, b.opType());
	}
	
	@Test
	public void testBinExprLeftRightSet() {
		Add add = new Add(intLit(0L), intLit(1L));
		Add newAdd = doConversion(add);

		IRBinOp b = assertInstanceOfAndReturn(IRBinOp.class, newAdd.getIrExpr());
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
		
		IRMove move = assertInstanceOfAndReturn(IRMove.class, newAssign.getIrStmt());
		assertTrue(move.target() instanceof IRTemp);
	}

	//-------------------------------------------------------------------------------- 
	// Block
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// Decl
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// If
	//-------------------------------------------------------------------------------- 

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
		
		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIrStmt());
		IRCall call = assertInstanceOfAndReturn(IRCall.class, exp.expr());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
	}
	
	@Test
	public void testFunctionCallOneArg() {
		ProcedureCall stmt = new ProcedureCall(new FExpr("f", elems(intLit(0L))));
		ProcedureCall newStmt = doConversion(stmt);

		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIrStmt());
		IRCall call = assertInstanceOfAndReturn(IRCall.class, exp.expr());
		IRName name = assertInstanceOfAndReturn(IRName.class, call.target());
		assertEquals("f", name.name());
	}

	@Test
	public void testFunctionCallMultiArg() {
		ProcedureCall stmt = new ProcedureCall(new FExpr("f",
						elems(intLit(0L), arbitraryCondition())));
		ProcedureCall newStmt = doConversion(stmt);

		IRExp exp = assertInstanceOfAndReturn(IRExp.class, newStmt.getIrStmt());
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
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter();
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
