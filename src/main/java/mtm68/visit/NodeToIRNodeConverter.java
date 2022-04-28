package mtm68.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMem.MemType;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.binary.And;
import mtm68.ast.nodes.binary.EqEq;
import mtm68.ast.nodes.binary.Or;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.symbol.DispatchVectorClassResolver;
import mtm68.ast.symbol.DispatchVectorIndexResolver;
import mtm68.ast.symbol.ProgramSymbols;
import mtm68.ast.types.ArrayType;
import mtm68.ast.types.BoolType;
import mtm68.ast.types.IntType;
import mtm68.ast.types.ObjectType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;
import mtm68.util.Debug;
import mtm68.util.FreshTempGenerator;

public class NodeToIRNodeConverter extends Visitor {
	
	private String programName;

	private int labelCounter;
		
	private IRNodeFactory inf;
	
	/**
	 * Keys are the function/procedure name as defined in the
	 * AST nodes. Values are the encoded function/procedure 
	 * symbol.
	 */
	private Map<String, String> funcAndProcEncodings;
	
	/**
	 * Keys are the class name as defined in the AST nodes.
	 * Values are a mapping whose keys are the method name as
	 * defined in the AST nodes. Values are the encoded method
	 * symbol.
	 */
	private Map<String, Map<String, String>> objectMethodEncodings;
	
	private DispatchVectorClassResolver dispatchVectorClassResolver;
	
	private DispatchVectorIndexResolver dispatchVectorIndexResolver;
	
	private Map<String, Integer> classNameToNumFields;
	
	private Map<String, IRMem> classNameToDispatchVectorAddr;
	
	private static final String OUT_OF_BOUNDS_LABEL = "_xi_out_of_bounds";

	private static final String MALLOC_LABEL = "_xi_alloc";

	private static final String ALLOC_LAYER = "_I$allocLayer_piiiiii";
	
	private static final int WORD_SIZE = 8;

	
	public NodeToIRNodeConverter(String programName, IRNodeFactory inf) {
		this(programName, inf, new ProgramSymbols());
	}
	
	public NodeToIRNodeConverter(String programName, Map<String, String> funcAndProcEncodings, IRNodeFactory inf) {
		this(programName, funcAndProcEncodings, inf, new ProgramSymbols());
	}
	
	public NodeToIRNodeConverter(String programName, IRNodeFactory inf, ProgramSymbols syms) {
		this(programName, new HashMap<>(), inf, syms);
		saveFuncSymbols(syms.getFuncDecls());
		saveClassSymbols(syms.getClassDecls());
	}
	
	public NodeToIRNodeConverter(String programName, Map<String, String> funcAndProcEncodings, IRNodeFactory inf, ProgramSymbols syms) {
		this.programName = programName;
		this.labelCounter = 0;
		this.funcAndProcEncodings = funcAndProcEncodings;
		this.inf = inf;
		this.objectMethodEncodings = new HashMap<>();
		this.dispatchVectorClassResolver = new DispatchVectorClassResolver(syms);
		this.dispatchVectorIndexResolver = new DispatchVectorIndexResolver(syms);
		this.classNameToNumFields = new HashMap<>();
		this.classNameToDispatchVectorAddr = new HashMap<>();
	}

	public <N extends Node> N performConvertToIR(N root) {
		try {
			return root.accept(this);
		} catch(InternalCompilerError e) {
			if(Debug.DEBUG_ON) {
				e.printStackTrace();
			}
		}
		return root;
	}

	@Override
	public Node leave(Node parent, Node n) {
		return n.convertToIR(this, inf);
	}

	/**
	 * Gets the program name
	 * @return
	 */
	public String getProgramName() {
		return programName;
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
		return FreshTempGenerator.getFreshTemp();
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
		return Constants.RET_PREFIX + retIdx;
	}
	
	public void saveFuncSymbols(List<FunctionDecl> decls) {
		for(FunctionDecl decl : decls) saveFuncSymbol(decl);
	}
	
	public void saveClassSymbols(List<ClassDecl> decls) {
		for(ClassDecl decl : decls) saveClassSymbol(decl);
	}

	public String argVal(int argIdx) {
		return Constants.ARG_PREFIX + argIdx;
	}
	
	/**
	 * Returns an encoding of an object method. The encoding is the same as the
	 * encoding defined for a function or procedure in the Xi ABI except that the
	 * function name is proceeded by the class name and an underscore.
	 * 
	 * Example:
	 * A.f() is encoded as _I_A_f_p
	 * Dog.bite(int) : int is encoded as _I_Dogbite_itoi
	 */
	public String saveAndGetMethodSymbol(FunctionDecl functionDecl, String className) {
		if(!objectMethodEncodings.containsKey(className)) {
			objectMethodEncodings.put(className, new HashMap<>());
		}

		Map<String, String> methods = objectMethodEncodings.get(className);
		if(!methods.containsKey(functionDecl.getId())) {
			String methodNameEncoding = encodeMethodName(className, functionDecl.getId());
			saveFuncSymbol(functionDecl, methodNameEncoding, methods);
		}
		
		return methods.get(functionDecl.getId());
	}
	
	/**
	 * Returns an encoding of a function or procedure
	 * using the encoding defined in the Xi ABI.
	 */
	public String saveAndGetFuncSymbol(FunctionDecl functionDecl) {
		if(!funcAndProcEncodings.containsKey(functionDecl.getId()))
			saveFuncSymbol(functionDecl);
		
		return funcAndProcEncodings.get(functionDecl.getId());
	}

	public void saveFuncSymbol(FunctionDecl functionDecl) {
		saveFuncSymbol(functionDecl, encodeFuncName(functionDecl.getId()), funcAndProcEncodings);
	}
	
	private void saveFuncSymbol(FunctionDecl functionDecl, String encodedFuncName, 
			Map<String, String> encodings) {
		StringBuilder sb = new StringBuilder();
		sb.append("_I");
		sb.append(encodedFuncName);
		sb.append("_");
		
		sb.append(encodeReturnTypes(functionDecl.getReturnTypes()));

		List<Type> argTypes = functionDecl.getArgs()
										  .stream()
										  .map(SimpleDecl::getType)
										  .collect(Collectors.toList());
		sb.append(encodeTypes(argTypes));
	
		String encoded = sb.toString();
		encodings.put(functionDecl.getId(), encoded);
	}

	public void saveClassSymbol(ClassDecl classDecl) {
		List<FunctionDecl> methods = classDecl.getMethodDecls();
		String className = classDecl.getId();
		for(FunctionDecl method : methods) {
			saveAndGetMethodSymbol(method, className);
		}
	}
	
	/**
	 * Returns an encoding of a procedure using the encoding defined in the Xi ABI.
	 * 
	 * @throws InternalCompilerError if the symbol has not yet been defined.
	 */
	public String getFuncSymbol(String funcName) {
		String enc = this.funcAndProcEncodings.get(funcName);
		
		if(enc == null) {
			throw new InternalCompilerError("Failed to  get function symbol: " + funcName);
		}
		
		return enc;
	}
	
	/**
	 * Returns an encoding of a procedure using the encoding defined in the Xi ABI.
	 * 
	 * @throws InternalCompilerError if the symbol has not yet been defined.
	 */
	public String getFuncSymbol(FExpr expr) {
		String enc = this.funcAndProcEncodings.get(expr.getId());
		
		if(enc == null) {
			throw new InternalCompilerError("Failed to  get function symbol: " + expr.getId());
		}
		
		return enc;
	}	
	
	private String encodeMethodName(String className, String methodName) {
		return encodeClassName(className) + "_" + encodeFuncName(methodName);
	}

	private String encodeClassName(String className) {
		return className.replaceAll("_", "__");
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
	 * objects are encoded as "o" concatenated with the length of the unescaped
	 * class name concatenated with the escaped class name
	 * arrays are encoded as {@code a}
	 * 
	 */
	private String encodeType(Type type) {
		if(type instanceof IntType) {
			return "i";
		} else if(type instanceof BoolType) {
			return "b";
		} else if(type instanceof ObjectType) {
			String typeName = ((ObjectType) type).getName();
			String escapedTypeName = encodeClassName(typeName);
			return "o" + typeName.length() + escapedTypeName;
		} else if (type instanceof ArrayType) {
			return "a" + encodeType(((ArrayType) type).getType());
		} else {
			throw new InternalCompilerError("Couldn't encode type: " + type);
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
			return inf.IRJump(inf.IRName(labelToJump));
	}
	
	private IRStmt getCtrlFlow(Not n, String trueLabel, String falseLabel) {
			return getCtrlFlow(n.getExpr(), falseLabel, trueLabel);
	}
	
	private IRStmt getCtrlFlow(And a, String trueLabel, String falseLabel) {
			String l1 = getFreshLabel();
			return inf.IRSeq(
				getCtrlFlow(a.getLeft(), l1, falseLabel),
				inf.IRLabel(l1),
				getCtrlFlow(a.getRight(), trueLabel, falseLabel)
			);
	}

	private IRStmt getCtrlFlow(Or o, String trueLabel, String falseLabel) {
			String l1 = getFreshLabel();
			return inf.IRSeq(
				getCtrlFlow(o.getLeft(), trueLabel , l1),
				inf.IRLabel(l1),
				getCtrlFlow(o.getRight(), trueLabel, falseLabel)
			);
	}
	
	private IRStmt getCtrlFlow(EqEq e, String trueLabel, String falseLabel) {
			IRExpr left = e.getLeft().getIRExpr();
			IRExpr right = e.getRight().getIRExpr();
			return inf.IRCJump(inf.IRBinOp(OpType.EQ, left, right), trueLabel, falseLabel);
	}

	public IRSeq boundsCheck(IRExpr arr, IRExpr index) {
		IRLabel ok = inf.IRLabel(getFreshLabel());
		IRLabel err = inf.IRLabel(getFreshLabel());

		IRMem lenAddr = inf.IRMem(inf.IRBinOp(OpType.SUB, arr, inf.IRConst(getWordSize())));
		IRBinOp boundsCheck = inf.IRBinOp(OpType.ULT, index, lenAddr);

		return inf.IRSeq(
				inf.IRCJump(boundsCheck, ok.name(), err.name()),
				err,
				inf.IRCallStmt(inf.IRName(getOutOfBoundsLabel())),
				ok);
	}

	public IRMem getOffsetIntoArr(IRExpr arr, IRExpr index) {
		return getOffsetIntoArr(arr, index, false);
	}
	
	public IRMem getOffsetIntoArr(IRExpr arr, IRExpr index, boolean isImmutable) {
		/*
		 * index is going to be at mem address: (mem addr of arr) + (WORD_SIZE * index).
		 * We can us the temp's here because it will be executed after
		 * a seq that does the temp setup
		 */
		IRExpr e = inf.IRBinOp(OpType.MUL, inf.IRConst(getWordSize()), index);
		IRExpr e2 = inf.IRBinOp(OpType.ADD, arr, e); 
		if(isImmutable) {
			return  inf.IRMem(e2, MemType.IMMUTABLE);
		} else {
			return  inf.IRMem(e2);
		}
	}

	public IRESeq allocateAndInitArray(List<IRExpr> items) {
	
		IRTemp arrBase = inf.IRTemp(newTemp());
        IRConst sizeOfArrAndLen = inf.IRConst(items.size() * getWordSize() + getWordSize());
        IRName malloc = inf.IRName(getMallocLabel());

        List<IRStmt> seq = ArrayUtils.empty();

        // alloc array and move addr into temp and store length of array
        IRCallStmt allocStmt = inf.IRCallStmt(malloc, ArrayUtils.singleton(sizeOfArrAndLen));
        seq.add(allocStmt);
		seq.add(inf.IRMove(arrBase, inf.IRTemp(retVal(0))));
		seq.add(inf.IRMove(inf.IRMem(arrBase, MemType.IMMUTABLE), inf.IRConst(items.size())));
        
        // put items in their index
        for(int i=0; i < items.size(); i++) {
			IRBinOp offset = inf.IRBinOp(OpType.MUL, new IRConst(i + 1), new IRConst(getWordSize()));
            IRBinOp elem = inf.IRBinOp(OpType.ADD, arrBase, offset); 
            seq.add(inf.IRMove(inf.IRMem(elem), items.get(i)));
        }
        
        IRBinOp startOfArr = inf.IRBinOp(OpType.ADD, arrBase, inf.IRConst(getWordSize()));
        
        return inf.IRESeq(inf.IRSeq(seq), startOfArr);
	}
	
	public IRESeq concatArrays(IRExpr leftArr, IRExpr rightArr) {
		
		IRTemp l1 = inf.IRTemp(newTemp());
		IRTemp l2 = inf.IRTemp(newTemp());
		IRTemp l = inf.IRTemp(newTemp());
		IRTemp startOfArr = inf.IRTemp(newTemp());
		IRTemp ptr = inf.IRTemp(newTemp());
		IRTemp idx = inf.IRTemp(newTemp());
		IRTemp a1 = inf.IRTemp(newTemp());
		IRTemp a2 = inf.IRTemp(newTemp());
		String header = getFreshLabel();
		String fstCmpTrueLabel = getFreshLabel();
		String sndCmpLabel = getFreshLabel();
		String sndCmpTrueLabel = getFreshLabel();
		String done = getFreshLabel();
		
		List<IRStmt> stmts = ArrayUtils.elems(
			inf.IRMove(a1, leftArr),
			inf.IRMove(a2, rightArr),
				
			// Alloc new array
		 	inf.IRMove(l1, inf.IRMem(inf.IRBinOp(OpType.ADD,
		 			a1, inf.IRConst(-1 * getWordSize())))),

		 	inf.IRMove(l2, inf.IRMem(inf.IRBinOp(OpType.ADD,
		 			a2, inf.IRConst(-1 * getWordSize())))),

		 	inf.IRMove(l, inf.IRBinOp(OpType.ADD, l1, l2)),

		 	inf.IRCallStmt(inf.IRName(getMallocLabel()), 
		 			ArrayUtils.singleton(
		 					inf.IRBinOp(OpType.ADD, inf.IRBinOp(OpType.MUL, l,
							inf.IRConst(getWordSize())), inf.IRConst(getWordSize()))
		 			)),
		 	
		 	// Setup index to point to start of new arr
		 	inf.IRMove(ptr, inf.IRTemp(retVal(0))),
		 	inf.IRMove(idx, inf.IRConst(0)),
		 	
			// Move size into new arr
		 	inf.IRMove(inf.IRMem(ptr), l),
		 	inf.IRMove(ptr, inf.IRBinOp(OpType.ADD, ptr, inf.IRConst(getWordSize()))),
		 	
		 	// Save start of arr
		 	inf.IRMove(startOfArr, ptr),

			// Check if idx < l1
		 	inf.IRLabel(header),
		 	inf.IRCJump(inf.IRBinOp(OpType.LT, idx, l1), fstCmpTrueLabel, sndCmpLabel),
		 	
		 	// save at loc ptr the element idx in first array
		 	inf.IRLabel(fstCmpTrueLabel),
		 	inf.IRMove(inf.IRMem(ptr),
		 			inf.IRMem(inf.IRBinOp(OpType.ADD, a1, inf.IRBinOp(OpType.MUL, idx, inf.IRConst(getWordSize()))))),
		 	inf.IRMove(ptr, inf.IRBinOp(OpType.ADD, ptr, inf.IRConst(getWordSize()))),
		 	inf.IRMove(idx, inf.IRBinOp(OpType.ADD, idx, inf.IRConst(1))),
		 	inf.IRJump(inf.IRName(header)),
		 	
		 	// check if idx < l
		 	inf.IRLabel(sndCmpLabel),
		 	inf.IRCJump(inf.IRBinOp(OpType.LT, idx, l), sndCmpTrueLabel, done),
		 	
		 	// save at loc ptr the element idx - l1 in the second arr
		 	inf.IRLabel(sndCmpTrueLabel),
		 	inf.IRMove(inf.IRMem(ptr), inf.IRMem(inf.IRBinOp(OpType.ADD,
		 			a2,
		 			inf.IRBinOp(OpType.MUL, inf.IRBinOp(OpType.SUB, idx, l1), inf.IRConst(getWordSize()))))),
		 	inf.IRMove(ptr, inf.IRBinOp(OpType.ADD, ptr, inf.IRConst(getWordSize()))),
		 	inf.IRMove(idx, inf.IRBinOp(OpType.ADD, idx, inf.IRConst(1))),
		 	inf.IRJump(inf.IRName(sndCmpLabel)),
		 	
		 	inf.IRLabel(done)
		 );

		return inf.IRESeq(inf.IRSeq(stmts), startOfArr);
	}

	public IRSeq allocateExtendedDeclArray(String name, List<IRExpr> indices) {
		// 1. Create temp vars for each index 
		List<IRTemp> idxTemps = indices.stream()
			.map(e -> inf.IRTemp(newTemp()))
			.collect(Collectors.toList());
		
		// 2. Move indices into vars
		List<IRStmt> tempMoves = ArrayUtils.empty(); 
		for(int i = 0; i < indices.size(); i++) {
			IRExpr ei = indices.get(i);
			IRTemp ti = idxTemps.get(i);
			
			tempMoves.add(inf.IRMove(ti, ei));
		}
		
		// 3. e1 < 0 | e2 < 0 | ... | en < 0
		String err = getFreshLabel();
		IRExpr zero = inf.IRConst(0L);

		List<IRStmt> condStmts = ArrayUtils.empty();
		for(int i = 0; i < idxTemps.size(); i++) {
			IRExpr ei = idxTemps.get(i);
			IRExpr lt = inf.IRBinOp(OpType.LT, ei, zero);
			
			IRLabel next = inf.IRLabel(getFreshLabel());
			condStmts.add(inf.IRCJump(lt, err, next.name()));
			
			// After last CJump, add call to error function
			if(i == idxTemps.size() - 1) {
				condStmts.add(inf.IRLabel(err));
				condStmts.add(inf.IRCallStmt(inf.IRName(getOutOfBoundsLabel())));
			}
			condStmts.add(next);
		}
		
		// 4. Calculate offsets (including total size)
		// 1 + n0 + n0(n1 + 1) + n0*n1(n2 + 1) + ...
		
		IRExpr one = inf.IRConst(1L);
		
		List<IRTemp> offsetTemps = ArrayUtils.empty();
		List<IRStmt> offsetStmts = ArrayUtils.empty();
		for(int i = 0; i < idxTemps.size() + 1; i++) {
			IRTemp offTemp = inf.IRTemp(newTemp());
			offsetTemps.add(offTemp);

			if(i == 0) {
				offsetStmts.add(inf.IRMove(offTemp, zero));
				continue;
			}
			
			if(i == 1) {
				// 1 + n1
				IRExpr off = inf.IRBinOp(OpType.ADD, inf.IRConst(1L), idxTemps.get(0));
				offsetStmts.add(inf.IRMove(offTemp, off));
				continue;
			}
			
			IRExpr prevOff = offsetTemps.get(i - 1);
			IRExpr inner = inf.IRBinOp(OpType.ADD, idxTemps.get(i - 1), one);
			
			IRExpr mult = null;
			for(int j = 0; j < i - 1; j++) {
				IRExpr idxSize = idxTemps.get(j);
				if(j == 0) mult = idxSize; 
				else mult = inf.IRBinOp(OpType.MUL, mult, idxSize);
			}
			mult = inf.IRBinOp(OpType.MUL, mult, inner);
			
			IRExpr off = inf.IRBinOp(OpType.ADD, prevOff, mult);
			offsetStmts.add(inf.IRMove(offTemp, off));
		}
		
		// 5. Calculate size of each layer
		List<IRStmt> sizeStmts = ArrayUtils.empty();
		List<IRTemp> sizeTemps = ArrayUtils.empty();
		
		for(int i = 1; i < offsetTemps.size(); i++) {
			IRTemp offTemp = offsetTemps.get(i);
			IRTemp offPrevTemp = offsetTemps.get(i - 1);
			IRExpr size = inf.IRBinOp(OpType.SUB, offTemp, offPrevTemp); 
			
			IRTemp sizeTemp = inf.IRTemp(newTemp());
			sizeTemps.add(sizeTemp);
			
			sizeStmts.add(inf.IRMove(sizeTemp, size));
		}
		
		// 6. Allocate array and extract base pointer from return value
		IRTemp sizeTemp = offsetTemps.remove(offsetTemps.size() - 1);
		IRExpr word = inf.IRConst(getWordSize());
	   IRCallStmt allocStmt = inf.IRCallStmt(
	   		inf.IRName(getMallocLabel()), 
	   		ArrayUtils.singleton(mul(word, sizeTemp)));
	   
	   IRTemp basePtr = genTemp();
	   IRStmt saveBasePtr = inf.IRMove(basePtr, inf.IRTemp(retVal(0)));
	   IRStmt baseArrayLengthStmt = inf.IRMove(inf.IRMem(basePtr), idxTemps.get(0));

	   // 7. Fill in values for the arrays except for the last level
	   List<IRStmt> allocLayerStmts = ArrayUtils.empty();
	   for(int i = 0; i < indices.size() - 1; i++) {
	   	List<IRExpr> args = ArrayUtils.elems(
	   			idxTemps.get(i),
	   			idxTemps.get(i + 1),
	   			sizeTemps.get(i),
	   			offsetTemps.get(i),
	   			offsetTemps.get(i + 1),
	   			basePtr);

	   	allocLayerStmts.add(inf.IRCallStmt(inf.IRName(ALLOC_LAYER), args));
	   }
	   
	   // 8. Move basePtr + 8 into our id
	   IRMove saveArrPtrStmt = inf.IRMove(inf.IRTemp(newTemp(name)), add(basePtr, word));
		
		List<IRStmt> result = ArrayUtils.empty();
		result.addAll(tempMoves);
		result.addAll(condStmts);
		result.addAll(offsetStmts);
		result.addAll(sizeStmts);
		result.add(allocStmt);
		result.add(saveBasePtr);
		result.add(baseArrayLengthStmt);
		result.addAll(allocLayerStmts);
		result.add(saveArrPtrStmt);
		
		return inf.IRSeq(result);
	}
	
	/**
	 * Argument list
	 * -------------
	 * _ARG0: index value in current layer
	 * _ARG1: index value in next layer
	 * _ARG2: layer size 
	 * _ARG3: offset of current layer 
	 * _ARG4: offset of next layer 
	 * _ARG5: base pointer of array 
	 * 
	 * @return
	 */
	public IRFuncDefn allocLayer() {
		List<IRStmt> stmts = ArrayUtils.empty();
		
		// Temps
		IRTemp iTemp = genTemp();
		IRTemp bTemp = genTemp();
		IRTemp idxTemp = genTemp();
		IRTemp blockSizeTemp = genTemp();
		IRTemp layerSizeTemp = genTemp();
		IRTemp currIdxTemp = genTemp();
		IRTemp nextIdxTemp = genTemp();
		IRTemp currOffTemp = genTemp();
		IRTemp nextOffTemp = genTemp();
		IRTemp baseTemp = genTemp();
		IRTemp ptrTemp = genTemp();
		IRTemp ptrMemTemp = genTemp();
		
		// Labels
		IRLabel headerLabel = inf.IRLabel("header");
		IRLabel okLabel = inf.IRLabel("ok");
		IRLabel continueLabel = inf.IRLabel("continue");
		IRLabel afterLabel = inf.IRLabel("after");
		IRLabel fallthroughLabel = inf.IRLabel("fallthrough");
		IRLabel doneLabel = inf.IRLabel("done");

		// Constants
		IRConst word = inf.IRConst(getWordSize());
		IRConst one = inf.IRConst(1L);
		IRConst zero = inf.IRConst(0L);

		// Move args into temps
		stmts.add(inf.IRMove(currIdxTemp, inf.IRTemp(argVal(0))));
		stmts.add(inf.IRMove(nextIdxTemp, inf.IRTemp(argVal(1))));
		stmts.add(inf.IRMove(layerSizeTemp, inf.IRTemp(argVal(2))));
		stmts.add(inf.IRMove(currOffTemp, inf.IRTemp(argVal(3))));
		stmts.add(inf.IRMove(nextOffTemp, inf.IRTemp(argVal(4))));
		stmts.add(inf.IRMove(baseTemp, inf.IRTemp(argVal(5))));
		
		// Setup vars
		stmts.add(inf.IRMove(iTemp, zero));
		stmts.add(inf.IRMove(bTemp, zero));
		stmts.add(inf.IRMove(blockSizeTemp, add(currIdxTemp, one)));
		
		// Begin loop
		stmts.add(headerLabel);
		
		stmts.add(inf.IRMove(idxTemp, add(iTemp, mul(bTemp, blockSizeTemp))));
		stmts.add(inf.IRCJump(lt(idxTemp, layerSizeTemp), okLabel.name(), doneLabel.name()));
		stmts.add(okLabel);
		
		stmts.add(inf.IRCJump(eq(iTemp, zero), afterLabel.name(), continueLabel.name()));
		
		stmts.add(continueLabel);
		
		IRExpr sectionOff = mul(bTemp, mul(currIdxTemp, add(nextIdxTemp, one)));
		IRExpr blockOff = mul(sub(iTemp, one), add(nextIdxTemp, one));
		
		stmts.add(inf.IRMove(ptrTemp, add(sectionOff, add(blockOff, nextOffTemp))));
		stmts.add(inf.IRMove(ptrMemTemp, add(baseTemp, mul(word, ptrTemp))));
		stmts.add(inf.IRMove(inf.IRMem(ptrMemTemp), nextIdxTemp));
		
		IRExpr addr = add(baseTemp, mul(word, add(idxTemp, currOffTemp)));
		stmts.add(inf.IRMove(inf.IRMem(addr), add(ptrMemTemp, word)));
		
		stmts.add(afterLabel);
		stmts.add(inf.IRMove(iTemp, add(iTemp, one)));
		stmts.add(inf.IRCJump(lt(iTemp, blockSizeTemp), headerLabel.name(), fallthroughLabel.name()));

		stmts.add(fallthroughLabel);
		stmts.add(inf.IRMove(iTemp, zero));
		stmts.add(inf.IRMove(bTemp, add(bTemp, one)));
		stmts.add(inf.IRJump(inf.IRName(headerLabel.name())));
		
		stmts.add(doneLabel);
		stmts.add(inf.IRReturn());
		
		return inf.IRFuncDefn(ALLOC_LAYER, inf.IRSeq(stmts), 6);
	}

	private IRBinOp eq(IRExpr left, IRExpr right) {
		return inf.IRBinOp(OpType.EQ, left, right);
	}
	
	private IRBinOp lt(IRExpr left, IRExpr right) {
		return inf.IRBinOp(OpType.LT, left, right);
	}
	
	private IRBinOp add(IRExpr left, IRExpr right) {
		return inf.IRBinOp(OpType.ADD, left, right);
	}
	
	private IRBinOp sub(IRExpr left, IRExpr right) {
		return inf.IRBinOp(OpType.SUB, left, right);
	}

	private IRBinOp mul(IRExpr left, IRExpr right) {
		return inf.IRBinOp(OpType.MUL, left, right);
	}
	
	private IRTemp genTemp() {
		return inf.IRTemp(newTemp());
	}

	public IRSeq constructFuncDefnSeq(FunctionDecl functionDecl, Block body) {
		List<IRStmt> stmts = new ArrayList<>();
		
		List<SimpleDecl> args = functionDecl.getArgs();
		for(int i = 0; i < args.size(); i++) {
			String tempName = newTemp(args.get(i).getId());
			stmts.add(inf.IRMove(new IRTemp(tempName), new IRTemp(argVal(i))));
		}
		
		stmts.add(body.getIRStmt());
		if(body.getResult() == Result.UNIT) stmts.add(new IRReturn());
		
		return inf.IRSeq(stmts);
	}
	
	public IRESeq convertNonCtrlFlowBoolean(Expr condition) {
		String trueLabel = getFreshLabel();
		String falseLabel = getFreshLabel();
		String afterElse = getFreshLabel();
		IRTemp resultTemp = inf.IRTemp(FreshTempGenerator.getFreshTemp());
		
		List<IRStmt> stmts = ArrayUtils.elems(
					getCtrlFlow(condition, trueLabel, falseLabel),
					inf.IRLabel(trueLabel),
					inf.IRMove(resultTemp, inf.IRConst(1L)),
					inf.IRJump(inf.IRName(afterElse)),
					inf.IRLabel(falseLabel),
					inf.IRMove(resultTemp, inf.IRConst(0L))
				);
		
		
		stmts.add(inf.IRLabel(afterElse));
		IRSeq seq = inf.IRSeq(stmts);
		
		return inf.IRESeq(seq, resultTemp);
	}
	
	public IRESeq constructDispatchVector(ClassDefn defn) {
		String className = defn.getId();
		Map<String, String> methods = dispatchVectorClassResolver.getMethods(className);

		List<IRExpr> elems = ArrayUtils.empty();
		for(String funcId : methods.keySet()) {
			String invokeClassName = methods.get(funcId);
			String funcNameEncoded = encodeMethodName(invokeClassName, funcId);
			
			// Encode the encoded function name as a char[]
			List<IRExpr> string = ArrayUtils.stringToCharList(funcNameEncoded)
					.stream()
					.map(ch -> inf.IRConst(ch))
					.collect(Collectors.toList());
			IRESeq eseq = allocateAndInitArray(string);
			
			// Place it into the correct spot in the sequence based on the index
			// we calculated it to be at.
			int index = dispatchVectorIndexResolver.getMethodIndex(className, funcId);
			elems.add(index, eseq);
		}

		IRESeq eseq = allocateAndInitArray(elems);
		classNameToDispatchVectorAddr.put(defn.getId(), getOffsetIntoArr(eseq, inf.IRConst(0), true));
		return eseq;
	}
	
	public void saveNumFields(ClassDefn defn) {
		classNameToNumFields.put(defn.getId(), defn.getBody().getFields().size());
	}

	public int getNumFields(String className) {
		if(classNameToNumFields.containsKey(className)) {
			return classNameToNumFields.get(className);
		} else {
			throw new InternalCompilerError("Could not find class " + className + " when searching for number of fields.");
		}
	}

	public IRMem getDispatchVectorAddr(String className) {
		if(classNameToDispatchVectorAddr.containsKey(className)) {
			return classNameToDispatchVectorAddr.get(className);
		} else {
			throw new InternalCompilerError("Could not find class " + className + " when searching for dispatch vector address.");
		}
	}

	/**
	 * A function that initializes all dispatch vectors.
	 */
	public IRFuncDefn dispatchVectorInit() {
		List<IRStmt> stmts = ArrayUtils.empty();
		return null;
	}
}
