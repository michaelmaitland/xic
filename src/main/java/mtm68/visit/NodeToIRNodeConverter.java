package mtm68.visit;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.binary.And;
import mtm68.ast.nodes.binary.EqEq;
import mtm68.ast.nodes.binary.Or;
import mtm68.exception.FatalTypeException;
import mtm68.util.ArrayUtils;

public class NodeToIRNodeConverter extends Visitor {

	int labelCounter;
	
	int tmpCounter;
	
	private static final String OUT_OF_BOUNDS_LABEL = "_xi__out_of_bounds";

	private static final String MALLOC_LABEL = "_xi_alloc";
	
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
	
	public String getFuncSymbol() {
		/*
		 * To encode procedure or function:
		 * "_I" + nameOfFunction + "_" + return type encoding (p if proc) +  encodings of each argument
		 * 
		 * To encode function:
		 * 
		 */
		return null;
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

	public IRStmt getCtrlFlow(Expr condition, String trueLabel, String falseLabel) {
		if(condition instanceof BoolLiteral) {
			return  getCtrlFlow((BoolLiteral)condition, trueLabel, falseLabel);
		} else if (condition instanceof Not) {
			return getCtrlFlow((Not)condition, trueLabel, falseLabel);
		} else if (condition instanceof And) {
			return getCtrlFlow((And)condition, trueLabel, falseLabel);
		} else if (condition instanceof Or) {
			return getCtrlFlow((Or)condition, trueLabel, falseLabel);
		} else if(condition instanceof EqEq) {
			return getCtrlFlow((EqEq)condition, trueLabel, falseLabel);
		} else {
			return new IRCJump(condition.getIrExpr(), trueLabel, falseLabel);
		}
	}
	
	private IRStmt getCtrlFlow(BoolLiteral b, String trueLabel, String falseLabel) {
			String labelToJump = b.getValue() ? trueLabel : falseLabel;
			return new IRJump(new IRName(labelToJump));
	}
	
	private IRStmt getCtrlFlow(Not n, String trueLabel, String falseLabel) {
			return getCtrlFlow(n.getExpr(), falseLabel, trueLabel);
	}
	
	private IRStmt getCtrlFlow(And a, String trueLabel, String falseLabel) {
			String l1 = getFreshLabel();
			List<IRStmt> stmts = ArrayUtils.empty();
			stmts.add(getCtrlFlow(a.getLeft(), l1, falseLabel));
			stmts.add(new IRLabel(l1));
			stmts.add(getCtrlFlow(a.getRight(), trueLabel, falseLabel));
			return new IRSeq(stmts);
	}

	private IRStmt getCtrlFlow(Or o, String trueLabel, String falseLabel) {
			String truePrime = getFreshLabel();
			List<IRStmt> stmts = ArrayUtils.empty();
			stmts.add(getCtrlFlow(o.getLeft(), truePrime, falseLabel));
			stmts.add(new IRLabel(truePrime));
			stmts.add(getCtrlFlow(o.getRight(), trueLabel, falseLabel));
			return new IRSeq(stmts);
	}
	
	private IRStmt getCtrlFlow(EqEq e, String trueLabel, String falseLabel) {
			IRExpr left = e.getLeft().getIrExpr();
			IRExpr right = e.getRight().getIrExpr();
			return new IRCJump(new IRBinOp(OpType.EQ, left, right), trueLabel, falseLabel);
	}
}
