package mtm68.lexer;

import static mtm68.lexer.TokenType.ADD;
import static mtm68.lexer.TokenType.AND;
import static mtm68.lexer.TokenType.BOOL;
import static mtm68.lexer.TokenType.CHARACTER;
import static mtm68.lexer.TokenType.CLOSE_CURLY;
import static mtm68.lexer.TokenType.CLOSE_PAREN;
import static mtm68.lexer.TokenType.CLOSE_SQUARE;
import static mtm68.lexer.TokenType.COLON;
import static mtm68.lexer.TokenType.COMMA;
import static mtm68.lexer.TokenType.DIV;
import static mtm68.lexer.TokenType.ELSE;
import static mtm68.lexer.TokenType.EQ;
import static mtm68.lexer.TokenType.EQEQ;
import static mtm68.lexer.TokenType.EXCLAMATION;
import static mtm68.lexer.TokenType.FALSE;
import static mtm68.lexer.TokenType.GEQ;
import static mtm68.lexer.TokenType.GT;
import static mtm68.lexer.TokenType.HIGH_MULT;
import static mtm68.lexer.TokenType.ID;
import static mtm68.lexer.TokenType.IF;
import static mtm68.lexer.TokenType.INT;
import static mtm68.lexer.TokenType.INTEGER;
import static mtm68.lexer.TokenType.LENGTH;
import static mtm68.lexer.TokenType.LEQ;
import static mtm68.lexer.TokenType.LT;
import static mtm68.lexer.TokenType.MOD;
import static mtm68.lexer.TokenType.MULT;
import static mtm68.lexer.TokenType.NEQ;
import static mtm68.lexer.TokenType.OPEN_CURLY;
import static mtm68.lexer.TokenType.OPEN_PAREN;
import static mtm68.lexer.TokenType.OPEN_SQUARE;
import static mtm68.lexer.TokenType.OR;
import static mtm68.lexer.TokenType.RETURN;
import static mtm68.lexer.TokenType.SEMICOLON;
import static mtm68.lexer.TokenType.STRING;
import static mtm68.lexer.TokenType.SUB;
import static mtm68.lexer.TokenType.TRUE;
import static mtm68.lexer.TokenType.UNDERSCORE;
import static mtm68.lexer.TokenType.USE;
import static mtm68.lexer.TokenType.error;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

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

	// xic-ref (--lex [basic test]): char02.xi 
	@Test
	public void testSingleQuoteChar() throws IOException {
	List<Token> tokens = lex("single quote char", "'\\''");
		Token t = tFactory.newToken(CHARACTER, "'", 1, 1);
		assertEquals(t , tokens.get(0));
	}

	// xic-ref (--lex [basic test]): char03.xi
	@Test
	public void testDoubleQuoteChar() throws IOException {
	List<Token> tokens = lex("double quote char", "'\\\"'");
		Token t = tFactory.newToken(CHARACTER, "\"", 1, 1);
		assertEquals(t , tokens.get(0));

	}

	// xic-ref (--lex [basic test]): int06.xi
	@Test
	public void testMinInt() throws IOException {
	List<Token> tokens = lex("min int", "-9223372036854775808");
		Token t1 = tFactory.newToken(SUB, 1, 1);
		Token t2 = tFactory.newToken(INTEGER, new BigInteger("9223372036854775808"), 1, 2);
		assertEquals(t1 , tokens.get(0));
		assertEquals(t2 , tokens.get(1));
	}

	// xic-ref (--lex [basic test]): string01.xi
		@Test
	public void testEscapedDoubleQuoteString() throws IOException {
	List<Token> tokens = lex("double quote char", "\"\\\"\"");
		Token t = tFactory.newToken(STRING, "\"", 1, 1);
		assertEquals(t , tokens.get(0));
	}

	// xic-ref (--lex [basic test]): string03.xi
	@Test
	public void testSingleQuoteString() throws IOException {
		assertError("single quote String", "\"'\"");
	}

	// xic-ref (--lex [basic test]): string05.xi
	@Test
	public void testDoubleQuoteWithTextString() throws IOException {
	List<Token> tokens = lex("double quote with text", "\"\\\" This should work \"");
		Token t = tFactory.newToken(STRING, "\" This should work ", 1, 1);
		assertEquals(t , tokens.get(0));
	}

	// xic-ref (--lex [basic test]): string09.xi
	@Test
	public void testEscapedBackslashEscapedDoubleQuoteString() throws IOException {
	List<Token> tokens = lex("escaped backslash escaped double quote string", "\"\\\\\\\"\"");
		Token t = tFactory.newToken(STRING, "\\\"", 1, 1);
		assertEquals(t , tokens.get(0));
	}

	// xic-ref (--lex [basic-error test]): char05.xi
	@Test
	public void testCharLiteralWithManyQuotesProducesError() throws IOException {
		assertError("char literal with many quotes ", "''''''''''''''''''''''''''''''''''''''''");
	}

	// xic-ref (--lex [basic-error test]): string01.xi
	@Test
	public void stringWithUnescapedBackslashProducesError() throws IOException {
		assertError("string with unescaped back slash", "\"\\\"");
	}

	// xic-ref (--lex [basic-error test]): string02.xi
	@Test
	public void doubleQuoteProducesError() throws IOException {
		assertError("double quote", "\"");
	}

	// xic-ref (--lex [basic-error test]): string03.xi
	@Test
	public void invalidCharEscapedInString() throws IOException {
		assertError("invalid char escaped in string", "\"\\q\"");
	}

	// xic-ref (--lex [basic-error test]): string04.xi
	@Test
	public void invalidHexEscapedInString() throws IOException {
		assertError("invalid char escaped in string", "\"\\x\"");
	}

	// xic-ref (--lex [basic-error test]): string05.xi
	@Test
	public void escapedBackslashAndUnescapedBackslashProducesError() throws IOException {
		assertError("escaped Backslash And Unescaped Backslash", "\"\\\\\\\"");
	}

	// xic-ref (--lex [basic-error test]): string06.xi
	@Test
	public void backslashSpaceProducesError() throws IOException {
		assertError("backslash space", "\"\\ \\ \\ \"");
	}

	// xic-ref (--lex [basic-error test]): string07.xi
	@Test
	public void testStringWithManyUnescapedQuotesProducesError() throws IOException {
		assertError("string with many double quotes ", "\"\"\"");
	}

	// xic-ref (--lex [basic-error test]): string08.xi
	// xic-ref (--lex [basic-error test]): string09.xi
	@Test
	public void testStringThatDoesNotCloseProducesError() throws IOException {
		assertError("string that does not close", "\"abcdef");
	}

	// xic-ref (--lex [combo test]): medley01.xi
	@Test
	public void testMedley() throws IOException {
		List<Token> tokens = lex("medley", "bool''''x'x'x'x_x2451'x:bool=5+5<=5555bool5:int[]=\"55bool\\x2F\";'5';'\\'';\n"
				+ "//at694_cjb328_dbd64_ecp84");
		Token t = tFactory.newToken(CHARACTER, "\'", 1, 68);
		assertEquals(t , tokens.get(19));
	}

	// xic-ref (--lex [combo test]): string05.xi
	@Test
	public void testEscapedStrings() throws IOException {
		List<Token> tokens = lex("escaped strings", "\"Hello \"\"\\\"World\\\"\"\"; x = \"\"\\\\\""); 
		Token t = tFactory.newToken(STRING, "\"World\"", 1, 9);
		assertEquals(t , tokens.get(1));
	}
	
	// xic-ref (--lex [extension-error test (might succeed)]): string01.xi
	@Test
	public void testMultiLineString() throws IOException {
		assertError("multi line string", "\"This might\nnot work\"");
	}
	
	// xic-ref (--lex [extension-error test (might succeed)]): string02.xi
	@Test
	public void testMultiLineEmptyString() throws IOException {
		assertError("multi line empty string", "\"\n\"");
	}

	// xic-ref (Test --lex): lex05.xi
	@Test
	public void testEscapedSingleQuoteCharLiteralInMedley() throws IOException {
		List<Token> tokens = lex("Escaped single quote char literal in medley", "if ( a >= '\\'' ) return x else return y");
		Token t = tFactory.newToken(CHARACTER, "'", 1, 11);
		assertEquals(t, tokens.get(4));
	}

	// xic-ref (Test --lex): largeintliteral.xi
	@Test
	public void testLargeIntLiteralInMedley() throws IOException {
		List<Token> tokens = lex("LargeIntLiteralInMedley", "main(args: int[][]) {\n"
				+ "    b: int = 1000000000000000000000000000000;\n"
				+ "}");
		Token t = tFactory.newToken(INTEGER, "1000000000000000000000000000000", 2, 14);
		assertEquals(t, tokens.get(15));
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
		assertSingleToken(INTEGER, new BigInteger("56"), "56");
		assertSingleToken(CHARACTER, 'c', "'c'");
		assertSingleToken(STRING, "hello", "\"hello\"");

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
		Token errorToken = null;
		for(Token t : tokens) {
			if(t.getType()== error) errorToken = t;
		}
		assertNotNull(errorToken);
		assertEquals(error, errorToken.getType());
		assertTrue(errorToken.toString().contains("error:"));

	}
}

