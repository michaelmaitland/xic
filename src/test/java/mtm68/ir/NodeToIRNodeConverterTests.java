package mtm68.ir;

import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.Node;
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

	//-------------------------------------------------------------------------------- 
	// CharLiteral 
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// FExp
	//-------------------------------------------------------------------------------- 
	
	//-------------------------------------------------------------------------------- 
	// FunctionDefn
	//-------------------------------------------------------------------------------- 

	//-------------------------------------------------------------------------------- 
	// IntLiteral
	//-------------------------------------------------------------------------------- 

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

	//-------------------------------------------------------------------------------- 
	// BinExpr (Add, And, Div, EqEq, GreaterThan, GreaterThanOrEqual,
	//			HighMult, LessThan, LessThanOrEqual, Mod, Mult,
	//			Or, Sub)
	//-------------------------------------------------------------------------------- 

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
		node = conv.performConvertToIR(node);
		
		return node;
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
