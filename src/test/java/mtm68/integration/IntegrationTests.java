package mtm68.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import edu.cornell.cs.cs4120.ir.visit.CFGVisitor;
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
	void testArrayConcat() {
		generateAndAssertOutput("string_concat.xi", "Hello world!");
	}
	
	void testExtendedDecl() {
		generateAndAssertOutput("extended_decl.xi", "");
	}
	
	@Test
	void testAck() {
		generateAndAssertOutput("ack.xi", "Ack(2,11): 25\r\n");
	}
	
	@Test
	void testIfStmts() {
		generateAndAssertOutput("if_stmts.xi", "1123");
	}
	
	private void generateAndAssertOutput(String filename, String expected){
		try {
			IRNode irRoot = generateIRFromFile(filename);
			
//			CodeWriterSExpPrinter codeWriter = new CodeWriterSExpPrinter(new PrintWriter(System.out));
//			irRoot.printSExp(codeWriter);
//			codeWriter.flush();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.setOut(new PrintStream(baos));
				
			IRSimulator simulator = new IRSimulator((IRCompUnit) irRoot);
			simulator.call("_Imain_paai", 0);

			assertEquals(expected, baos.toString());
		} catch (FileNotFoundException | SemanticException e) {
			e.printStackTrace();
			fail();
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
		
		assertFalse("Found errors after collecting functions", funcCollector.hasError());
		
		TypeChecker typeChecker = new TypeChecker(funcTable);	
		program = typeChecker.performTypeCheck(program);
		
		assertFalse("Found errors after collecting functions", typeChecker.hasError());

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
		
		return irRoot;

	}
	
	
	
	
}
