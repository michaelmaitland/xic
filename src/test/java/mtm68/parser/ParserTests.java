package mtm68.parser;

import static mtm68.lexer.TokenType.*;
import static mtm68.util.ArrayUtils.*;
import static mtm68.util.NodeTestUtil.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Negate;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.StringLiteral;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.binary.LessThan;
import mtm68.ast.nodes.binary.Mult;
import mtm68.ast.nodes.binary.Sub;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.ExtendedDecl;
import mtm68.ast.nodes.stmts.ProcedureCall;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.ast.nodes.stmts.Statement;
import mtm68.ast.nodes.stmts.While;
import mtm68.ast.types.Types;
import mtm68.exception.ParserError;
import mtm68.lexer.MockLexer;
import mtm68.lexer.Token;
import mtm68.lexer.TokenFactory;
import mtm68.lexer.TokenType;
import mtm68.util.ArrayUtils;

public class ParserTests {
	
	private ComplexSymbolFactory symFac = new ComplexSymbolFactory();
	private TokenFactory tokenFac = new TokenFactory();

	//-------------------------------------------------------------------------------- 
	//- Function Decls 
	//-------------------------------------------------------------------------------- 

	@Test
	void noFunctionDeclsValidInterface() throws Exception {
		Interface i = parseInterfaceFromTokens(ArrayUtils.empty());
		assertEquals(0, i.getFunctionDecls().size());
	}

	@Test
	void interfaceWithSingleDecl() throws Exception {
		// f(a:int):bool[]
		List<Token> tokens = elems(
				token(ID, "f"),
				token(OPEN_PAREN),
				token(ID, "a"),
				token(COLON),
				token(INT),
				token(CLOSE_PAREN),
				token(COLON),
				token(BOOL),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getFunctionDecls().size());
		
		FunctionDecl decl = i.getFunctionDecls().get(0);
		assertEquals(1, decl.getReturnTypes().size());
		assertEquals(1, decl.getArgs().size());
	}

	@Test
	void interfaceWithMultipleDecls() throws Exception {
		// f(a:int):bool[]
		// g():int, bool
		List<Token> tokens = elems(
				token(ID, "f"),
				token(OPEN_PAREN),
				token(ID, "a"),
				token(COLON),
				token(INT),
				token(CLOSE_PAREN),
				token(COLON),
				token(BOOL),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE),
				token(ID, "g"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN),
				token(COLON),
				token(INT),
				token(COMMA),
				token(BOOL)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(2, i.getFunctionDecls().size());
		
		FunctionDecl declTwo = i.getFunctionDecls().get(1);
		assertEquals(2, declTwo.getReturnTypes().size());
		assertEquals(0, declTwo.getArgs().size());
	}
	
	//-------------------------------------------------------------------------------- 
	//- Assign Statement 
	//-------------------------------------------------------------------------------- 

	@Test
	void singleAssign() throws Exception {
		// x = 3
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(EQ), 
				token(INTEGER, 3L));
		Program prog = parseProgFromStmt(tokens);
		
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		assertInstanceOf(Var.class, assignStmt.getLhs());
	}

	@Test
	void singleAssignWithDecl() throws Exception {
		// x:int = 3
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(INT), 
				token(EQ), 
				token(INTEGER, 3L));
		Program prog = parseProgFromStmt(tokens);
		
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		SimpleDecl decl = assertInstanceOfAndReturn(SimpleDecl.class, assignStmt.getLhs());
		assertEquals(Types.INT, decl.getType());
	}

	@Test
	void singleAssignWithArrayDeclError() throws Exception {
		// x:int[3] = 0
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(INT), 
				token(OPEN_SQUARE),
				token(INTEGER, 3L),
				token(CLOSE_SQUARE),
				token(EQ), 
				token(INTEGER, 0L));

		assertSyntaxError(EQ, parseErrorFromStmt(tokens));
	}

	@Test
	void singleAssignWithArrayDeclValid() throws Exception {
		// x:int[] = 0
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(INT), 
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE),
				token(EQ), 
				token(INTEGER, 0L));

		Program prog = parseProgFromStmt(tokens);
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		SimpleDecl decl = assertInstanceOfAndReturn(SimpleDecl.class, assignStmt.getLhs());
		assertEquals(Types.ARRAY(Types.INT), decl.getType());
	}

	@Test
	void singleAssignWithDoubleArrayDeclValid() throws Exception {
		// x:int[][] = 0
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(INT), 
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE),
				token(EQ), 
				token(INTEGER, 0L));

		Program prog = parseProgFromStmt(tokens);
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		SimpleDecl decl = assertInstanceOfAndReturn(SimpleDecl.class, assignStmt.getLhs());
		assertEquals(Types.addArrayDims(Types.INT, 2), decl.getType());
	}

	@Test
	void singleAssignArrayIndex() throws Exception {
		// x[0] = 3
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(OPEN_SQUARE), 
				token(INTEGER, 0L), 
				token(CLOSE_SQUARE), 
				token(EQ), 
				token(INTEGER, 3L));

		Program prog = parseProgFromStmt(tokens);
		
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		assertInstanceOf(ArrayIndex.class, assignStmt.getLhs());
	}

	@Test
	void singleAssignArrayIndexNoExpressionError() throws Exception {
		// x[] = 3
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(OPEN_SQUARE), 
				token(CLOSE_SQUARE), 
				token(EQ), 
				token(INTEGER, 3L));

		assertSyntaxError(CLOSE_SQUARE, parseErrorFromStmt(tokens));
	}

	@Test
	void singleAssignArrayMultipleIndices() throws Exception {
		// x[true]["hello"][3] = 3
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(OPEN_SQUARE), 
				token(TRUE),
				token(CLOSE_SQUARE), 
				token(OPEN_SQUARE), 
				token(STRING, "hello"),
				token(CLOSE_SQUARE), 
				token(OPEN_SQUARE), 
				token(INTEGER, 3L),
				token(CLOSE_SQUARE), 
				token(EQ), 
				token(INTEGER, 3L));

		Program prog = parseProgFromStmt(tokens);
		
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog)) ;
		ArrayIndex ai = assertInstanceOfAndReturn(ArrayIndex.class, assignStmt.getLhs());
		assertInstanceOf(IntLiteral.class, ai.getIndex());
	}

	@Test
	void singleAssignNoParentheses() throws Exception {
		// (x)[0] = 3
		List<Token> tokens = elems(
				token(OPEN_PAREN), 
				token(ID, "x"), 
				token(CLOSE_PAREN), 
				token(OPEN_SQUARE), 
				token(INTEGER, 0L), 
				token(CLOSE_SQUARE), 
				token(EQ), 
				token(INTEGER, 3L));

		assertSyntaxError(OPEN_PAREN, parseErrorFromStmt(tokens));
	}

	@Test
	void multipleAssignOneWildcardSyntaxError() throws Exception {
		// _ = 3
		List<Token> tokens = elems(
				token(UNDERSCORE), 
				token(EQ), 
				token(INTEGER, 3L));

		assertSyntaxError(INTEGER, parseErrorFromStmt(tokens));
	}

	@Test
	void multipleAssignOneWildcardValid() throws Exception {
		// _ = g()
		List<Token> tokens = elems(
				token(UNDERSCORE), 
				token(EQ), 
				token(ID, "g"),
				token(OPEN_PAREN), 
				token(CLOSE_PAREN) 
				);

		Program prog = parseProgFromStmt(tokens);
		MultipleAssign assign = assertInstanceOfAndReturn(MultipleAssign.class, firstStatement(prog));
		assertEquals(Optional.empty(), assign.getDecls().get(0));
	}

	@Test
	void multipleAssignNotAllDeclsError() throws Exception {
		// x: bool, y = g()
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(BOOL), 
				token(COMMA),
				token(ID, "y"),
				token(EQ), 
				token(ID, "g"),
				token(OPEN_PAREN), 
				token(CLOSE_PAREN) 
				);

		assertSyntaxError(EQ, parseErrorFromStmt(tokens));
	}

	@Test
	void multipleAssignValid() throws Exception {
		// x: bool, y:int = g()
		List<Token> tokens = elems(
				token(ID, "x"), 
				token(COLON), 
				token(BOOL), 
				token(COMMA),
				token(ID, "y"),
				token(COLON), 
				token(INT), 
				token(EQ), 
				token(ID, "g"),
				token(OPEN_PAREN), 
				token(CLOSE_PAREN) 
				);

		Program prog = parseProgFromStmt(tokens);
		assertInstanceOf(MultipleAssign.class, firstStatement(prog));
		assertTrue(firstStatement(prog) instanceof MultipleAssign);
	}
	
	//-------------------------------------------------------------------------------- 
	//- If Statement 
	//-------------------------------------------------------------------------------- 

	@Test
	void ifNoElse() throws Exception {
		// if e s
		List<Token> ifTokens = elems(token(IF));
		ifTokens.addAll(arbitraryExp());
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		If ifStmt = assertInstanceOfAndReturn(If.class, firstStatement(prog));
		assertEquals(Optional.empty(), ifStmt.getElseBranch());
	}

	@Test
	void ifWithElse() throws Exception {
		// if e s1 else s2
		List<Token> ifTokens = elems(token(IF));
		ifTokens.addAll(arbitraryExp());
		ifTokens.addAll(arbitraryStmt());
		ifTokens.add(token(ELSE));
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		If ifStmt = assertInstanceOfAndReturn(If.class, firstStatement(prog));
		assertTrue(ifStmt.getElseBranch().isPresent());
	}

	@Test
	void ifWithParens() throws Exception {
		// if(e) s
		List<Token> ifTokens = elems(token(IF));
		ifTokens.add(token(OPEN_PAREN));
		ifTokens.addAll(arbitraryExp());
		ifTokens.add(token(CLOSE_PAREN));
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		
		If ifStmt = assertInstanceOfAndReturn(If.class, firstStatement(prog));
		assertEquals(Optional.empty(), ifStmt.getElseBranch());
	}

	//-------------------------------------------------------------------------------- 
	//- While Statement 
	//-------------------------------------------------------------------------------- 

	@Test
	void whileValid() throws Exception {
		// while e s
		List<Token> tokens = elems(token(WHILE));
		tokens.addAll(arbitraryExp());
		tokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(tokens);
		
		While whileStmt = assertInstanceOfAndReturn(While.class, firstStatement(prog));
		assertNotNull(whileStmt.getBody());
		assertNotNull(whileStmt.getCondition());
	}

	@Test
	void whileWithParens() throws Exception {
		// while (e) s
		List<Token> tokens = elems(token(WHILE));
		tokens.add(token(OPEN_PAREN));
		tokens.addAll(arbitraryExp());
		tokens.add(token(CLOSE_PAREN));
		tokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(tokens);
		
		While whileStmt = assertInstanceOfAndReturn(While.class, firstStatement(prog));
		assertNotNull(whileStmt.getBody());
		assertNotNull(whileStmt.getCondition());
	}

	//-------------------------------------------------------------------------------- 
	//- Return Statement 
	//-------------------------------------------------------------------------------- 

	@Test
	void returnEmpty() throws Exception {
		// return
		List<Token> tokens = elems(
				token(RETURN));

		Program prog = parseProgFromStmt(tokens);
		
		Optional<Return> retStmt = returnStatement(prog);
		assertTrue(retStmt.isPresent());
		assertTrue(retStmt.get().getRetList().isEmpty());
	}

	@Test
	void returnEmptyWithSemi() throws Exception {
		// return;
		List<Token> tokens = elems(
				token(RETURN),
				token(SEMICOLON)
				);

		Program prog = parseProgFromStmt(tokens);
		
		Optional<Return> retStmt = returnStatement(prog);
		assertTrue(retStmt.isPresent());
		assertTrue(retStmt.get().getRetList().isEmpty());
	}

	@Test
	void returnMultiple() throws Exception {
		// return "hi", 3, true
		List<Token> tokens = elems(
				token(RETURN),
				token(STRING, "hi"),
				token(COMMA),
				token(INTEGER, 3L),
				token(COMMA),
				token(TRUE)
				);

		Program prog = parseProgFromStmt(tokens);
		
		Optional<Return> retStmt = returnStatement(prog);
		assertTrue(retStmt.isPresent());
		
		List<Expr> retExprs = retStmt.get().getRetList();
		assertEquals(3, retExprs.size());
		assertInstanceOf(StringLiteral.class, retExprs.get(0));
		assertInstanceOf(IntLiteral.class, retExprs.get(1));
		assertInstanceOf(BoolLiteral.class, retExprs.get(2));
	}

	@Test
	void returnNotLastStmtError() throws Exception {
		// return "hi", 3, true
		// x = "something"
		List<Token> tokens = elems(
				token(RETURN),
				token(STRING, "hi"),
				token(COMMA),
				token(INTEGER, 3L),
				token(COMMA),
				token(TRUE),
				token(SEMICOLON),
				token(ID, "x"),
				token(EQ),
				token(STRING, "something")
				);

		assertSyntaxError(ID, parseErrorFromStmt(tokens));
	}

	//-------------------------------------------------------------------------------- 
	//- Function Call Statement 
	//-------------------------------------------------------------------------------- 
	
	@Test
	void procedureCallNoArgs() throws Exception {
		// f()
		List<Token> tokens = elems(
			token(ID, "f"),
			token(OPEN_PAREN),
			token(CLOSE_PAREN)
			);

		Program prog = parseProgFromStmt(tokens);
		
		ProcedureCall fc = assertInstanceOfAndReturn(ProcedureCall.class, firstStatement(prog));
		assertTrue(fc.getFexp().getArgs().isEmpty());
	}

	@Test
	void procedureCallWithArgs() throws Exception {
		// f(true, "false")
		List<Token> tokens = elems(
			token(ID, "f"),
			token(OPEN_PAREN),
			token(TRUE),
			token(COMMA),
			token(STRING, "false"),
			token(CLOSE_PAREN)
			);

		Program prog = parseProgFromStmt(tokens);
		
		ProcedureCall fc = assertInstanceOfAndReturn(ProcedureCall.class, firstStatement(prog));
		List<Expr> args = fc.getFexp().getArgs(); 
		assertEquals(2, args.size());
		assertInstanceOf(BoolLiteral.class, args.get(0));
		assertInstanceOf(StringLiteral.class, args.get(1));
	}

	//-------------------------------------------------------------------------------- 
	//- Block Statement 
	//-------------------------------------------------------------------------------- 
	
	@Test
	void blockSingleSemiSyntaxError() throws Exception {
		// {;}
		List<Token> tokens = elems(token(SEMICOLON));
		
		
		assertSyntaxError(SEMICOLON, parseErrorFromStmt(tokens));
	}

	@Test
	void blockEmpty() throws Exception {
		// {}
		List<Token> tokens = elems(
				token(OPEN_CURLY),
				token(CLOSE_CURLY)
				);
		
		Program prog = parseProgFromStmt(tokens);
		
		Block block = assertInstanceOfAndReturn(Block.class, firstStatement(prog));
		assertTrue(block.getStmts().isEmpty());
		assertEquals(Optional.empty(), block.getReturnStmt());
	}

	@Test
	void blockMultipleStmtsWithSemis() throws Exception {
		// { s1; s2; s3; }
		List<Token> tokens = elems(
				token(OPEN_CURLY)
				);
		tokens.addAll(arbitraryStmt());
		tokens.add(token(SEMICOLON));
		tokens.addAll(arbitraryStmt());
		tokens.add(token(SEMICOLON));
		tokens.addAll(arbitraryStmt());
		tokens.add(token(SEMICOLON));
		tokens.add(token(CLOSE_CURLY));
		
		Program prog = parseProgFromStmt(tokens);
		
		Block block = assertInstanceOfAndReturn(Block.class, firstStatement(prog));
		assertEquals(3, block.getStmts().size());
	}

	@Test
	void blockMultipleStmtsNoSemis() throws Exception {
		// { s1 s2 s3 }
		List<Token> tokens = elems(
				token(OPEN_CURLY)
				);
		tokens.addAll(arbitraryStmt());
		tokens.addAll(arbitraryStmt());
		tokens.addAll(arbitraryStmt());
		tokens.add(token(CLOSE_CURLY));
		
		Program prog = parseProgFromStmt(tokens);
		
		Block block = assertInstanceOfAndReturn(Block.class, firstStatement(prog));
		assertEquals(3, block.getStmts().size());
	}

	@Test
	void blockEmptyStmtsInvalid() throws Exception {
		// { s1; ; s3; }
		List<Token> tokens = elems(
				token(OPEN_CURLY)
				);
		tokens.addAll(arbitraryStmt());
		tokens.add(token(SEMICOLON));
		tokens.add(token(SEMICOLON));
		tokens.addAll(arbitraryStmt());
		tokens.add(token(SEMICOLON));
		tokens.add(token(CLOSE_CURLY));
		
		assertSyntaxError(SEMICOLON, parseErrorFromStmt(tokens));
	}

	//-------------------------------------------------------------------------------- 
	//- Decl Statement 
	//-------------------------------------------------------------------------------- 
	
	@Test
	void declSimpleType() throws Exception {
		// x:int 
		List<Token> tokens = elems(
				token(ID, "x"),
				token(COLON),
				token(INT)
				);
		
		Program prog = parseProgFromStmt(tokens);
		
		SimpleDecl decl = assertInstanceOfAndReturn(SimpleDecl.class, firstStatement(prog));
		assertEquals(Types.INT, decl.getType());
	}

	@Test
	void declSimpleTypeArray() throws Exception {
		// x:int[]
		List<Token> tokens = elems(
				token(ID, "x"),
				token(COLON),
				token(INT),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE)
				);
		
		Program prog = parseProgFromStmt(tokens);
		
		SimpleDecl decl = assertInstanceOfAndReturn(SimpleDecl.class, firstStatement(prog));
		assertEquals(Types.ARRAY(Types.INT), decl.getType());
	}

	@Test
	void declArrayWithInitialization() throws Exception {
		// x:int[true]["hi"][]
		List<Token> tokens = elems(
				token(ID, "x"),
				token(COLON),
				token(INT),
				token(OPEN_SQUARE),
				token(TRUE),
				token(CLOSE_SQUARE),
				token(OPEN_SQUARE),
				token(STRING, "hi"),
				token(CLOSE_SQUARE),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE)
				);
		
		Program prog = parseProgFromStmt(tokens);
		
		ExtendedDecl decl = assertInstanceOfAndReturn(ExtendedDecl.class, firstStatement(prog));
		assertEquals(Types.addArrayDims(Types.INT, 3), decl.getExtendedType().getType());
		
		List<Expr> indices = decl.getExtendedType().getIndices();
		assertEquals(2, indices.size());
		assertInstanceOf(BoolLiteral.class, indices.get(0));
		assertInstanceOf(StringLiteral.class, indices.get(1));
	}

	@Test
	void declArrayWithInitializationError() throws Exception {
		// x:int[][true]
		List<Token> tokens = elems(
				token(ID, "x"),
				token(COLON),
				token(INT),
				token(OPEN_SQUARE),
				token(CLOSE_SQUARE),
				token(OPEN_SQUARE),
				token(TRUE),
				token(CLOSE_SQUARE)
				);
		
		assertSyntaxError(TRUE, parseErrorFromStmt(tokens));
	}

	//-------------------------------------------------------------------------------- 
	//- Precedence 
	//-------------------------------------------------------------------------------- 

	@Test
	void arrayIndexHigherPrecedence() throws Exception {
		// "hi" < a[1]
		List<Token> tokens = elems(
				token(STRING, "hi"),
				token(LT),
				token(ID, "a"),
				token(OPEN_SQUARE),
				token(INTEGER, 1L),
				token(CLOSE_SQUARE)
				);
		
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(LessThan.class, firstExp(prog));
	}

	//-------------------------------------------------------------------------------- 
	//- Associativity 
	//-------------------------------------------------------------------------------- 

	@Test
	void testAsssociativityAddDiv() throws Exception {
		// 4 + 3 / 2 
		List<Token> tokens = elems(
				token(INTEGER, 4L),
				token(ADD),
				token(INTEGER, 3L),
				token(DIV),
				token(INTEGER, 2L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));

		// 4 / 2 + 3
		tokens = elems(
				token(INTEGER, 4L),
				token(DIV),
				token(INTEGER, 2L),
				token(ADD),
				token(INTEGER, 3L)
			);
		prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));
	}

	@Test
	void testAsssociativityAddMult() throws Exception {
		// 4 + 3 / 2 
		List<Token> tokens = elems(
				token(INTEGER, 4L),
				token(ADD),
				token(INTEGER, 3L),
				token(MULT),
				token(INTEGER, 2L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));

		// 4 / 2 + 3
		tokens = elems(
				token(INTEGER, 4L),
				token(MULT),
				token(INTEGER, 2L),
				token(ADD),
				token(INTEGER, 3L)
			);
		prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));
	}

	@Test
	void testAsssociativityAddSub() throws Exception {
		// 4 + 3 / 2 
		List<Token> tokens = elems(
				token(INTEGER, 4L),
				token(ADD),
				token(INTEGER, 3L),
				token(SUB),
				token(INTEGER, 2L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Sub.class, firstExp(prog));

		// 4 / 2 + 3
		tokens = elems(
				token(INTEGER, 4L),
				token(SUB),
				token(INTEGER, 2L),
				token(ADD),
				token(INTEGER, 3L)
			);
		prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));
	}

	void testAssociativityFunc() throws Exception {
		// -h()
		List<Token> tokens = elems(
				token(SUB),
				token(ID, "h"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Negate.class, firstExp(prog));

	}
	
	@Test
	void testAssociativityArr() throws Exception {
		// -h[0]
		List<Token> tokens = elems(
				token(SUB),
				token(ID, "h"),
				token(OPEN_SQUARE),
				token(INTEGER, 0L),
				token(CLOSE_SQUARE)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Negate.class, firstExp(prog));
	}

	@Test
	void testAssociativityIntNegation() throws Exception {
		// -3 * 4
		List<Token> tokens = elems(
				token(SUB),
				token(INTEGER, 3L),
				token(MULT),
				token(INTEGER, 4L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Mult.class, firstExp(prog));
	}
	
	@Test
	void testAssociativityMultiplication() throws Exception{
		// 1 + 2 * 4
		List<Token> tokens = elems(
				token(INTEGER, 1L),
				token(ADD),
				token(INTEGER, 2L),
				token(MULT),
				token(INTEGER, 4L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Add.class, firstExp(prog));
	}
	
	@Test
	void testAsssociativityMultNegativeNumbers() throws Exception{
		// -4 * -3
		List<Token> tokens = elems(
				token(SUB),
				token(INTEGER, 4L),
				token(MULT),
				token(SUB),
				token(INTEGER, 3L)
				);
		Program prog = parseProgFromExp(tokens);
		assertInstanceOf(Mult.class, firstExp(prog));
	}
	
	// f () { [INSERT HERE] }
	
	private void assertSyntaxError(TokenType expected, ParserError actual) {
		assertEquals(expected, tokenFromError(actual).getType());
	}

	private Token tokenFromError(ParserError errorInfo) {
		return errorInfo.getToken();
	}

	private Statement firstStatement(Program program) {
		return program.getFunctionDefns().get(0).getBody().getStmts().get(0);
	}

	private Optional<Return> returnStatement(Program program) {
		return program.getFunctionDefns().get(0).getBody().getReturnStmt();
	}

	private Expr firstExp(Program program) {
		SingleAssign assign = (SingleAssign) firstStatement(program);
		return assign.getRhs();
	}

	private Program parseProgFromExp(List<Token> exp) throws Exception {
		return parseProgFromStmt(expToStmt(exp));
	}
	
	private Program parseProgFromStmt(List<Token> stmt) throws Exception {
		ParseResult parseResult = new ParseResult(setupParser(stmtToProg(stmt)));
		return (Program) parseResult.getNode().get();
	}

	private ParserError parseErrorFromStmt(List<Token> stmt) throws Exception {
		ParseResult parseResult = new ParseResult(setupParser(stmtToProg(stmt)));
		return (ParserError) parseResult.getFirstError();
	}
	
	private Interface parseInterfaceFromTokens(List<Token> tokens) throws Exception {
		tokens.add(0, token(IXI));
		return (Interface) setupParser(tokens).parse().value;
	}
	
	private Parser setupParser(List<Token> tokens) {
		return new Parser(new MockLexer(tokens), symFac);
	}
	
	private List<Token> arbitraryStmt() {
		return elems(token(ID, "x"), token(EQ), token(INTEGER, 3L));
	}

	private List<Token> arbitraryExp() {
		return elems(token(INTEGER, 3L), token(MOD), token(STRING, "hi"));
	}
	
	private List<Token> stmtToProg(List<Token> stmt) {
		stmt.add(token(CLOSE_CURLY));
		stmt.add(token(EOF));

		List<Token> prepend = elems(
				token(XI),
				token(ID, "f"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN),
				token(OPEN_CURLY)
				);
		return concat(prepend, stmt);
	}
	
	private List<Token> expToStmt(List<Token> exp) {
		List<Token> prepend = elems(token(ID, "x"), token(EQ));
		return concat(prepend, exp);
	}
	
	private Token token(TokenType t) {
		return tokenFac.newToken(t, 0, 0);
	}

	private Token token(TokenType t, Object data) {
		return tokenFac.newToken(t, data, 0, 0);
	}
	
	}
