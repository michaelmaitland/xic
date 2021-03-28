package mtm68.ir;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.Node;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.Visitor;

public class NodeToIRNodeConverterTests {

	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private <N extends Node> N doConversion(N node) {
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter();
		addLocs(node);
		node = conv.performConvertToIR(node);
		
		if(conv.hasError()) {
			assertTrue(false, "Expected no errors but got " + conv.getFirstError().getFileErrorMessage());
		}
		return node;
	}

	private <N extends Node> void assertConversionError( N node) {
		NodeToIRNodeConverter conv = new NodeToIRNodeConverter();
		addLocs(node);
		conv.performConvertToIR(node);
		assertTrue(conv.hasError(), "Expected conversion error but got none");
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
