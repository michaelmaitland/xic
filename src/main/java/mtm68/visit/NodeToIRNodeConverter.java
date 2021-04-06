package mtm68.visit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
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
import mtm68.ast.types.ArrayType;
import mtm68.ast.types.BoolType;
import mtm68.ast.types.IntType;
import mtm68.ast.types.Type;
import mtm68.exception.FatalTypeException;
import mtm68.exception.InternalCompilerException;
import mtm68.util.ArrayUtils;

public class NodeToIRNodeConverter extends Visitor {

	private int labelCounter;
	
	private int tmpCounter;
	
	private IRNodeFactory irFactory;
	
	/**
	 * Keys are the function/procedure name as defined in the
	 * AST nodes. Values are the encoded function/procedure 
	 * symbol.
	 */
	private Map<String, String> funcAndProcEncodings;
	
	private static final String OUT_OF_BOUNDS_LABEL = "_xi__out_of_bounds";

	private static final String MALLOC_LABEL = "_xi_alloc";
	
	private static final int WORD_SIZE = 8;
	
	public NodeToIRNodeConverter(IRNodeFactory irFactory) {
		this(new HashMap<>(), irFactory);
	}
	
	public NodeToIRNodeConverter(Map<String, String> funcAndProcEncodings, IRNodeFactory irFactory) {
		this.labelCounter = 0;
		this.tmpCounter = 0;
		this.funcAndProcEncodings = funcAndProcEncodings;
		this.irFactory = irFactory;
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
		return n.convertToIR(this, irFactory);
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
	public String getFreshLabel() {
		labelCounter++;
		return "_l" + labelCounter; 
	}
	
	/**
	 * @return a temp that does not need to be used by a different node.
	 */
	public String newTemp() {
		tmpCounter++;
		return "_t" + tmpCounter;
	}
	
	/**
	 * Returns a temp that is built using a name. This allows different nodes to
	 * reference the same temp.
	 * 
	 * @param name the identifier that the temp is assocaiated with 
	 * @return the temp 
	 */
	public String newTemp(String name) {
		return "_t" + name;
	}

	public String retVal(int retIdx) {
		return "RET_" + retIdx;
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
			return "a" + encodeType(((ArrayType) type).getType());
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
			return irFactory.IRJump(irFactory.IRName(labelToJump));
	}
	
	private IRStmt getCtrlFlow(Not n, String trueLabel, String falseLabel) {
			return getCtrlFlow(n.getExpr(), falseLabel, trueLabel);
	}
	
	private IRStmt getCtrlFlow(And a, String trueLabel, String falseLabel) {
			String l1 = getFreshLabel();
			return irFactory.IRSeq(
				getCtrlFlow(a.getLeft(), l1, falseLabel),
				irFactory.IRLabel(l1),
				getCtrlFlow(a.getRight(), trueLabel, falseLabel)
			);
	}

	private IRStmt getCtrlFlow(Or o, String trueLabel, String falseLabel) {
			String l1 = getFreshLabel();
			return irFactory.IRSeq(
				getCtrlFlow(o.getLeft(), trueLabel , l1),
				irFactory.IRLabel(l1),
				getCtrlFlow(o.getRight(), trueLabel, falseLabel)
			);
	}
	
	private IRStmt getCtrlFlow(EqEq e, String trueLabel, String falseLabel) {
			IRExpr left = e.getLeft().getIRExpr();
			IRExpr right = e.getRight().getIRExpr();
			return irFactory.IRCJump(irFactory.IRBinOp(OpType.EQ, left, right), trueLabel, falseLabel);
	}

	public IRSeq boundsCheck(IRExpr arr, IRExpr index) {
		IRLabel ok = irFactory.IRLabel(getFreshLabel());
		String errLabel = getOutOfBoundsLabel();

		IRMem lenAddr = irFactory.IRMem(irFactory.IRBinOp(OpType.SUB, arr, irFactory.IRConst(getWordSize())));
		IRBinOp boundsCheck = irFactory.IRBinOp(OpType.ULT, index, lenAddr);

		return irFactory.IRSeq(irFactory.IRCJump(boundsCheck, ok.name(), errLabel),
						 ok);
	}

	public IRMem getOffsetIntoArr(IRExpr arr, IRExpr index) {
		/*
		 * index is going to be at mem address: (mem addr of arr) + (WORD_SIZE * index).
		 * We can us the temp's here because it will be executed after
		 * a seq that does the temp setup
		 */
		IRExpr e = irFactory.IRBinOp(OpType.MUL, irFactory.IRConst(getWordSize()), index);
		IRExpr e2 = irFactory.IRBinOp(OpType.ADD, arr, e); 
		return  irFactory.IRMem(e2);
	}

	public IRESeq allocateAndInitArray(List<IRExpr> items) {
	
		IRTemp arrBase = irFactory.IRTemp(newTemp());
        IRConst sizeOfArrAndLen = irFactory.IRConst(items.size() * getWordSize() + getWordSize());
        IRName malloc = irFactory.IRName(getMallocLabel());

        List<IRStmt> seq = ArrayUtils.empty();

        // alloc array and move addr into temp and store length of array
		seq.add(irFactory.IRMove(arrBase, new IRCall(malloc, sizeOfArrAndLen)));
		seq.add(irFactory.IRMove(irFactory.IRMem(arrBase), irFactory.IRConst(items.size())));
        
        // put items in their index
        for(int i=0; i < items.size(); i++) {
			IRBinOp offset = irFactory.IRBinOp(OpType.MUL, new IRConst(i), new IRConst(getWordSize()));
            IRBinOp elem = irFactory.IRBinOp(OpType.ADD, arrBase, offset); 
            seq.add(irFactory.IRMove(irFactory.IRMem(elem), items.get(i)));
        }
        
        IRBinOp startOfArr = irFactory.IRBinOp(OpType.ADD, arrBase, irFactory.IRConst(getWordSize()));
        
        return irFactory.IRESeq(irFactory.IRSeq(seq), startOfArr);
	}
}
