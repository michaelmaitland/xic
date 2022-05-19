package mtm68.parser;

import static mtm68.lexer.TokenType.ADD;
import static mtm68.lexer.TokenType.BOOL;
import static mtm68.lexer.TokenType.CLASS;
import static mtm68.lexer.TokenType.CLOSE_CURLY;
import static mtm68.lexer.TokenType.CLOSE_PAREN;
import static mtm68.lexer.TokenType.CLOSE_SQUARE;
import static mtm68.lexer.TokenType.COLON;
import static mtm68.lexer.TokenType.COMMA;
import static mtm68.lexer.TokenType.DIV;
import static mtm68.lexer.TokenType.DOT;
import static mtm68.lexer.TokenType.ELSE;
import static mtm68.lexer.TokenType.EOF;
import static mtm68.lexer.TokenType.EQ;
import static mtm68.lexer.TokenType.EXTENDS;
import static mtm68.lexer.TokenType.ID;
import static mtm68.lexer.TokenType.IF;
import static mtm68.lexer.TokenType.INT;
import static mtm68.lexer.TokenType.INTEGER;
import static mtm68.lexer.TokenType.IXI;
import static mtm68.lexer.TokenType.LT;
import static mtm68.lexer.TokenType.MOD;
import static mtm68.lexer.TokenType.MULT;
import static mtm68.lexer.TokenType.NEW;
import static mtm68.lexer.TokenType.OPEN_CURLY;
import static mtm68.lexer.TokenType.OPEN_PAREN;
import static mtm68.lexer.TokenType.OPEN_SQUARE;
import static mtm68.lexer.TokenType.RETURN;
import static mtm68.lexer.TokenType.SEMICOLON;
import static mtm68.lexer.TokenType.STRING;
import static mtm68.lexer.TokenType.SUB;
import static mtm68.lexer.TokenType.THIS;
import static mtm68.lexer.TokenType.TRUE;
import static mtm68.lexer.TokenType.UNDERSCORE;
import static mtm68.lexer.TokenType.USE;
import static mtm68.lexer.TokenType.WHILE;
import static mtm68.lexer.TokenType.XI;
import static mtm68.util.ArrayUtils.concat;
import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.NodeTestUtil.assertInstanceOf;
import static mtm68.util.NodeTestUtil.assertInstanceOfAndReturn;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FieldAccess;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.MethodCall;
import mtm68.ast.nodes.Negate;
import mtm68.ast.nodes.New;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.StringLiteral;
import mtm68.ast.nodes.This;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.binary.LessThan;
import mtm68.ast.nodes.binary.Mult;
import mtm68.ast.nodes.binary.Sub;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.ExtendedDecl;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.ProcedureCall;
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
		assertEquals(0, i.getBody().getFunctionDecls().size());
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
		assertEquals(1, i.getBody().getFunctionDecls().size());
		
		FunctionDecl decl = i.getBody().getFunctionDecls().get(0);
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
		assertEquals(2, i.getBody().getFunctionDecls().size());
		
		FunctionDecl declTwo = i.getBody().getFunctionDecls().get(1);
		assertEquals(2, declTwo.getReturnTypes().size());
		assertEquals(0, declTwo.getArgs().size());
	}
	
	
	//-------------------------------------------------------------------------------- 
	//- Class Decls
	//-------------------------------------------------------------------------------- 
	
	@Test
	void interfaceWithSingleClassDeclNoSuper() throws Exception {
		// class A {}
		List<Token> tokens = elems(
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getBody().getClassDecls().size());
		
		ClassDecl decl = i.getBody().getClassDecls().get(0);
		assertEquals("A", decl.getId());
		assertNull(decl.getSuperType());
		assertEquals(0, decl.getMethodDecls().size());
	}	

	@Test
	void interfaceWithSingleClassDeclWithSuper() throws Exception {
		// class A extends B {}
		List<Token> tokens = elems(
				token(CLASS),
				token(ID, "A"),
				token(EXTENDS),
				token(ID, "B"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getBody().getClassDecls().size());
		
		ClassDecl decl = i.getBody().getClassDecls().get(0);
		assertEquals("A", decl.getId());
		assertEquals("B", decl.getSuperType());
		assertEquals(0, decl.getMethodDecls().size());
	}	
	
	@Test
	void interfaceWithSingleClassDeclWithMethod() throws Exception {
		// class A {
		//   f(a:int):bool[]
		// }
		List<Token> tokens = elems(
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
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
				token(CLOSE_CURLY)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getBody().getClassDecls().size());
		
		ClassDecl decl = i.getBody().getClassDecls().get(0);
		assertEquals("A", decl.getId());
		assertNull(decl.getSuperType());
		assertEquals(1, decl.getMethodDecls().size());
		
		FunctionDecl fDecl = decl.getMethodDecls().get(0);
		assertEquals(1, fDecl.getReturnTypes().size());
		assertEquals(1, fDecl.getArgs().size());
	}	
	
	@Test
	void interfaceWithSingleClassDeclWithMultipleMethods() throws Exception {
		// class A {
		//   f(a:int):bool[]
		//   g(a:int):int, bool
		// }
		List<Token> tokens = elems(
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
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
				token(BOOL),
				token(CLOSE_CURLY)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getBody().getClassDecls().size());
		
		ClassDecl decl = i.getBody().getClassDecls().get(0);
		assertEquals("A", decl.getId());
		assertNull(decl.getSuperType());
		assertEquals(2, decl.getMethodDecls().size());
		
		FunctionDecl declTwo = decl.getMethodDecls().get(1);
		assertEquals(2, declTwo.getReturnTypes().size());
		assertEquals(0, declTwo.getArgs().size());
	}	
	
	//-------------------------------------------------------------------------------- 
	//- Interface
	//-------------------------------------------------------------------------------- 
	@Test
	void interfaceSingleUse() throws Exception {
		// use A
		List<Token> tokens = elems(
				token(USE),
				token(ID, "A")
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getUses().size());
	} 
	
	@Test
	void interfaceMultipleUse() throws Exception {
		// use A;
		// use B
		List<Token> tokens = elems(
				token(USE),
				token(ID, "A"),
				token(SEMICOLON),
				token(USE),
				token(ID, "B")
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(2, i.getUses().size());
	} 
	
	@Test
	void interfaceFunctionAndClass() throws Exception {
		// f(a:int):bool[]
		// class A{}
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
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(1, i.getBody().getClassDecls().size());
		assertEquals(1, i.getBody().getFunctionDecls().size());
	} 
	
	@Test
	void interfaceClassAndFunction() throws Exception {
		// class A {}
		// f(a:int):bool[]
		List<Token> tokens = elems(
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY),
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
		assertEquals(1, i.getBody().getClassDecls().size());
		assertEquals(1, i.getBody().getFunctionDecls().size());
	} 
	
	@Test
	void interfaceUseClassAndFunctionMedley() throws Exception {
		// use A;
		// use B;
		// class A {}
		// f(a:int):bool[]
		// class C
		// g(a:int):int[]
		List<Token> tokens = elems(
				token(USE),
				token(ID, "A"),
				token(SEMICOLON),
				token(USE),
				token(ID, "B"),
				token(SEMICOLON),
				token(CLASS),
				token(ID, "A"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY),
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
				token(CLASS),
				token(ID, "B"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY),
				token(CLASS),
				token(ID, "C"),
				token(OPEN_CURLY),
				token(CLOSE_CURLY),
				token(ID, "g"),
				token(OPEN_PAREN),
				token(ID, "a"),
				token(COLON),
				token(INT),
				token(CLOSE_PAREN),
				token(COLON),
				token(INT)
				);
		Interface i = parseInterfaceFromTokens(tokens);
		assertEquals(3, i.getBody().getClassDecls().size());
		assertEquals(2, i.getBody().getFunctionDecls().size());
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
	
	
	//-------------------------------------------------------------------------------- 
	//- Class Definitions
	//-------------------------------------------------------------------------------- 
	
	@Test
	void testEmptyClassDefn () throws Exception{
		// Class A {}
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());
		assertEquals(0, prog.getBody().getFunctionDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals("A", defn.getId());
		assertNull(defn.getSuperType());
		assertEquals(0, defn.getBody().getFields().size());
		assertEquals(0, defn.getBody().getMethodDefns().size());
	}
	
	@Test
	void testClassDefnExtends () throws Exception{
		// Class A extends B {}
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(EXTENDS),
 				token(ID, "B"),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());
		assertEquals(0, prog.getBody().getFunctionDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals("A", defn.getId());
		assertEquals("B", defn.getSuperType());
	}
	
	@Test
	void testClassDefnWithMethods() throws Exception{
		// Class A { f() {} g() {}}
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(ID, "f"),
 				token(OPEN_PAREN),
 				token(CLOSE_PAREN),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
				token(ID, "g"),
 				token(OPEN_PAREN),
 				token(CLOSE_PAREN),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals(2, defn.getBody().getMethodDefns().size());
		assertTrue(defn.getBody().getMethodDefns().get(0).getFunctionDecl().isMethod());
		
		ClassDecl decl = defn.getClassDecl();
		assertEquals("A", decl.getId());
		assertEquals(2, decl.getMethodDecls().size());
	}
	
	@Test
	void testClassDefnWithFields() throws Exception{
		// Class A { x:int y:int }
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(ID, "x"),
 				token(COLON),
 				token(INT),
 				token(ID, "y"),
 				token(COLON),
 				token(INT),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals(2, defn.getBody().getFields().size());
		
		ClassDecl decl = defn.getClassDecl();
		assertEquals("A", decl.getId());
		assertEquals(0, decl.getMethodDecls().size());
	}
		
	
	@Test
	void testClassDefnWithMethodAndField() throws Exception{
		// Class A { x:int f() {}}
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(ID, "x"),
 				token(COLON),
 				token(INT),
 				token(ID, "f"),
 				token(OPEN_PAREN),
 				token(CLOSE_PAREN),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals(1, defn.getBody().getFields().size());
		assertEquals(1, defn.getBody().getMethodDefns().size());
	}
	
	@Test
	void testClassDefnWithMethodsAndFieldsAnyOrder() throws Exception{
		// Class A { x:int f() {}}
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(ID, "x"),
 				token(COLON),
 				token(INT),
 				token(ID, "f"),
 				token(OPEN_PAREN),
 				token(CLOSE_PAREN),
 				token(OPEN_CURLY),
 				token(CLOSE_CURLY),
 				token(ID, "y"),
 				token(COLON),
 				token(INT),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());

		ClassDefn defn = prog.getBody().getClassDefns().get(0);
		assertEquals(2, defn.getBody().getFields().size());
		assertEquals(1, defn.getBody().getMethodDefns().size());
		
		ClassDecl decl = defn.getClassDecl();
		assertEquals("A", decl.getId());
		assertEquals(1, decl.getMethodDecls().size());
	}
	
	@Test
	void testClassDefnReturnThis() throws Exception{
		// Class A { f() : A { return this } }
		List<Token> tokens = elems(
				token(XI),
 				token(CLASS),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(ID, "f"),
 				token(OPEN_PAREN),
 				token(CLOSE_PAREN),
 				token(COLON),
 				token(ID, "A"),
 				token(OPEN_CURLY),
 				token(RETURN),
 				token(THIS),
 				token(CLOSE_CURLY),
 				token(CLOSE_CURLY),
				token(EOF)
				);
		Program prog = parseProg(tokens);

		assertEquals(1, prog.getBody().getClassDefns().size());
		
		Optional<Return> retStmt = firstClassReturnStatement(prog); 
		assertTrue(retStmt.isPresent());
		assertEquals(1, retStmt.get().getRetList().size());
	    assertInstanceOf(This.class, retStmt.get().getRetList().get(0));
	}
	
	//--------------------------------------------------------------------------------
	//- new keyword
	//--------------------------------------------------------------------------------
	
	@Test
	void singleAssignNew() throws Exception {
	    // x = new A.init()
		List<Token> tokens = elems(
				token(ID, "x"),
				token(EQ),
				token(NEW),
				token(ID, "A"),
				token(DOT),
				token(ID, "init"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN));
		Program prog = parseProgFromStmt(tokens);
	
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		assertInstanceOf(Var.class, assignStmt.getLhs());
		assertInstanceOf(New.class, assignStmt.getRhs());
	}
	
	@Test
	void singleAssignDeclNew() throws Exception {
	    // x : A = new A.init()
		List<Token> tokens = elems(
				token(ID, "x"),
				token(COLON),
				token(ID, "A"),
				token(EQ),
				token(NEW),
				token(ID, "A"),
				token(DOT),
				token(ID, "init"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN));
		Program prog = parseProgFromStmt(tokens);
	
		SingleAssign assignStmt = assertInstanceOfAndReturn(SingleAssign.class, firstStatement(prog));
		assertInstanceOf(SimpleDecl.class, assignStmt.getLhs());
		assertInstanceOf(New.class, assignStmt.getRhs());
	}
	
	@Test
	void returnNew() throws Exception {
		// return new A.init()
		List<Token> tokens = elems(
				token(RETURN), 
				token(NEW), 
				token(ID, "A"), 
				token(DOT),
				token(ID, "init"),
				token(OPEN_PAREN), 
				token(CLOSE_PAREN));
		Program prog = parseProgFromStmt(tokens);

		Optional<Return> retStmt = returnStatement(prog);
		assertTrue(retStmt.isPresent());
		assertEquals(1, retStmt.get().getRetList().size());
	    assertInstanceOf(New.class, retStmt.get().getRetList().get(0));
	}
	
	@Test
	void procedureCallNewArg() throws Exception {
		// f(new A.init()) 
		List<Token> tokens = elems(
				token(ID, "f"), 
				token(OPEN_PAREN), 
				token(NEW),
				token(ID, "A"), 
				token(DOT),
				token(ID, "init"),
				token(OPEN_PAREN), 
				token(CLOSE_PAREN),
				token(CLOSE_PAREN));
		Program prog = parseProgFromStmt(tokens);

		ProcedureCall fc = assertInstanceOfAndReturn(ProcedureCall.class, firstStatement(prog));
		List<Expr> args = fc.getFexp().getArgs(); 
		assertEquals(1, args.size());
		assertInstanceOf(New.class, args.get(0));
	}
	
	//--------------------------------------------------------------------------------
	//- MethodCall
	//--------------------------------------------------------------------------------
	@Test
	public void thisDotMethodCall() throws Exception {
		// this.f()
		List<Token> tokens = elems(
				token(THIS),
				token(DOT),
				token(ID, "f"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN));
		Program prog = parseProgFromExp(tokens);
		
		MethodCall mc = assertInstanceOfAndReturn(MethodCall.class, firstExp(prog));
		assertEquals("this", mc.getObj().getId());
		assertEquals("f", mc.getFExpr().getId());
		assertTrue(mc.getFExpr().isMethodCall());
	}
	
	@Test
	public void varDotMethodCall() throws Exception {
		// o.f()
		List<Token> tokens = elems(
				token(ID, "o"),
				token(DOT),
				token(ID, "f"),
				token(OPEN_PAREN),
				token(CLOSE_PAREN));
		Program prog = parseProgFromExp(tokens);
		
		MethodCall mc = assertInstanceOfAndReturn(MethodCall.class, firstExp(prog));
		assertEquals("o", mc.getObj().getId());
		assertEquals("f", mc.getFExpr().getId());
		assertTrue(mc.getFExpr().isMethodCall());
	}
	
	//--------------------------------------------------------------------------------
	//- FieldAccess
	//--------------------------------------------------------------------------------
	@Test
	public void thisFieldAccess() throws Exception {
		// this.p
		List<Token> tokens = elems(
				token(THIS),
				token(DOT),
				token(ID, "p"));
		Program prog = parseProgFromExp(tokens);
		
		FieldAccess fa = assertInstanceOfAndReturn(FieldAccess.class, firstExp(prog));
		assertEquals("this", fa.getObj().getId());
		assertEquals("p", fa.getField().getId());
	}
	
	@Test
	public void varFieldAccess() throws Exception {
		// o.p
		List<Token> tokens = elems(
				token(ID, "o"),
				token(DOT),
				token(ID, "p"));
		Program prog = parseProgFromExp(tokens);
		
		FieldAccess fa= assertInstanceOfAndReturn(FieldAccess.class, firstExp(prog));
		assertEquals("o", fa.getObj().getId());
		assertEquals("p", fa.getField().getId());
	}
		
	
	private void assertSyntaxError(TokenType expected, ParserError actual) {
		assertEquals(expected, tokenFromError(actual).getType());
	}

	private Token tokenFromError(ParserError errorInfo) {
		return errorInfo.getToken();
	}

	private Statement firstStatement(Program program) {
		return program.getBody().getFunctionDefns().get(0).getBody().getStmts().get(0);
	}

	private Optional<Return> returnStatement(Program program) {
		return program.getBody().getFunctionDefns().get(0).getBody().getReturnStmt();
	}
	
	private Optional<Return> firstClassReturnStatement(Program program) {
		return program.getBody()
			.getClassDefns()
			.get(0).getBody()
			.getMethodDefns()
			.get(0).getBody()
			.getReturnStmt();
	}

	private Expr firstExp(Program program) {
		SingleAssign assign = (SingleAssign) firstStatement(program);
		return assign.getRhs();
	}

	private Program parseProgFromExp(List<Token> exp) throws Exception {
		return parseProgFromStmt(expToStmt(exp));
	}
	
	private Program parseProgFromStmt(List<Token> stmt) throws Exception {
		return parseProg(stmtToProg(stmt));
	}
	
	private Program parseProg(List<Token> stmt) throws Exception {
		ParseResult parseResult = new ParseResult(setupParser(stmt));
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
