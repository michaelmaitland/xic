package mtm68.visit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.type.ArrayType;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.binary.And;
import mtm68.ast.nodes.binary.EqEq;
import mtm68.ast.nodes.binary.Or;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.BoolType;
import mtm68.ast.types.IntType;
import mtm68.ast.types.Type;
import mtm68.exception.FatalTypeException;
import mtm68.exception.InternalCompilerException;
import mtm68.util.ArrayUtils;

public class NodeToIRNodeConverter extends Visitor {

	private int labelCounter;
	
	private int tmpCounter;
	
	/*
	 * Keys are the function/procedure name as defined in the
	 * AST nodes. Values are the encoded function/procedure 
	 * symbol.
	 */
	private Map<String, String> funcAndProcEncodings;
	
	private static final String OUT_OF_BOUNDS_LABEL = "_xi__out_of_bounds";

	private static final String MALLOC_LABEL = "_xi_alloc";
	
	private static final int WORD_SIZE = 8;
	
	public NodeToIRNodeConverter() {
		this.labelCounter = 0;
		this.tmpCounter = 0;
		this.funcAndProcEncodings = new HashMap<>();
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
	
	/**
	 * Returns an encoding of a function or procedure
	 * using the encoding defined in the Xi ABI.
	 */
	public String getFuncSymbol(FunctionDecl functionDecl) {
		StringBuilder sb = new StringBuilder();
		sb.append("_I");
		sb.append(encodeFuncName(functionDecl.getId()));
		sb.append("_");
		
		sb.append(encodeReturnTypes(functionDecl.getReturnTypes()));

		List<Type> argTypes = functionDecl.getArgs()
										  .stream()
										  .map(SimpleDecl::getType)
										  .collect(Collectors.toList());
		sb.append(encodeTypes(argTypes));
	
		String encoded = sb.toString();
		funcAndProcEncodings.put(functionDecl.getId(), encoded); 
		return encoded;
	}
	
	/**
	 * Returns an encoding of a procedure using the encoding defined in the Xi ABI.
	 * 
	 * @throws InternalCompilerException if the symbol has not yet been defined.
	 */
	public String getFuncSymbol(FExpr expr) {
		String enc = this.funcAndProcEncodings.get(expr.getId());
		
		if(enc == null) {
			throw new InternalCompilerException();
		}
		
		return enc;
	}	
	
	private String encodeFuncName(String funcName) {
		return funcName.replaceAll("_", "__");
	}
	
	/**
	 * Encodes a list of types by concatenating the encoding of each type, which
	 * uses the same encoding rules as {@code encodeType}.
	 */
	private String encodeTypes(List<Type> types) {
		StringBuilder enc = new StringBuilder();
		for(Type t : types) {
			enc.append(encodeType(t));
		}
		return enc.toString();
	}

	/** 
	 * Encodes a type. 
	 * ints are encoded as {@code i}
	 * bools are encoded as {@code b}
	 * arrays are encoded as {@code a}
	 * 
	 */
	private String encodeType(Type type) {
		if(type instanceof IntType) {
			return "i";
		} else if(type instanceof BoolType) {
			return "b";
		} else if (type instanceof ArrayType) {
			return "a";
		} else {
			throw new FatalTypeException();
		}
	}
	
	/**
	 * Encodes the return types of a function. If a function returns a single return
	 * type, it is encoded using the same rules as defined by {@code encodeType}. If
	 * a function returns multiple results, it is encoded as {@code t} followed by
	 * the number of arguments returned, followed by the encoding of the types
	 * returned. If there are no return types, then it is encoded as {@code p}.
	 * 
	 */
	private String encodeReturnTypes(List<Type> types) {

		if(types.isEmpty()) {
			return "p";
		} else if(types.size() == 1) {
			return encodeType(types.get(0));
		} else {
			return "t" + types.size() + encodeTypes(types);
		}
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
			return new IRCJump(condition.getIRExpr(), trueLabel, falseLabel);
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
			IRExpr left = e.getLeft().getIRExpr();
			IRExpr right = e.getRight().getIRExpr();
			return new IRCJump(new IRBinOp(OpType.EQ, left, right), trueLabel, falseLabel);
	}

	public IRSeq boundsCheck(IRExpr arr, IRExpr index) {
		IRLabel ok = new IRLabel(getFreshLabel());
		String errLabel = getOutOfBoundsLabel();

		IRMem lenAddr = new IRMem(new IRBinOp(OpType.SUB, arr, new IRConst(getWordSize())));
		IRBinOp boundsCheck = new IRBinOp(OpType.ULT, index, lenAddr);

		return new IRSeq(new IRCJump(boundsCheck, ok.name(), errLabel),
						 ok);
	}

	public IRMem getOffsetIntoArr(IRExpr arr, IRExpr index) {
		/*
		 * index is going to be at mem address: (mem addr of arr) + (WORD_SIZE * index).
		 * We can us the temp's here because it will be executed after
		 * a seq that does the temp setup
		 */
		IRExpr e = new IRBinOp(OpType.MUL, new IRConst(getWordSize()), index);
		IRExpr e2 = new IRBinOp(OpType.ADD, arr, e); 
		return  new IRMem(e2);
	}
}
