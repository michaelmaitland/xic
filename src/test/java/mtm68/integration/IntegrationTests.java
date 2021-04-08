package mtm68.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.interpret.IRSimulator;
import edu.cornell.cs.cs4120.ir.interpret.IRSimulator.Trap;
import edu.cornell.cs.cs4120.ir.visit.CFGVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import mtm68.FileType;
import mtm68.SymbolTableManager;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Program;
import mtm68.exception.SemanticException;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.TokenFactory;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.ErrorUtils;
import mtm68.visit.FunctionCollector;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;

public class IntegrationTests {
	
	@Test
	void testHelloWorld() {
		generateAndAssertOutput("hello_world.xi", "Hello world!");
	}
	
	@Test
	void testStringConcat() {
		generateAndAssertOutput("string_concat.xi", "Hello world!");
	}
	
	@Test
	void testArrayConcat() {
		generateAndAssertOutput("array_concat.xi", "POOP");
	}
	
	@Test
	void testExtendedDecl() {
		generateAndAssertOutput("extended_decl.xi", "");
	}
	
	@Test
	void testAck() {
		generateAndAssertOutput("ack.xi", "Ack(2,11): 25\n");
	}
	
	@Test
	void testIfStmts() {
		generateAndAssertOutput("if_stmts.xi", "1123");
	}

	@Test
	void testNestedIfStmts() {
		generateAndAssertOutput("nested_if_stmts.xi", "1234");
	}	
	@Test
	void testEx01(){
		generateAndAssertOutput("ex01.xi", "Hello, World!\n");
	}
	
	@Test
	void testPrimes() {
		generateAndAssertOutput("primes.xi", "Largest prime less than 1,000 is 997");
	}
	
	@Test
	void testArrayAdventure() {
		generateAndAssertOutput("array_adventure.xi", "64\n0\n6\n");
	}
	
	@Test
	void testRandFeatures() {
		generateAndAssertOutput("rand_features.xi", "Hello6\n4\n8\n1\n9\n-15\n");
	}
	
	@Test
	void testMultiReturn() {
		generateAndAssertOutput("multi_return.xi", "(2, 3)\n(1, 2)\n1\nfirst second\n");
	}
	
	@Test
	void testEmptyArray() {
		generateAndAssertOutput("empty_array_explore.xi", "Just this\nJust this\n\n");
	}
	
	@Test
	void testExtdDeclConcat() {
		generateAndAssertOutput("extended_decl_concat.xi", "Hello how are you ?! ");
	}
	
	@Test
	void testArrNegDim() {
		generateAndAssertError("array_negdim_err.xi", "Out of bounds!");
	}
	
	@Test
	void testArrOutOfBounds() {
		generateAndAssertError("arr_out_of_bounds.xi", "Out of bounds!");
	}
	
	@Test
	void testBoolArray() {
		generateAndAssertOutput("bool_array.xi", "Success!\n");
	}
	
	private void generateAndAssertOutput(String filename, String expected){
		try {
			IRNode irRoot = generateIRFromFile(filename);
			
//			CodeWriterSExpPrinter codeWriter = new CodeWriterSExpPrinter(new PrintWriter(System.out));
//			irRoot.printSExp(codeWriter);
//			codeWriter.flush();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
			IRSimulator simulator = new IRSimulator((IRCompUnit) irRoot, baos);
			simulator.call("_Imain_paai", 0);

			assertEquals(expected, baos.toString());
		} catch (FileNotFoundException | SemanticException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private void generateAndAssertError(String filename, String expected){
		try {
			IRNode irRoot = generateIRFromFile(filename);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			
			IRSimulator simulator = new IRSimulator((IRCompUnit) irRoot, baos);
			simulator.call("_Imain_paai", 0);
				
			fail("No error thrown");
		} catch (FileNotFoundException | SemanticException e) {
			e.printStackTrace();
			fail();
		} catch (Trap e) {
			assertEquals(expected, e.getMessage());
		}
	}

	private IRNode generateIRFromFile(String filename) throws FileNotFoundException, SemanticException {

		Path testFilePath = Paths.get("src/test/resources/testfiles");
		Path libPath = Paths.get("src/test/resources/testlib");
		
		// LEX AND PARSE
		TokenFactory tokenFactory = new TokenFactory();
		Lexer lexx = new FileTypeLexer(filename, testFilePath, FileType.parseFileType(filename), tokenFactory);
		Parser parser = new Parser(lexx, tokenFactory);
		ParseResult parseResult = new ParseResult(parser);
		
		ErrorUtils.printErrors(parseResult, filename);
		
		assertTrue("Found errors in parse stage", parseResult.isValidAST());
				
		Program program = (Program) parseResult.getNode().get();
		
		// TYPECHECK
		SymbolTableManager symTableManager = new SymbolTableManager(libPath);
		Map<String, FunctionDecl> libFuncTable = symTableManager.mergeSymbolTables((Program) program);
		
		FunctionCollector funcCollector = new FunctionCollector(libFuncTable);
		Map<String, FunctionDecl> funcTable = funcCollector.visit(program);

		ErrorUtils.printErrors(funcCollector.getErrors(), filename);

		assertFalse("Found errors after collecting functions", funcCollector.hasError());
		
		TypeChecker typeChecker = new TypeChecker(funcTable);	
		program = typeChecker.performTypeCheck(program);
		
		ErrorUtils.printErrors(typeChecker.getTypeErrors(), filename);
		
		assertFalse("Found errors after typechecking", typeChecker.hasError());

		//TRANSFORM TO IRCODE
		IRNodeFactory nodeFactory = new IRNodeFactory_c();
		
		NodeToIRNodeConverter irConverter = new NodeToIRNodeConverter(filename, nodeFactory, new ArrayList<>(funcTable.values()));
		Lowerer lowerer = new Lowerer(nodeFactory);
		IRConstantFolder constFolder = new IRConstantFolder(nodeFactory);
		CFGVisitor cfgVisitor = new CFGVisitor(nodeFactory);
		UnusedLabelVisitor unusedLabelVisitor = new UnusedLabelVisitor(nodeFactory);
		
		program = irConverter.performConvertToIR(program);
		program.getIrCompUnit().appendFunc(irConverter.allocLayer());
		
		IRNode irRoot = lowerer.visit(program.getIrCompUnit());
		irRoot = constFolder.visit(irRoot);
		irRoot = cfgVisitor.visit(irRoot);
		irRoot = unusedLabelVisitor.visit(irRoot);
		
		CheckCanonicalIRVisitor canonVisitor = new CheckCanonicalIRVisitor();
		CheckConstFoldedIRVisitor constFoldVisitor = new CheckConstFoldedIRVisitor();
		
		canonVisitor.visit(irRoot);		
		assertNull(canonVisitor.noncanonical());
		assertTrue("IRNode is not properly folded" , constFoldVisitor.visit(irRoot));
		
		return irRoot;
	}
	
	
	
	
}
