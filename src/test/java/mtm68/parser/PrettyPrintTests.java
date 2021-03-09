package mtm68.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import mtm68.FileType;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Use;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.binary.Add;
import mtm68.ast.nodes.stmts.Block;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.util.ArrayUtils;


public class PrettyPrintTests {
	private StringWriter output = new StringWriter();
	private SExpPrinter p = new CodeWriterSExpPrinter(new PrintWriter(output));
	
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
	void testAddErr() {
		String prog = "+++";
		String expected = "1:1 error:Unexpected token";
		
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
			assertEquals(expected, result.getFirstSyntaxError().toString());
		}
	}
	
	private Parser setUpParser(String prog, FileType ft) {
		Lexer lexer = new FileTypeLexer(new StringReader(prog), ft);
		return new Parser(lexer, new ComplexSymbolFactory());
	}
}

