package mtm68.types;

import static java.util.Optional.of;
import static mtm68.ast.types.Types.ARRAY;
import static mtm68.ast.types.Types.BOOL;
import static mtm68.ast.types.Types.EMPTY_ARRAY;
import static mtm68.ast.types.Types.INT;
import static mtm68.ast.types.Types.TVEC;
import static mtm68.ast.types.Types.addArrayDims;
import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static mtm68.util.ArrayUtils.singleton;
import static mtm68.util.NodeTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.ArrayLength;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Negate;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Not;
import mtm68.ast.nodes.StringLiteral;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.binary.BinExpr;
import mtm68.ast.nodes.binary.EqEq;
import mtm68.ast.nodes.binary.Mult;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.ExtendedDecl;
import mtm68.ast.nodes.stmts.FunctionCall;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.ast.nodes.stmts.While;
import mtm68.ast.types.DeclType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
import mtm68.ast.types.TypingContext;
import mtm68.util.ArrayUtils;
import mtm68.visit.TypeChecker;



public class TypeCheckerTests {

	//-------------------------------------------------------------------------------- 
	// ArrayIndex
	//-------------------------------------------------------------------------------- 

	@Test
	void arrayIndexWithIntArrayIsTypeInt() {
		ArrayIndex ai = new ArrayIndex(new StringLiteral("hi"), intLit(0L));
		ArrayIndex newAi = doTypeCheck(ai);
		
		assertEquals(INT, newAi.getType());
	}

	@Test
	void arrayIndexWithNoArray() {
		ArrayIndex ai = new ArrayIndex(arbitraryCondition(), intLit(0L));
		assertTypeCheckError(ai);
	}

	@Test
	void arrayIndexWithNoIntIndex() {
		ArrayIndex ai = new ArrayIndex(new StringLiteral("hi"), arbitraryCondition());
		assertTypeCheckError(ai);
	}

	//-------------------------------------------------------------------------------- 
	// ArrayInit
	//-------------------------------------------------------------------------------- 

	@Test
	void arrayInitEmptyArray() {
		List<Expr> items = new ArrayList<>();
		ArrayInit ai = new ArrayInit(items);
		ArrayInit newAi = doTypeCheck(ai);
		
		assertEquals(EMPTY_ARRAY,  newAi.getType());
	}

	@Test
	void arrayInitArrayWithSameTypeElems() {
		List<Expr> items = elems(intLit(0L), intLit(1L));
		ArrayInit ai = new ArrayInit(items);
		ArrayInit newAi = doTypeCheck(ai);
		
		assertEquals(ARRAY(INT), newAi.getType());
	}

	@Test
	void arrayInitArrayWithOneElem() {
		List<Expr> items = elems(intLit(1L));
		ArrayInit ai = new ArrayInit(items);
		ArrayInit newAi = doTypeCheck(ai);
		
		assertEquals(ARRAY(INT), newAi.getType());
	}

	@Test
	void arrayInitArrayWithDiffTypeElems() {
		List<Expr> items = elems(intLit(1L), arbitraryCondition());
		ArrayInit ai = new ArrayInit(items);

		assertTypeCheckError(ai);
	}

	//-------------------------------------------------------------------------------- 
	// ArrayLength
	//-------------------------------------------------------------------------------- 
	
	@Test
	void arrayLengthWithArrayHasTypeInt() {
		ArrayLength ai = new ArrayLength(new StringLiteral("hi"));
		ArrayLength newAi = doTypeCheck(ai);
		
		assertEquals(INT, newAi.getType());
	}
	
	@Test
	void arrayLengthNotArrayTypeFails() {
		ArrayLength ai = new ArrayLength(intLit(0L));
		assertTypeCheckError(ai);
	}

	//-------------------------------------------------------------------------------- 
	// BoolLiteral
	//-------------------------------------------------------------------------------- 

	@Test
	void trueIsBoolLiteral() {
		BoolLiteral literal = boolLit(true);
		BoolLiteral newLiteral = doTypeCheck(literal);
		
		assertEquals(BOOL, newLiteral.getType());
	}

	@Test
	void falseIsBoolLiteral() {
		BoolLiteral literal = boolLit(false);
		BoolLiteral newLiteral = doTypeCheck(literal);
		
		assertEquals(BOOL, newLiteral.getType());
	}


	//-------------------------------------------------------------------------------- 
	// CharLiteral 
	//-------------------------------------------------------------------------------- 

	@Test
	void charLiteralIsInt() {
		CharLiteral literal = charLit('c');
		CharLiteral newLiteral = doTypeCheck(literal);
		
		assertEquals(INT, newLiteral.getType());
	}

	//-------------------------------------------------------------------------------- 
	// FExp
	//-------------------------------------------------------------------------------- 
	
	@Test
	void fexpValid() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", 
				elems(new SimpleDecl("x", INT), new SimpleDecl("y", BOOL)), 
				singleton(INT));

		FExpr exp = new FExpr("f", elems(intLit(0L), boolLit(true)));
		exp = doTypeCheck(context, exp);

		assertEquals(Types.INT, exp.getType());
	}

	@Test
	void fexpMultipleReturnArgs() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, BOOL));

		FExpr exp = new FExpr("f", empty());
		exp = doTypeCheck(context, exp);

		assertEquals(TVEC(INT, BOOL), exp.getType());
	}

	@Test
	void fexpNoReturnInvalid() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), empty());

		FExpr exp = new FExpr("f", empty());
		assertTypeCheckError(context, exp);
	}

	@Test
	void fexpMismatchNumberOfArgs() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", singleton(new SimpleDecl("x", INT)), singleton(BOOL));

		FExpr exp = new FExpr("f", elems(intLit(0L), boolLit(true)));
		assertTypeCheckError(context, exp);
	}

	@Test
	void fexpMismatchArgTypes() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", elems(new SimpleDecl("x", INT), new SimpleDecl("y", INT)), singleton(BOOL));

		FExpr exp = new FExpr("f", elems(intLit(0L), boolLit(true)));
		assertTypeCheckError(context, exp);
	}
	
	//-------------------------------------------------------------------------------- 
	// FunctionDefn
	//-------------------------------------------------------------------------------- 
	
	@Test
	void procAnyResult() {
		FunctionDecl fDecl = new FunctionDecl("proc", elems(new SimpleDecl("x", INT)), ArrayUtils.empty());
		Block voidBlock = new Block(elems(
				new SimpleDecl("y", INT),
				new Return(ArrayUtils.empty())
				));
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		fDefn = doTypeCheck(fDefn);
		
		assertEquals(Result.VOID, fDefn.getBody().getResult());
		
		Block unitBlock = new Block(elems(
				new SimpleDecl("y", INT)
				));
		
		FunctionDefn fDefn2 = new FunctionDefn(fDecl, unitBlock);
		fDefn2 = doTypeCheck(fDefn2);
		
		assertEquals(Result.UNIT, fDefn2.getBody().getResult());
	}
	
	@Test
	void funcOnlyVoidResult() {
		FunctionDecl fDecl = new FunctionDecl("f", elems(new SimpleDecl("x", INT)), elems(Types.INT));
		Block voidBlock = new Block(elems(
				new SimpleDecl("y", INT),
				new Return(elems(intLit(1L)))
				));
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, voidBlock);
		fDefn = doTypeCheck(fDefn);
		
		assertEquals(Result.VOID, fDefn.getBody().getResult());

		Block unitBlock = new Block(elems(
				new SimpleDecl("y", INT)
				));
		
		FunctionDefn fDefn2 = new FunctionDefn(fDecl, unitBlock);
		assertTypeCheckError(fDefn2);
	}
	
	@Test
	void funcArgsInBodyScope() {
		FunctionDecl fDecl = new FunctionDecl("f", elems(new SimpleDecl("x", INT)), elems(Types.INT));
		Block block = new Block(elems(
				new SimpleDecl("x", INT),
				new Return(elems(intLit(1L)))
				));
		
		FunctionDefn fDefn = new FunctionDefn(fDecl, block);
		assertTypeCheckError(fDefn);
		
		Block block2 = new Block(elems(
				new Return(elems(new Var("x")))
				));
		
		FunctionDefn fDefn2 = new FunctionDefn(fDecl, block2);
		fDefn2 = doTypeCheck(fDefn2);
		assertEquals(Result.VOID, fDefn2.getBody().getResult());
	}
	
	//-------------------------------------------------------------------------------- 
	// IntLiteral
	//-------------------------------------------------------------------------------- 
	@Test
	void intIsIntLiteral() {
		IntLiteral literal = intLit(0L);
		IntLiteral newLiteral = doTypeCheck(literal);
		
		assertEquals(INT, newLiteral.getType());
	}
	
	//-------------------------------------------------------------------------------- 
	// Negate 
	//-------------------------------------------------------------------------------- 

	@Test
	void negateIntIsInt() {
		Negate n = new Negate(intLit(1L));
		Negate newN = doTypeCheck(n);
		
		assertEquals(INT, newN.getType());
	}

	@Test
	void negateBoolIsError() {
		Negate n = new Negate(arbitraryCondition());
		assertTypeCheckError(n);
	}
	
	//-------------------------------------------------------------------------------- 
	// Not 
	//-------------------------------------------------------------------------------- 

	@Test
	void notBoolIsBool() {
		Not n = new Not(arbitraryCondition());
		Not newN = doTypeCheck(n);
		
		assertEquals(BOOL, newN.getType());
	}

	@Test
	void notIntIsError() {
		Not n = new Not(intLit(0L));
		assertTypeCheckError(n);
	}
	
	//-------------------------------------------------------------------------------- 
	// StringLiteral
	//-------------------------------------------------------------------------------- 

	@Test
	void stringIsIntArray() {
		StringLiteral literal = stringLit("hello");
		StringLiteral newLiteral = doTypeCheck(literal);
		
		assertEquals(ARRAY(INT), newLiteral.getType());
	}
	
	//-------------------------------------------------------------------------------- 
	// Var
	//-------------------------------------------------------------------------------- 
	
	@Test
	void varFailsWhenNotInScope() {
		TypingContext context = new TypingContext();
		Var var = new Var("x");

		assertTypeCheckError(context, var);
	}

	@Test
	void varIsAssignedFromContext() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", BOOL);

		Var var = new Var("x");
		Var newVar = doTypeCheck(context, var);
		
		assertEquals(BOOL, newVar.getType());
	}

	//-------------------------------------------------------------------------------- 
	// BinExpr (Add, And, Div, EqEq, GreaterThan, GreaterThanOrEqual,
	//			HighMult, LessThan, LessThanOrEqual, Mod, Mult,
	//			Or, Sub)
	//-------------------------------------------------------------------------------- 
	@Test
	void addVarLeftIntRightIsInt() {
		Add add = new Add(new Var("x"), intLit(1L));
		assertTypeCheckError(add);
	}

	@Test
	void multIntLeftIntRight() {
		Mult mult = new Mult(intLit(0L), intLit(1L));
		Mult newMult= doTypeCheck(mult);

		assertEquals(INT, newMult.getLeft().getType());
		assertEquals(INT, newMult.getRight().getType());
		assertEquals(INT, newMult.getType());
	}


	@Test
	void addIntLeftIntRightIsInt() {
		Add add = new Add(intLit(0L), intLit(1L));
		Add newAdd = doTypeCheck(add);
		
		assertEquals(INT, newAdd.getLeft().getType());
		assertEquals(INT, newAdd.getRight().getType());
		assertEquals(INT, newAdd.getType());
	}

	@Test
	void eqEqIntLeftIntRightAndIsBool() {
		EqEq eq = new EqEq(intLit(0L), intLit(1L));
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(INT, newEq.getLeft().getType());
		assertEquals(INT, newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}

	@Test
	void eqEqBoolLeftBoolRightAndIsBool() {
		EqEq eq = new EqEq(arbitraryCondition(), arbitraryCondition());
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(BOOL, newEq.getLeft().getType());
		assertEquals(BOOL, newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}

	@Test
	void eqEqArrLeftArrRightAndIsBool() {
		EqEq eq = new EqEq(stringLit("hello"), stringLit("hello"));
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(ARRAY(INT), newEq.getLeft().getType());
		assertEquals(ARRAY(INT), newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}

	@Test
	void eqEqArrEmptyLeftEmptyArrRightAndIsBool() {
		EqEq eq = new EqEq(emptyArray(), emptyArray());
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(EMPTY_ARRAY, newEq.getLeft().getType());
		assertEquals(EMPTY_ARRAY, newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}
	
	@Test
	void eqEqArrLeftEmptyArrRightAndIsBool() {
		EqEq eq = new EqEq(stringLit("hello"), emptyArray());
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(ARRAY(INT), newEq.getLeft().getType());
		assertEquals(EMPTY_ARRAY, newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}

	@Test
	void eqEqEmptyArrLeftArrRightAndIsBool() {
		EqEq eq = new EqEq(emptyArray(), stringLit("hello"));
		EqEq newEq = doTypeCheck(eq);
		
		assertEquals(EMPTY_ARRAY, newEq.getLeft().getType());
		assertEquals(ARRAY(INT), newEq.getRight().getType());
		assertEquals(BOOL, newEq.getType());
	}

	@Test
	void addFailsWhenBoolLeftAndBoolRight() {
		BinExpr expr = new Add(arbitraryCondition(),arbitraryCondition());
		assertTypeCheckError(null, expr);
	}

	@Test
	void addFailsWhenNotIntLeft() {
		BinExpr expr = new Add(arbitraryCondition(),intLit(0L));
		assertTypeCheckError(null, expr);
	}

	@Test
	void addFailsWhenNotIntRight() {
		BinExpr expr = new Add(arbitraryCondition(),intLit(0L));
		assertTypeCheckError(null, expr);
	}

	@Test
	void eqEqIntLeftIntRightIsTypeBool() {
		BinExpr eqeq = new EqEq(intLit(0L), intLit(1L));
		BinExpr newEqEq = doTypeCheck(eqeq);
		
		assertEquals(INT, newEqEq.getLeft().getType());
		assertEquals(INT, newEqEq.getRight().getType());
		assertEquals(BOOL, newEqEq.getType());
	}
	
	@Test
	void eqEqBoolLeftBoolRightIsTypeBool() {
		BinExpr eqeq = new EqEq(arbitraryCondition(), arbitraryCondition());
		BinExpr newEqEq = doTypeCheck(eqeq);
		
		assertEquals(BOOL, newEqEq.getLeft().getType());
		assertEquals(BOOL, newEqEq.getRight().getType());
		assertEquals(BOOL, newEqEq.getType());
	}
	
	@Test
	void addArrLeftArrRightIsArr() {
		Add add = new Add(stringLit("hi"), stringLit("there"));
		Add newAdd = doTypeCheck(add);
		
		assertEquals(ARRAY(INT), newAdd.getLeft().getType());
		assertEquals(ARRAY(INT), newAdd.getRight().getType());
		assertEquals(ARRAY(INT), newAdd.getType());
	}
	
	@Test
	void addArrLeftDifferentArrRightError() {
		Add add = new Add(stringLit("hi"), arrayWithElems(emptyArray()));
		assertTypeCheckError(add);
	}


	//-------------------------------------------------------------------------------- 
	// Assign
	//-------------------------------------------------------------------------------- 

	@Test
	void singleAssignValid() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		
		// x = 0
		SingleAssign assign = new SingleAssign(new Var("x"), intLit(0L));
		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void singleAssignDeclValid() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		
		// y:int = 0
		SingleAssign assign = new SingleAssign(new SimpleDecl("y", INT), intLit(0L));
		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
		assertTrue(context.isDefined("y"));
	}

	@Test
	void singleAssignDeclAlreadyDeclared() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		
		// x:int = 0
		SingleAssign assign = new SingleAssign(new SimpleDecl("x", INT), intLit(0L));
		assertTypeCheckError(context, assign);
	}

	@Test
	void singleAssignToProcedureIsError() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), empty());
		
		// x:int = f()
		SingleAssign assign = new SingleAssign(new SimpleDecl("x", INT), new FExpr("f", empty()));
		assertTypeCheckError(context, assign);
	}

	@Test
	void singleAssignDeclMismatchType() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		
		// y:bool = 0
		SingleAssign assign = new SingleAssign(new SimpleDecl("y", BOOL), intLit(0L));
		assertTypeCheckError(context, assign);
	}

	@Test
	void singleAssignDecEmptyArray() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		
		// y:int[] = {}
		SingleAssign assign = new SingleAssign(
				new SimpleDecl("y", ARRAY(INT)), 
				new ArrayInit(empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
		assertTrue(context.isDefined("y"));
	}

	@Test
	void singleAssignIntoArray() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", ARRAY(BOOL));
		
		// x[0] = true
		SingleAssign assign = new SingleAssign(
				new ArrayIndex(new Var("x"), intLit(0L)), 
				boolLit(true));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void singleAssignIntoArrayTypeMismatch() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", ARRAY(INT));
		
		// x[0] = true 
		SingleAssign assign = new SingleAssign(
				new ArrayIndex(new Var("x"), intLit(0L)), 
				boolLit(true));
		
		assertTypeCheckError(context, assign);
	}

	@Test
	void singleAssignIntoArrayEmptyArray() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", ARRAY(ARRAY(INT)));
		
		// x[0] = {}
		SingleAssign assign = new SingleAssign(
				new ArrayIndex(new Var("x"), intLit(0L)), 
				new ArrayInit(empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void singleAssignIntoMultiArray() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", ARRAY(ARRAY(INT)));
		
		// x[0][1] = 2 
		SingleAssign assign = new SingleAssign(
				new ArrayIndex(new ArrayIndex(new Var("x"), intLit(0L)), intLit(1L)), 
				intLit(2L));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void singleAssignFunctionResult() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);
		context.addFuncDecl("f", empty(), singleton(INT));
		
		// x = f() 
		SingleAssign assign = new SingleAssign(
				new Var("x"), 
				new FExpr("f", empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void multiAssignValid() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, BOOL));
		
		// x:int, y:bool = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						of(new SimpleDecl("x", INT)),
						of(new SimpleDecl("y", BOOL))
					), 
				new FExpr("f", empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
		assertTrue(context.isDefined("y"));
	}

	@Test
	void multiAssignSingleWildcard() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT));
		
		// _ = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						Optional.empty()
					), 
				new FExpr("f", empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
	}

	@Test
	void multiAssignMultipleWithWildcards() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, ARRAY(INT), BOOL));
		
		// _, x:int, _ = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						Optional.empty(),
						of(simDecl("x", ARRAY(INT))),
						Optional.empty()
					), 
				new FExpr("f", empty()));

		assign = doTypeCheck(context, assign);
		
		assertEquals(Result.UNIT, assign.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void multiAssignMismatchTypeVectorSize() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(ARRAY(INT), INT, BOOL));
		
		// _, x:int = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						Optional.empty(),
						of(simDecl("x", INT))
					), 
				new FExpr("f", empty()));

		assertTypeCheckError(context, assign);
	}

	@Test
	void multiAssignOverlappingIdents() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, INT));
		
		// x:int, x:int = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						of(simDecl("x", INT)),
						of(simDecl("x", INT))
					), 
				new FExpr("f", empty()));

		assertTypeCheckError(context, assign);
	}

	@Test
	void multiAssignTypeMismatch() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, INT));
		
		// x:int, y:bool = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						of(simDecl("x", INT)),
						of(simDecl("y", BOOL))
					), 
				new FExpr("f", empty()));

		assertTypeCheckError(context, assign);
	}

	@Test
	void multiAssignProcedureFail() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), empty());
		
		// _ = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						Optional.empty()
					), 
				new FExpr("f", empty()));

		assertTypeCheckError(context, assign);
	}

	@Test
	void multiAssignIdentAlreadyInContext() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), elems(INT, INT));
		context.addIdBinding("x", BOOL);
		
		// x:int, y:int = f() 
		MultipleAssign assign = new MultipleAssign(
				elems(
						of(simDecl("x", INT)),
						of(simDecl("y", INT))
					), 
				new FExpr("f", empty()));

		assertTypeCheckError(context, assign);
	}

	//-------------------------------------------------------------------------------- 
	// Block
	//-------------------------------------------------------------------------------- 

	@Test
	void emptyBlockIsUnit() {
		Block block = emptyBlock();
		Block newBlock = doTypeCheck(block);
		
		assertEquals(Result.UNIT, newBlock.getResult());
	}

	@Test
	void blockAllStatementsUnit() {
		Block block = new Block(elems(
				new SimpleDecl("x", INT),
				new SimpleDecl("y", INT),
				new SimpleDecl("z", INT)
				));
		Block newBlock = doTypeCheck(block);
		
		assertEquals(Result.UNIT, newBlock.getResult());
	}

	@Test
	void blockMatchesTypeOfLastStmt() {
		TypingContext context = setupRho(empty());
		Block block = new Block(elems(
				new SimpleDecl("x", INT),
				new SimpleDecl("y", INT),
				new SimpleDecl("z", INT)
				), new Return(empty()));
		Block newBlock = doTypeCheck(context, block);
		
		assertEquals(Result.VOID, newBlock.getResult());
	}

	@Test
	void blockCantHaveVoidInMiddle() {
		TypingContext context = setupRho(empty());
		Block block = new Block(elems(
				new SimpleDecl("x", INT),
				new Block(empty(), new Return(empty())),
				new SimpleDecl("z", INT)
				), new Return(empty()));

		assertTypeCheckError(context, block);
	}

	//-------------------------------------------------------------------------------- 
	// Decl
	//-------------------------------------------------------------------------------- 

	@Test
	void declAddsToContext() {
		TypingContext context = new TypingContext();
		SimpleDecl decl = new SimpleDecl("x", INT);
		decl = doTypeCheck(context, decl);
		
		assertEquals(Result.UNIT, decl.getResult());
		assertTrue(context.isDefined("x"));
	}

	@Test
	void declAlreadyInScopeError() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", BOOL);

		SimpleDecl decl = new SimpleDecl("x", INT);
		assertTypeCheckError(context, decl);
	}

	@Test
	void extendedDeclAlreadyInScopeError() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", BOOL);

		ExtendedDecl decl = new ExtendedDecl("x", new DeclType(INT, 
				elems(intLit(3L)), 
				1));
		assertTypeCheckError(context, decl);
	}

	@Test
	void extendedDeclArrayValid() {
		TypingContext context = new TypingContext();
		ExtendedDecl decl = new ExtendedDecl("x", new DeclType(INT, 
				elems(intLit(3L)), 
				1));

		decl = doTypeCheck(context, decl);
		
		assertEquals(Result.UNIT, decl.getResult());
		assertEquals(addArrayDims(INT, 2), context.getIdType("x"));
	}

	@Test
	void extendedDeclArrayNonIntIndex() {
		TypingContext context = new TypingContext();
		ExtendedDecl decl = new ExtendedDecl("x", new DeclType(INT, 
				elems(boolLit(false)), 
				1));

		assertTypeCheckError(context, decl);
	}

	//-------------------------------------------------------------------------------- 
	// If
	//-------------------------------------------------------------------------------- 

	@Test
	void ifNoElseIsUnit() {
		If ifStmt = new If(arbitraryCondition(), emptyBlock());
		ifStmt = doTypeCheck(ifStmt);
		
		assertEquals(Result.UNIT, ifStmt.getResult());
	}

	@Test
	void ifNoElseIsUnitDespiteVoidInner() {
		TypingContext gamma = setupRho(empty());
		If ifStmt = new If(arbitraryCondition(), new Block(empty(), new Return(empty())));
		ifStmt = doTypeCheck(gamma, ifStmt);
		
		assertEquals(Result.UNIT, ifStmt.getResult());
	}

	@Test
	void ifRestoresScope() {
		TypingContext context = new TypingContext();
		context.addIdBinding("z", BOOL);

		If ifStmt = new If(arbitraryCondition(), new SimpleDecl("x", INT));
		ifStmt = doTypeCheck(context, ifStmt);
		
		assertEquals(Result.UNIT, ifStmt.getResult());
		assertFalse(context.isDefined("x"));
		assertTrue(context.isDefined("z"));
	}

	@Test
	void ifWithElseBothVoidIsVoid() {
		TypingContext context = setupRho(empty());

		// if cond { return } else { return }
		If ifStmt = new If(arbitraryCondition(), 
				new Block(empty(), new Return(empty())),
				new Block(empty(), new Return(empty()))
			);
		ifStmt = doTypeCheck(context, ifStmt);
		
		assertEquals(Result.VOID, ifStmt.getResult());
	}

	@Test
	void ifWithElseOneUnitIsUnit() {
		TypingContext context = setupRho(empty());

		// if cond { x : int } else { return }
		If ifStmt = new If(arbitraryCondition(), 
				new SimpleDecl("x", INT),
				new Block(empty(), new Return(empty()))
			);
		ifStmt = doTypeCheck(context, ifStmt);
		
		assertEquals(Result.UNIT, ifStmt.getResult());
	}

	@Test
	void ifWithElseBranchesDontShareContext() {
		TypingContext context = new TypingContext();

		If ifStmt = new If(arbitraryCondition(), 
				new SimpleDecl("x", INT),
				new SimpleDecl("x", INT)
			);
		ifStmt = doTypeCheck(context, ifStmt);
		
		assertEquals(Result.UNIT, ifStmt.getResult());
	}

	//-------------------------------------------------------------------------------- 
	// While
	//-------------------------------------------------------------------------------- 

	@Test
	void whileValidIsUnit() {
		While whileStmt = new While(arbitraryCondition(), emptyBlock());
		whileStmt = doTypeCheck(whileStmt);
		
		assertEquals(Result.UNIT, whileStmt.getResult());
	}

	@Test
	void whileRequiresBooleanCondition() {
		While whileStmt = new While(intLit(0L), emptyBlock());
		assertTypeCheckError(whileStmt);
	}

	@Test
	void whileDoesntLeakScope() {
		TypingContext context = new TypingContext();
		context.addIdBinding("x", INT);

		While whileStmt = new While(arbitraryCondition(), new SimpleDecl("y", INT));
		whileStmt = doTypeCheck(context, whileStmt);

		assertFalse(context.isDefined("y"));
		assertTrue(context.isDefined("x"));
	}

	//-------------------------------------------------------------------------------- 
	// Procedure Call
	//-------------------------------------------------------------------------------- 

	@Test
	void procedureCallValid() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), empty());

		FunctionCall stmt = new FunctionCall(new FExpr("f", empty()));
		stmt = doTypeCheck(context, stmt);
		
		assertEquals(Result.UNIT, stmt.getResult());
	}

	@Test
	void procedureCallUnboundFunction() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), empty());

		FunctionCall stmt = new FunctionCall(new FExpr("g", empty()));
		assertTypeCheckError(context, stmt);
	}

	@Test
	void procedureCallDoesntReturnUnit() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", empty(), singleton(INT));

		FunctionCall stmt = new FunctionCall(new FExpr("f", empty()));
		assertTypeCheckError(context, stmt);
	}

	@Test
	void procedureCallMismatchNumArgs() {
		TypingContext context = new TypingContext();
		context.addFuncDecl("f", singleton(new SimpleDecl("x", INT)), empty());

		FunctionCall stmt = new FunctionCall(new FExpr("f", empty()));
		assertTypeCheckError(context, stmt);
	}

	//-------------------------------------------------------------------------------- 
	// Return
	//-------------------------------------------------------------------------------- 
	
	@Test
	void returnAlwaysVoid() {
		TypingContext gamma = setupRho(empty());

		Return ret = new Return(empty());
		ret = doTypeCheck(gamma, ret);

		assertEquals(Result.VOID, ret.getResult());
	}

	@Test
	void returnExprMismatchInNumberError() {
		TypingContext gamma = setupRho(elems(BOOL));

		Return ret = new Return(empty());
		assertTypeCheckError(gamma, ret);
	}

	@Test
	void returnExprMismatchInTypeError() {
		TypingContext gamma = setupRho(elems(INT));

		Return ret = new Return(elems(arbitraryCondition()));
		assertTypeCheckError(gamma, ret);
	}

	@Test
	void returnExprTypesMatch() {
		TypingContext gamma = setupRho(elems(BOOL));

		Return ret = new Return(elems(arbitraryCondition()));
		ret = doTypeCheck(gamma, ret);
		
		assertEquals(Result.VOID, ret.getResult());
	}

	@Test
	void returnMultipleExprTypesMatch() {
		TypingContext gamma = setupRho(elems(BOOL, INT));

		Return ret = new Return(elems(arbitraryCondition(), intLit(0L)));
		ret = doTypeCheck(gamma, ret);
		
		assertEquals(Result.VOID, ret.getResult());
	}

	@Test
	void returnMultipleExprTypesMismatch() {
		TypingContext gamma = setupRho(elems(addArrayDims(BOOL, 1), INT));

		Return ret = new Return(elems(arbitraryCondition(), intLit(0L)));
		assertTypeCheckError(gamma, ret);
	}

	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private <N extends Node> N doTypeCheck(TypingContext context, N node) {
		TypeChecker tc = new TypeChecker(context);
		addLocs(node);
		node = tc.performTypeCheck(node);
		
		if(tc.hasError()) {
			assertTrue(false, "Expected no errors but got " + tc.getFirstError().getFileErrorMessage());
		}
		return node;
	}

	private <N extends Node> N doTypeCheck(N node) {
		return doTypeCheck(new TypingContext(), node);
	}
	
	private <N extends Node> void assertTypeCheckError(TypingContext context, N node) {
		TypeChecker tc = new TypeChecker(context);
		addLocs(node);
		tc.performTypeCheck(node);
		assertTrue(tc.hasError(), "Expected type check error but got none");
	}

	private <N extends Node> void assertTypeCheckError(N node) {
		assertTypeCheckError(new TypingContext(), node);
	}
	
	private TypingContext setupRho(List<Type> retTypes) {
		TypingContext context = new TypingContext();
		context.addFuncBindings(empty(), retTypes);
		return context;
	}
}
