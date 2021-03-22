package mtm68.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.ArrayLength;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.HasLocation;
import mtm68.ast.nodes.Negate;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.BinExpr;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.Decl;
import mtm68.ast.nodes.stmts.FunctionCall;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.While;
import mtm68.ast.types.ArrayType;
import mtm68.ast.types.ContextType;
import mtm68.ast.types.HasResult;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
import mtm68.ast.types.TypingContext;
import mtm68.exception.BaseError;
import mtm68.exception.FatalTypeException;
import mtm68.exception.SemanticError;

public class TypeChecker extends Visitor {
	TypingContext context;
	
	private List<SemanticError> typeErrors;

	public TypeChecker(Map<String, ContextType> initSymTable) {
		this(new TypingContext(initSymTable));
	}

	public TypeChecker(TypingContext context) {
		this.context = context;
		typeErrors = new ArrayList<>();
	}
	
	public TypeChecker() {
		this.context = new TypingContext();
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
	public Visitor enter(Node n) {
		if(isScopeNode(n)) context.enterScope();
		if(n instanceof FunctionDefn) checkFuncBindings((FunctionDefn) n);

		return this;
	}

	@Override
	public Node leave(Node n, Node old) {
		// TODO: if n == old, we need to make a copy of n before modifying it. It should then return the modified copy
		if(isScopeNode(n)) context.leaveScope();

		return n.typeCheck(this);
	}

	public void typeCheck(HasType actual, Type expected) {
		if(!expected.equals(actual.getType())){
			reportError(actual, "Expected type: " + expected + ", but got: " + actual.getType());
		}
	}
	
	public <T extends HasType> void checkTypes(Node base, List<T> actual, List<Type> expected) {
		if(expected.size() != actual.size()) {
			reportError(base, "Type lists don't match in size");
			return;
		}
		
		for(int i = 0; i < actual.size(); i++) {
			typeCheck(actual.get(i), expected.get(i));
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
			typeCheck(retTypes.get(i), expected.get(i));
		}
	}
	
	public void checkDecl(Decl decl) {
		if(context.isDefined(decl.getId())) { 
			reportError(decl, "Identifier \"" + decl.getId() + "\" is already bound in scope");
			throw new FatalTypeException();
		}
		context.addIdBinding(decl.getId(), decl.getType());
	}
	
	public void checkFuncBindings(FunctionDefn fDefn) {
//		List<SimpleDecl> decls = fDefn.getFunctionDecl().getArgs();
//		for(Decl decl : decls) {
//			checkDecl(decl);
//		}
		context.addReturnTypeInScope(fDefn.getFunctionDecl().getReturnTypes());
	}
	
	public void checkProcCall(FunctionCall stmt) {
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
		typeCheck(arrayIndex.getIndex(), Types.INT);
		
		Type arrType = arrayIndex.getArr().getType();
		
		if(!(arrType instanceof ArrayType)) {
			reportError(arrayIndex, "Must index into an array");
			throw new FatalTypeException();
		}

		return ((ArrayType)arrType).getType();
	}
	
	public Type checkArrayInit(ArrayInit arrayInit) {
		
		Type expected;
		if(arrayInit.getItems().isEmpty()) {
			expected = null;
		} else {
			expected = arrayInit.getItems().get(0).getType();
		}

		checkTypes(arrayInit, arrayInit.getItems(), expected);
		return Types.ARRAY(expected);
	}

	public void checkArrayLength(ArrayLength arrayLength) {
		if(!(arrayLength.getExp().getType() instanceof ArrayType)) {
			reportError(arrayLength, "Length takes an argument of type Array.");
		}
	}
	
	public void checkNegate(Negate negate) {
		typeCheck(negate.getExpr(), Types.INT);
	}

	public void checkNot(Not not) {
		typeCheck(not.getExpr(), Types.BOOL);
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
		}
		return type;
	}

	public Type checkBinExpr(BinExpr be) {
		typeCheck(be.getLeft(), be.getOp().getExpectedType());
		typeCheck(be.getRight(), be.getOp().getExpectedType());
		
		return be.getOp().getReturnType();
	}

	public List<SemanticError> getTypeErrors() {
		return typeErrors;
	}

	public SemanticError getFirstError() {
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
				|| node instanceof If
				|| node instanceof While
				|| node instanceof FunctionDefn;
	}

}
