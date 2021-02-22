package mtm68.lexer;

import static mtm68.lexer.Lexer.TokenType.IF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.lexer.Lexer.Token;
import mtm68.lexer.Lexer.TokenType;
import static mtm68.lexer.Lexer.TokenType.*;

public class SourceFileLexerTests {
	
	@Test
	public void testEmpty() throws IOException {
		assertTrue(lex("empty", "").isEmpty());
	}

	@Test
	public void testAllSingleTokens() throws IOException {
		assertSingleToken(USE, "use");
		assertSingleToken(IF, "if");
		assertSingleToken(ELSE, "else");
		assertSingleToken(RETURN, "return");
		assertSingleToken(LENGTH, "length");
		assertSingleToken(INT_T, "int");
		assertSingleToken(BOOL_T, "bool");
		assertSingleToken(TRUE, "true");
		assertSingleToken(FALSE, "false");

		assertSingleToken(ID, "hello", "hello");
		assertSingleToken(INT, 56L, "56");
		assertSingleToken(CHARACTER, 'c', "'c'");
		assertSingleToken(STRING, "hello", "\"hello\"");

		assertSingleToken(DOT, ".");
		assertSingleToken(OPEN_SQUARE, "[");
		assertSingleToken(CLOSE_SQUARE, "]");
		assertSingleToken(OPEN_PAREN, "(");
		assertSingleToken(CLOSE_PAREN, ")");
		assertSingleToken(OPEN_CURLY, "{");
		assertSingleToken(CLOSE_CURLY, "}");
		assertSingleToken(EXCLAMATION, "!");
		assertSingleToken(COLON, ":");
		assertSingleToken(SEMICOLON, ";");
		assertSingleToken(COMMA, ",");
		assertSingleToken(EQ, "=");
		assertSingleToken(UNDERSCORE, "_");

		assertSingleToken(ADD, "+");
		assertSingleToken(SUB, "-");
		assertSingleToken(MULT, "*");
		assertSingleToken(DIV, "/");
		assertSingleToken(MOD, "%");
		assertSingleToken(HIGH_MULT, "*>>");
		assertSingleToken(LT, "<");
		assertSingleToken(LEQ, "<=");
		assertSingleToken(GT, ">");
		assertSingleToken(GEQ, ">=");
		assertSingleToken(EQEQ, "==");
		assertSingleToken(NEQ, "!=");
		assertSingleToken(AND, "&");
		assertSingleToken(OR, "|");
	}
	
	private List<Token> lex(String testName, String input) throws IOException {
		return new SourceFileLexer(testName + ".xi", new StringReader(input)).getTokens();
	}
	
	private void assertSingleToken(TokenType tt, Object attribute, String input) throws IOException{
		List<Token> lexed = lex("single", input);
		List<Token> expected = Arrays.asList(new Token(tt, attribute, 1, 1));

		assertEquals(expected, lexed);
	}

	private void assertSingleToken(TokenType tt, String input) throws IOException{
		assertSingleToken(tt, null, input);
	}
}

