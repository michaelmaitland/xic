package mtm68.ir;

import static mtm68.util.NodeTestUtil.boolLit;
import static mtm68.util.NodeTestUtil.charLit;
import static mtm68.util.NodeTestUtil.intLit;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRTemp;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
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
		
		assertTrue(newLiteral.getIrNode().isConstant());
		assertEquals(1, newLiteral.getIrNode().constant());
	}

	@Test
	void convertFalse() {
		BoolLiteral literal = boolLit(false);
		BoolLiteral newLiteral = doConversion(literal);
		
		assertTrue(newLiteral.getIrNode().isConstant());
		assertEquals(0, newLiteral.getIrNode().constant());

	}
	//-------------------------------------------------------------------------------- 
	// CharLiteral 
	//-------------------------------------------------------------------------------- 
	@Test
	void convertCharLiteral() {
		CharLiteral literal = charLit('a');
		CharLiteral newLiteral = doConversion(literal);
		
		assertTrue(newLiteral.getIrNode().isConstant());
		assertEquals('a', newLiteral.getIrNode().constant());
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
		
		assertTrue(newLiteral.getIrNode().isConstant());
		assertEquals(10L, newLiteral.getIrNode().constant());
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

		assertTrue(newVar.getIrNode() instanceof IRTemp);
		assertEquals("x", ((IRTemp)newVar.getIrNode()).name());
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

		assertTrue(newAdd.getIrNode() instanceof IRBinOp);
		assertEquals(OpType.ADD, ((IRBinOp)newAdd.getIrNode()).opType());
	}
	
	@Test
	public void testBinExprLeftRightSet() {
		Add add = new Add(intLit(0L), intLit(1L));
		Add newAdd = doConversion(add);

		assertTrue(newAdd.getIrNode() instanceof IRBinOp);
		assertNotNull(((IRBinOp)newAdd.getIrNode()).left());
		assertNotNull(((IRBinOp)newAdd.getIrNode()).right());
	}

	//-------------------------------------------------------------------------------- 
	// Assign
	//-------------------------------------------------------------------------------- 

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
