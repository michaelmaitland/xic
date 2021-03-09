package mtm68.parser;

import static mtm68.lexer.TokenType.*;
import static mtm68.util.ArrayUtils.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.LessThan;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.ast.nodes.stmts.Statement;
import mtm68.ast.types.Types;
import mtm68.exception.SyntaxErrorInfo;
import mtm68.lexer.MockLexer;
import mtm68.lexer.Token;
import mtm68.lexer.TokenFactory;
import mtm68.lexer.TokenType;

public class ParserTests {
	
	private ComplexSymbolFactory symFac = new ComplexSymbolFactory();
	private TokenFactory tokenFac = new TokenFactory();
	
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
	void singleAssignWithArrayDecl() throws Exception {
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
		assertInstanceOf(MultipleAssign.class, firstStatement(prog));
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
	void parseIfNoElse() throws Exception {
		List<Token> ifTokens = elems(token(IF));
		ifTokens.addAll(arbitraryExp());
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		If ifStmt = assertInstanceOfAndReturn(If.class, firstStatement(prog));
		assertEquals(Optional.empty(), ifStmt.getElseBranch());
	}

	@Test
	void parseIfWithElse() throws Exception {
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
	void parseIfWithParens() throws Exception {
		List<Token> ifTokens = elems(token(IF));
		ifTokens.add(token(OPEN_PAREN));
		ifTokens.addAll(arbitraryExp());
		ifTokens.add(token(CLOSE_PAREN));
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		
		If ifStmt = assertInstanceOfAndReturn(If.class, firstStatement(prog));
		assertEquals(Optional.empty(), ifStmt.getElseBranch());
	}
	
	@Test
	void parseBlockSingleSemiSyntaxError() throws Exception {
		List<Token> tokens = elems(token(SEMICOLON));
		
		SyntaxErrorInfo error = parseErrorFromStmt(tokens);
		assertSyntaxError(SEMICOLON, error);
	}

	//-------------------------------------------------------------------------------- 
	//- Precedence 
	//-------------------------------------------------------------------------------- 

	@Test
	void arrayIndexHigherPrecedence() throws Exception {
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
	
	// f () { [INSERT HERE] }
	
	private void assertSyntaxError(TokenType expected, SyntaxErrorInfo actual) {
		assertEquals(expected, tokenFromError(actual).getType());
	}

	private Token tokenFromError(SyntaxErrorInfo errorInfo) {
		return errorInfo.getToken();
	}

	private Statement firstStatement(Program program) {
		return program.getFunctionDefns().get(0).getBody().getStmts().get(0);
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

	private SyntaxErrorInfo parseErrorFromStmt(List<Token> stmt) throws Exception {
		ParseResult parseResult = new ParseResult(setupParser(stmtToProg(stmt)));
		return parseResult.getFirstSyntaxError();
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
	
	@SuppressWarnings("unchecked")
	private <T> T assertInstanceOfAndReturn(Class<T> clazz, Object obj) {
		assertTrue(obj.getClass() + " is not an instanceof " + clazz , clazz.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}

	private <T> void assertInstanceOf(Class<T> clazz, Object obj) {
		assertInstanceOfAndReturn(clazz, obj);
	}

}
