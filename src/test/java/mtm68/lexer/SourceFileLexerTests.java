package mtm68.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static mtm68.lexer.TokenType.*;

public class SourceFileLexerTests {
	
	private TokenFactory tFactory = new TokenFactory();
	@Test
	public void testEmpty() throws IOException {
		assertTrue(lex("empty", "").isEmpty());
	}
	
	@Test
	public void testEmptyLiteral() throws IOException {
		assertError("empty literal", "''");
	}

	@Test
	public void testValidIdentifier() throws IOException {
		List<Token> tokens = lex("valid_id", "a'_33");
		Token t = tFactory.newToken(ID, "a'_33", 1, 1);
		assertEquals(t , tokens.get(0));
	}

	@Test
	public void testEmptyString() throws IOException {
		List<Token> tokens = lex("empty_string", "\"\"");
		Token t = tFactory.newToken(STRING, "", 1, 1);
		assertEquals(t, tokens.get(0));
	}

	@Test
	public void testLongestMatch() throws IOException {
		List<Token> tokens = lex("longest_match", "iftrue");
		Token t = tFactory.newToken(ID, "iftrue", 1, 1);
		assertEquals(t, tokens.get(0));
	}

	@Test
	public void testNewlineToString() throws IOException {
		List<Token> tokens = lex("newline", "\"\\n\"");
		String rep = tokens.get(0).toString();
		assertEquals("1:1 string \\" + "n", rep);
	}

	@Test
	public void testHexLiterals() throws IOException {
		assertSingleToken(CHARACTER, '\uFFFF', "'\\xFFFF'");
		assertSingleToken(CHARACTER, '\u0000', "'\\x0000'");
		assertSingleToken(CHARACTER, '\u0000', "'\\x0'");
		assertSingleToken(CHARACTER, '\u000F', "'\\xF'");
		assertSingleToken(CHARACTER, '\uABCD', "'\\xabcd'");
		assertSingleToken(CHARACTER, 'A', "'\\x41'");
	}

	@Test
	public void testAllSingleTokens() throws IOException {
		assertSingleToken(USE, "use");
		assertSingleToken(IF, "if");
		assertSingleToken(ELSE, "else");
		assertSingleToken(RETURN, "return");
		assertSingleToken(LENGTH, "length");
		assertSingleToken(INT, "int");
		assertSingleToken(BOOL, "bool");
		assertSingleToken(TRUE, "true");
		assertSingleToken(FALSE, "false");

		assertSingleToken(ID, "hello", "hello");
		assertSingleToken(INTEGER, 56L, "56");
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
		Token t = tFactory.newToken(tt, attribute, 1, 1);
		List<Token> expected = Arrays.asList(t);

		assertEquals(expected, lexed);
	}

	private void assertSingleToken(TokenType tt, String input) throws IOException{
		assertSingleToken(tt, null, input);
	}
	
	private void assertError(String testName, String input) throws IOException {
		List<Token> tokens = lex(testName, input);
		assertTrue(!tokens.isEmpty());
		Token errorToken = tokens.get(0);
		assertEquals(error, errorToken.getType());
		assertTrue(errorToken.toString().contains("error:"));

	}
}

