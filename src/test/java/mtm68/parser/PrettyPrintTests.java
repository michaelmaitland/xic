package mtm68.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.FileType;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.TokenFactory;


public class PrettyPrintTests {
	private StringWriter output = new StringWriter();
	private SExpPrinter p = new CodeWriterSExpPrinter(new PrintWriter(output));
	
	@Test
	void testAddErr() {
		String prog = "+++";
		String expected = "1:1 error:Unexpected token +";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testArrayInit() {
		String prog = "foo() {\r\n"
				+ "    a: int[] = {72,101,108,108,111}\r\n"
				+ "    a: int[] = \"Hello\"\r\n"
				+ "}";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= (a ([] int)) (72 101 108 108 111))\r\n"
				+ "    (= (a ([] int)) \"Hello\")\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testArrayInit2() {
		String prog = "foo() {\n"
				+ "    n: int = gcd(10, 2)\n"
				+ "    a: int[n]\n"
				+ "    while (n > 0) {\n"
				+ "      n = n - 1\n"
				+ "      a[n] = n\n"
				+ "    }\n"
				+ "}\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= (n int) (gcd 10 2))\r\n"
				+ "    (a ([] int n))\r\n"
				+ "    (while (> n 0)\r\n"
				+ "     ((= n (- n 1))\r\n"
				+ "      (= ([] a n) n)\r\n"
				+ "     )\r\n"
				+ "    )\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testBeauty() {
		String prog = "===============================================================================\r\n"
				+ "= This is a beautiful document heading, not a xi program, but it still lexes! =\r\n"
				+ "===============================================================================\r\n"
				+ "";
		String expected = "1:1 error:Unexpected token ==";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testEx1() {
		String prog = "use io\n"
				+ "\n"
				+ "main(args: int[][]) {\n"
				+ "  print(\"Hello, Worl\\x64!\\n\")\n"
				+ "  c3po: int = 'x' + 47;\n"
				+ "  r2d2: int = c3po // No Han Solo\n"
				+ "}\n"
				+ "";
		String expected = "( ((use io))\n"
				+ "  ( (main ((args ([] ([] int)))) ()\n"
				+ "      ( (print \"Hello, World!\\n\")\n"
				+ "        (= (c3po int) (+ 'x' 47))\n"
				+ "        (= (r2d2 int) c3po)\n"
				+ "      )\n"
				+ "    )\n"
				+ "  )\n"
				+ ")\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testEx2() {
		String prog = "foo(): bool, int {\n"
				+ "  expr: int = 1 - 2 * 3 * -4 *\n"
				+ "  5pred: bool = true & true | false;\n"
				+ "  if (expr <= 47) { }\n"
				+ "  else pred = !pred\n"
				+ "  if (pred) { expr = 59 }\n"
				+ "  return pred, expr;\n"
				+ "}\n"
				+ "\n"
				+ "bar() {\n"
				+ "  _, i: int = foo()\n"
				+ "  b: int[i][]\n"
				+ "  b[0] = {1, 0}\n"
				+ "}\n"
				+ "";
		String expected = "( ()\n"
				+ "  ( (foo () (bool int)\n"
				+ "      ( (= (expr int)\n"
				+ "          (- 1 (* (* (* 2 3) (- 4))\n"
				+ "                  5\n"
				+ "               )\n"
				+ "          )\n"
				+ "        )\n"
				+ "        (= (pred bool)\n"
				+ "          (| (& true true) false)\n"
				+ "        )\n"
				+ "        (if (<= expr 47)\n"
				+ "          ()\n"
				+ "          (= pred (! pred))\n"
				+ "        )\n"
				+ "        (if pred ((= expr 59)))\n"
				+ "        (return pred expr)\n"
				+ "      )\n"
				+ "    )\n"
				+ "    (bar () ()\n"
				+ "      ( (= (_ (i int)) (foo))\n"
				+ "        (b ([] ([] int) i))\n"
				+ "        (= ([] b 0) (1 0))\n"
				+ "      )\n"
				+ "    )\n"
				+ "  )\n"
				+ ")\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testEx3() {
		String prog = "+------------------------------------+\r\n"
				+ "| What a beautiful, invalid program! |\r\n"
				+ "+------------------------------------+\r\n"
				+ "";
		String expected = "1:1 error:Unexpected token +";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testGCD() {
		String prog = "// Return the greatest common divisor of two integers\r\n"
				+ "gcd(a:int, b:int):int {\r\n"
				+ "  while (a != 0) {\r\n"
				+ "    if (a<b) b = b - a\r\n"
				+ "    else a = a - b\r\n"
				+ "  }\r\n"
				+ "  return b\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((gcd ((a int) (b int)) (int)\r\n"
				+ "   ((while (!= a 0)\r\n"
				+ "     ((if (< a b)\r\n"
				+ "       (= b (- b a))\r\n"
				+ "       (= a (- a b))\r\n"
				+ "      )\r\n"
				+ "     )\r\n"
				+ "    )\r\n"
				+ "    (return b)\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testInsertionSort() {
		String prog = "sort(a: int[]) {\r\n"
				+ "  i:int = 0\r\n"
				+ "  n:int = length(a)\r\n"
				+ "  while (i < n) {\r\n"
				+ "      j:int = i\r\n"
				+ "      while (j > 0) {\r\n"
				+ "        if (a[j-1] > a[j]) {\r\n"
				+ "            swap:int = a[j]\r\n"
				+ "            a[j] = a[j-1]\r\n"
				+ "            a[j-1] = swap\r\n"
				+ "        }\r\n"
				+ "        j = j-1\r\n"
				+ "      }\r\n"
				+ "      i = i+1\r\n"
				+ "  }\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((sort ((a ([] int))) ()\r\n"
				+ "   ((= (i int) 0)\r\n"
				+ "    (= (n int) (length a))\r\n"
				+ "    (while (< i n)\r\n"
				+ "     ((= (j int) i)\r\n"
				+ "      (while (> j 0)\r\n"
				+ "       ((if (> ([] a (- j 1)) ([] a j))\r\n"
				+ "         ((= (swap int) ([] a j))\r\n"
				+ "          (= ([] a j) ([] a (- j 1)))\r\n"
				+ "          (= ([] a (- j 1)) swap)\r\n"
				+ "         )\r\n"
				+ "        )\r\n"
				+ "        (= j (- j 1))\r\n"
				+ "       )\r\n"
				+ "      )\r\n"
				+ "      (= i (+ i 1))\r\n"
				+ "     )\r\n"
				+ "    )\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testIO() {
		String prog = "// I/O support\r\n"
				+ "\r\n"
				+ "print(str: int[])     // Print a string to standard output.\r\n"
				+ "println(str: int[])   // Print a string to standard output, followed by a newline.\r\n"
				+ "readln() : int[]      // Read from standard input until a newline.\r\n"
				+ "getchar() : int       // Read a single character from standard input.\r\n"
				+ "                      // Returns -1 if the end of input has been reached.\r\n"
				+ "eof() : bool          // Test for end of file on standard input.\r\n"
				+ "";
		String expected = "(\r\n"
				+ "  (\r\n"
				+ "    (print ((str ([] int))) ())\r\n"
				+ "    (println ((str ([] int))) ())\r\n"
				+ "    (readln () (([] int)))\r\n"
				+ "    (getchar () (int))\r\n"
				+ "    (eof () (bool))\r\n"
				+ "  )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.IXI), expected);
	}
	
	@Test
	void testmdArrays() {
		String prog = "foo() {\r\n"
				+ "    a: int[][]\r\n"
				+ "    b: int[3][4]\r\n"
				+ "    a = b\r\n"
				+ "    c: int[3][]\r\n"
				+ "    c[0] = b[0]; c[1] = b[1]; c[2] = b[2]\r\n"
				+ "    d: int[][] = {{1, 0}, {0, 1}}\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((a ([] ([] int)))\r\n"
				+ "    (b ([] ([] int 4) 3))\r\n"
				+ "    (= a b)\r\n"
				+ "    (c ([] ([] int) 3))\r\n"
				+ "    (= ([] c 0) ([] b 0))\r\n"
				+ "    (= ([] c 1) ([] b 1))\r\n"
				+ "    (= ([] c 2) ([] b 2))\r\n"
				+ "    (= (d ([] ([] int))) ((1 0) (0 1)))\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testRatAdd() {
		String prog = "// Add two rational numbers p1/q1 and p2/q2, returning\r\n"
				+ "// a pair (p3, q3) representing their sum p3/q3.\r\n"
				+ "ratadd(p1:int, q1:int, p2:int, q2:int) : int, int {\r\n"
				+ "    g:int = gcd(q1,q2)\r\n"
				+ "    p3:int = p1*(q2/g) + p2*(q1/g)\r\n"
				+ "    return p3, q1/g*q2\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((ratadd ((p1 int) (q1 int) (p2 int) (q2 int)) (int int)\r\n"
				+ "   ((= (g int) (gcd q1 q2))\r\n"
				+ "    (= (p3 int) (+ (* p1 (/ q2 g)) (* p2 (/ q1 g))))\r\n"
				+ "    (return p3 (* (/ q1 g) q2))\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testRatAddUse() {
		String prog = "foo() {\r\n"
				+ "    p:int, q:int = ratadd(2, 5, 1, 3)\r\n"
				+ "    _, q':int = ratadd(1, 2, 1, 3)\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= ((p int) (q int)) (ratadd 2 5 1 3))\r\n"
				+ "    (= (_ (q' int)) (ratadd 1 2 1 3))\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	
	@Test
	void testSpec1() {
		String prog = "foo() {\r\n"
				+ "    x:int = 2;\r\n"
				+ "    z:int;\r\n"
				+ "    b: bool, i:int = f(x);\r\n"
				+ "    s: int[] = \"Hello\";\r\n"
				+ "}\r\n"
				+ "";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= (x int) 2)\r\n"
				+ "    (z int)\r\n"
				+ "    (= ((b bool) (i int)) (f x))\r\n"
				+ "    (= (s ([] int)) \"Hello\")\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testSpec2() {
		String prog = "foo() {\r\n"
				+ "  x = x + 1\r\n"
				+ "  s = {1, 2, 3}\r\n"
				+ "  b = !b\r\n"
				+ "}";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= x (+ x 1))\r\n"
				+ "    (= s (1 2 3))\r\n"
				+ "    (= b (! b))\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}
	
	@Test
	void testSpec3() {
		String prog = "foo() {\r\n"
				+ "    s: int[] = \"Hello\" + {13, 10}\r\n"
				+ "}";
		String expected = "(()\r\n"
				+ " ((foo () ()\r\n"
				+ "   ((= (s ([] int)) (+ \"Hello\" (13 10)))\r\n"
				+ "   )\r\n"
				+ "  )\r\n"
				+ " )\r\n"
				+ ")\r\n"
				+ "";
		
		testPrettyPrint(setUpParser(prog, FileType.XI), expected);
	}

	private String superTrim (String s) {
		return s.replaceAll("[\\n\\t\\r ]",  "").trim();
	}
	
	private void testPrettyPrint(Parser parser, String expected) {
		ParseResult result = new ParseResult(parser);
		
		if(result.isValidAST()) {
			result.getNode().get().prettyPrint(p); 
			p.flush();
			assertEquals(superTrim(expected), superTrim(output.toString()));
		}
		else {
			assertEquals(expected, result.getFirstError().getFileErrorMessage());
		}
	}
	
	private Parser setUpParser(String prog, FileType ft) {
		TokenFactory tokenFactory = new TokenFactory();
		Lexer lexer = new FileTypeLexer(new StringReader(prog), ft, tokenFactory);
		return new Parser(lexer, tokenFactory);
	}
}

