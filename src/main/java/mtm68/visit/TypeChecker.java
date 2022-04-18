package mtm68.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.ArrayLength;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Negate;
import mtm68.ast.nodes.New;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.This;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.BinExpr;
import mtm68.ast.nodes.binary.Binop;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.Decl;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.ProcedureCall;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.While;
import mtm68.ast.symbol.SymbolTable;
import mtm68.ast.types.ArrayType;
import mtm68.ast.types.EmptyArrayType;
import mtm68.ast.types.HasResult;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
import mtm68.ast.types.TypingContext;
import mtm68.exception.BaseError;
import mtm68.exception.FatalTypeException;
import mtm68.exception.SemanticError;
import mtm68.util.ArrayUtils;

public class TypeChecker extends Visitor {

	private TypingContext context;
	
	private List<BaseError> typeErrors;

	// Sets described in the typing rules
	private	Set<Binop> intToIntToInt = ArrayUtils.newHashSet(Binop.ADD, Binop.SUB, Binop.MULT, Binop.HIGH_MULT, Binop.DIV, Binop.MOD);
	private	Set<Binop> intToIntToBool = ArrayUtils.newHashSet(Binop.EQEQ, Binop.NEQ, Binop.LT, Binop.LEQ, Binop.GT, Binop.GEQ);
	private	Set<Binop> boolToBoolToBool = ArrayUtils.newHashSet(Binop.EQEQ, Binop.NEQ, Binop.AND, Binop.OR);
	private	Set<Binop> arrToArrToBool = ArrayUtils.newHashSet(Binop.EQEQ, Binop.NEQ);
	private	Set<Binop> arrToArrToArr = ArrayUtils.newHashSet(Binop.ADD);


	public TypeChecker(SymbolTable symTable) {
		this(new TypingContext(symTable));
	}

	public TypeChecker(TypingContext context) {
		this.context = context;
		typeErrors = new ArrayList<>();
	}
	
	public TypeChecker() {
		this(new TypingContext());
		typeErrors = new ArrayList<>();
	}
	
	public <N extends Node> N performTypeCheck(N root) {
		try {
			return root.accept(this);
		} catch(FatalTypeException e) {
		}
		return root;
	}

	@Override
	public Visitor enter(Node parent, Node n) {
		if(isScopeNode(n) || parent instanceof If) context.enterScope();

		if(n instanceof FunctionDefn) addFuncReturn((FunctionDefn) n);

		return this;
	}

	@Override
	public Node leave(Node parent, Node n) {
		if(isScopeNode(n)) context.leaveScope();
		
		n = n.typeCheck(this);

		// Help me
		if(parent instanceof If && !isScopeNode(n)) context.leaveScope();

		return n;
	}

	public void checkType(HasType actual, Type expected) {
		if(!isEqualTypes(actual.getType(), expected)){
			reportError(actual, "Expected type: " + expected + ", but got: " + actual.getType());
		}
	}

	public void checkSubtype(Node base, Type subType, Type superType) {
		if(subType.equals(superType) || superType.equals(Types.UNIT)) return;

		reportError(base, subType + " is not a subtype of " + superType);
	}

	public void checkSubtypes(Node base, List<Type> subTypes, List<Type> superTypes) {
		if(subTypes.size() != superTypes.size()) {
			reportError(base, "Size mismatch in type vectors");
			return;
		}
		
		for(int i = 0; i < subTypes.size(); i++) {
			checkSubtype(base, subTypes.get(i), superTypes.get(i));
		}
	}
	
	public boolean isEqualTypes(Type t1, Type t2) {
		return t1.equals(t2)
				|| (Types.isArray(t1) && Types.isArray(t2) && isCompatibleArrayTypes(t1, t2));
	}
	
	public <T extends HasType> void checkTypes(Node base, List<T> actual, List<Type> expected) {
		if(expected.size() != actual.size()) {
			reportError(base, "Type lists don't match in size");
			return;
		}
		
		for(int i = 0; i < actual.size(); i++) {
			checkType(actual.get(i), expected.get(i));
		}	
	}

	public <T extends HasType> void checkTypes(Node base, List<T> items, Type expected) {
		List<Type> expectedList = items.stream()
									   .map(i -> expected)
									   .collect(Collectors.toList());
		checkTypes(base, items, expectedList);
	}
	
	public void checkResultIsUnit(HasResult result) {
		if(result.getResult() != Result.UNIT) {
			reportError(result, "Statement cannot return here");
		}
	}
	
	public <T extends HasType> void checkReturn(Return ret, List<T> retTypes) {
		List<Type> expected = context.getReturnTypeInScope();
		
		if(expected.size() != retTypes.size()) {
			reportError(ret, "Mismatch on number of expressions to return from return statement");
			return;
		}
		
		for(int i = 0; i < retTypes.size(); i++) {
			checkType(retTypes.get(i), expected.get(i));
		}
	}
	
	public void checkDecl(Decl decl) {
		if(context.isDefined(decl.getId())) { 
			reportError(decl, "Identifier \"" + decl.getId() + "\" is already bound in scope");
			throw new FatalTypeException();
		}
		context.addIdBinding(decl.getId(), decl.getType());
	}
	
	public void addFuncReturn(FunctionDefn fDefn) {
		context.addReturnTypeInScope(fDefn.getFunctionDecl().getReturnTypes());
	}
	
	public void checkProcCall(ProcedureCall stmt) {
		FExpr fexp = stmt.getFexp();
		
		String id = fexp.getId();
		checkFunctionDecl(stmt, id);
		checkFunctionArgs(stmt, id, fexp.getArgs());
		checkAndGetFunctionReturn(stmt, id, true);
	}

	public Type checkFunctionCall(FExpr fexp) {
		String id = fexp.getId();
		checkFunctionDecl(fexp, id);
		checkFunctionArgs(fexp, id, fexp.getArgs());
		return checkAndGetFunctionReturn(fexp, id, false);
	}


	private void checkFunctionDecl(Node base, String id) {
		if(!context.isFunctionDecl(id)) {
			reportError(base, "Identifier \"" + id + "\" is not a valid function");
			throw new FatalTypeException();
		}
	}

	private void checkFunctionArgs(Node base, String id, List<Expr> args) {
		List<Type> argTys = context.getArgTypes(id);
		checkTypes(base, args, argTys);
	}

	private Type checkAndGetFunctionReturn(Node base, String id, boolean isProc) {
		Type retTy = Types.TVEC(context.getReturnTypes(id));
		
		if(!isProc && retTy.equals(Types.UNIT)) {
			reportError(base, "Function can't return unit");
		} else if(isProc && !retTy.equals(Types.UNIT)) {
			reportError(base, "Procedure cannot have return type");
		}
		
		return retTy;
	}
	
	public void checkFunctionResult(FunctionDefn fDefn) {
		Type retTy = Types.TVEC(fDefn.getFunctionDecl().getReturnTypes());
		boolean isProc = retTy.equals(Types.UNIT);
		
		if(!isProc && fDefn.getBody().getResult().equals(Result.UNIT)){
			reportError(fDefn, "Function body must have void result.");
		}
	}

	public Type checkArrayIndex(ArrayIndex arrayIndex) {
		checkType(arrayIndex.getIndex(), Types.INT);
		
		Type arrType = arrayIndex.getArr().getType();
		
		if(!(arrType instanceof ArrayType)) {
			reportError(arrayIndex, "Must index into an array");
			throw new FatalTypeException();
		}

		return ((ArrayType)arrType).getType();
	}
	
	public Type checkArrayInit(ArrayInit arrayInit) {
		
		if(arrayInit.getItems().isEmpty()) {
			return Types.EMPTY_ARRAY;
		}

		Type expected = arrayInit.getItems().get(0).getType();
		checkTypes(arrayInit, arrayInit.getItems(), expected);
		return Types.ARRAY(expected);
	}

	public void checkArrayLength(ArrayLength arrayLength) {
		if(arrayLength.getExp().getType() instanceof EmptyArrayType) return;

		if(!(arrayLength.getExp().getType() instanceof ArrayType)) {
			reportError(arrayLength, "Length takes an argument of type Array.");
		}
	}
	
	public void checkNegate(Negate negate) {
		checkType(negate.getExpr(), Types.INT);
	}

	public void checkNot(Not not) {
		checkType(not.getExpr(), Types.BOOL);
	}

	/**
	 * Gets the type of the var from the context
	 * @param v The var 
	 * @return The type of var if it exists in the context, null otherwise.
	 */
	public Type checkVar(Var v) {
		Type type = context.getIdType(v.getId());
		
		if(type == null) {
			reportError(v, "Variable used before declaration.");
			throw new FatalTypeException();
		}

		return type;
	}

	public Type checkBinExpr(BinExpr be) {
		
		Binop op = be.getOp();
		Type leftType = be.getLeft().getType();
		Type rightType  = be.getRight().getType();

		if(checkIntToIntToInt(op, leftType, rightType)) {
			return Types.INT;

		} else if(checkIntToIntToBool(op, leftType, rightType)) {
			return Types.BOOL;

		} else if (checkBoolToBoolToBool(op, leftType, rightType)) {
			return Types.BOOL;

		} else if (checkArrToArrToBool(op, leftType, rightType)) {
			return Types.BOOL;

		} else if (checkArrToArrToArr(op, leftType, rightType)) {
			return Types.getLeastUpperBound(leftType, rightType);

		} else {
			reportError(be, "Cannot apply operator " + op.toString() + " to types "
							+ be.getLeft().getType() + " and "
							+ be.getRight().getType());
			throw new FatalTypeException();
		}
	}
	
	private boolean  checkIntToIntToInt(Binop op, Type t1, Type t2) {
		return intToIntToInt.contains(op) 
				&& t1.equals(Types.INT)
				&& t2.equals(Types.INT);
	}
	
	private boolean checkIntToIntToBool(Binop op, Type t1, Type t2) {
		return intToIntToBool.contains(op) 
				&& t1.equals(Types.INT) 
				&& t2.equals(Types.INT);
	}
	
	private boolean checkBoolToBoolToBool(Binop op, Type t1, Type t2) {
	return boolToBoolToBool.contains(op) 
				&& t1.equals(Types.BOOL) 
				&& t2.equals(Types.BOOL);
	}
	
	private boolean checkArrToArrToBool(Binop op, Type t1, Type t2) {
		return arrToArrToBool.contains(op) 
				&& Types.isArray(t1)
				&& Types.isArray(t2)
				&& isCompatibleArrayTypes(t1, t2);
	}
	
	private boolean checkArrToArrToArr(Binop op, Type t1, Type t2) {
		return arrToArrToArr.contains(op)
				&& Types.isArray(t1)
				&& Types.isArray(t2)
				&& isCompatibleArrayTypes(t1, t2);
	}

	public List<BaseError> getTypeErrors() {
		return typeErrors;
	}

	public BaseError getFirstError() {
		typeErrors.sort(BaseError.getComparator());
		return typeErrors.get(0);
	}
	
	public boolean hasError() {
		return typeErrors.size() > 0;
	}
	
	public void reportError(HasLocation location, String description) {
		typeErrors.add(new SemanticError(location, description));
	}
	
	private boolean isScopeNode(Node node) {
		return node instanceof Block
				|| node instanceof While
				|| node instanceof FunctionDefn;
	}
	
	private boolean isCompatibleArrayTypes(Type t1, Type t2) {
		return t1.equals(t2) || t1.equals(Types.EMPTY_ARRAY) || t2.equals(Types.EMPTY_ARRAY);
	}

	public Type checkThis(This this1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Type checkNew(New new1) {
		// TODO Auto-generated method stub
		return null;
	}

}
