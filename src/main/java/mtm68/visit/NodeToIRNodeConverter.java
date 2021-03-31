package mtm68.visit;

import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.ast.nodes.Node;
import mtm68.exception.FatalTypeException;

public class NodeToIRNodeConverter extends Visitor {

	int labelCounter;
	
	int tmpCounter;
	
	private static final String OUT_OF_BOUNDS_LABEL = "_out_of_bounds";

	private static final String MALLOC_LABEL = "_malloc";
	
	private static final int WORD_SIZE = 8;
	
	public NodeToIRNodeConverter() {
		this.labelCounter = 0;
		this.tmpCounter = 0;
	}

	public String getFreshLabel() {
		labelCounter++;
		return "_l" + labelCounter; 
	}
	
	public String newTemp() {
		tmpCounter++;
		return "_t" + tmpCounter;
	}

	public String getOutOfBoundsLabel() {
		return OUT_OF_BOUNDS_LABEL;
	}
	
	public String getMallocLabel() {
		return MALLOC_LABEL;
	}

	public int getWordSize() {
		return WORD_SIZE;
	}

	public <N extends Node> N performConvertToIR(N root) {
		try {
			return root.accept(this);
		} catch(FatalTypeException e) {
		}
		return root;
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.convertToIR(this);
	}

	}
