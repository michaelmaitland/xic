package mtm68.parser;

import static mtm68.lexer.TokenType.*;
import static mtm68.util.ArrayUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.Statement;
import mtm68.lexer.MockLexer;
import mtm68.lexer.Token;
import mtm68.lexer.TokenFactory;
import mtm68.lexer.TokenType;

public class ParserTests {
	
	private ComplexSymbolFactory symFac = new ComplexSymbolFactory();
	private TokenFactory tokenFac = new TokenFactory();
	
	@Test
	void testParseIfNoElse() throws Exception {
		List<Token> ifTokens = elems(token(IF));
		ifTokens.addAll(arbitraryExp());
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		assertTrue(firstStatement(prog) instanceof If);
		
		If ifStmt = (If) firstStatement(prog);
		
		assertEquals(Optional.empty(), ifStmt.getElseBranch());
	}

	@Test
	void testParseIfWithElse() throws Exception {
		List<Token> ifTokens = elems(token(IF));
		ifTokens.addAll(arbitraryExp());
		ifTokens.addAll(arbitraryStmt());
		ifTokens.add(token(ELSE));
		ifTokens.addAll(arbitraryStmt());

		Program prog = parseProgFromStmt(ifTokens);
		
		assertTrue(firstStatement(prog) instanceof If);
		
		If ifStmt = (If) firstStatement(prog);
		
		assertTrue(ifStmt.getElseBranch().isPresent());
	}
	
	// f () { [INSERT HERE] }
	
	private Statement firstStatement(Program program) {
		return program.getFunctionDefns().get(0).getBody().getStmts().get(0);
	}
	
	private Program parseProgFromStmt(List<Token> stmt) throws Exception {
		return (Program) setupParser(stmtToProg(stmt)).parse().value;
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
