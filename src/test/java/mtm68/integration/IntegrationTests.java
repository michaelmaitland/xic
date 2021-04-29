package mtm68.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import mtm68.FileType;
import mtm68.SymbolTableManager;
import mtm68.assem.Assem;
import mtm68.assem.CompUnitAssem;
import mtm68.assem.visit.TrivialRegisterAllocator;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Program;
import mtm68.exception.SemanticException;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.TokenFactory;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.ArrayUtils;
import mtm68.util.ErrorUtils;
import mtm68.util.FileUtils;
import mtm68.visit.FunctionCollector;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;

public class IntegrationTests {
	private static final OSType OS = OSType.getOSType(System.getProperty("os.name").toLowerCase());
	private static final int BUFFER_SIZE = 1024;
	
//	@Test
//	void testBasic() {
//		Assem assem = new SeqAssem(new LabelAssem("_Imain_paai"));
//		runAndAssertAssem(assem, "Hello world!");
//	}
	
	@Test
	void testSimplePrint() {
		generateAndAssertOutput("simple_print.xi", "a");
	}
	
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
	
	@Test
	void testDivByZero() {
		generateAndAssertError("div_by_zero.xi", "Division by zero!");	
	}
	
	@Test
	void testFibboncacci() {
		generateAndAssertOutput("fib.xi", "34");
	}
	
	@Test
	void testIterativeFibboncacci() {
		generateAndAssertOutput("iterative_fib.xi", "34");
	}	
	@Test
	void testNestedSideEffect() {
		generateAndAssertOutput("nested_side_effect.xi", "1234");
	}
	
	@Test
	void testSimpleArray() {
		generateAndAssertOutput("simple_array.xi", "Just this\n");
	}
	
	@Test
	void testPrintIfVarZero() {
		generateAndAssertOutput("print_if_zero.xi", "true\n");
	}
	
	@Test
	void testBinary06() {
		generateAndAssertOutput("binary06.xi");
	}
	
	@Test
	void testBinary12() {
		generateAndAssertOutput("binary12.xi");
	}
	
	@Test
	void testBinOpExplosion() {
		generateAndAssertOutput("binop_explosion.xi", "1100100101110182-12-84602230521");
	}
	
	private void generateAndAssertOutput(String filename) {
		String resFilename = filename.replaceFirst("\\.(xi|ixi)", ".res");
		Path resultFile = Paths.get("src/test/resources/testfile_results/" + resFilename);
		
		StringBuilder expected = new StringBuilder();
		 
        try (Stream<String> stream = Files.lines(resultFile)) 
        {
            stream.forEach(s -> expected.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        generateAndAssertOutput(filename, expected.toString());
	}
	
	private void generateAndAssertOutput(String filename, String expected){
		try {
			IRNode irRoot = generateIRFromFile(filename);
			
			String assemPathStr = "src/test/resources/runtime/release";

			FileUtils.diagPath = Paths.get(assemPathStr);
			FileUtils.assemPath = Paths.get(assemPathStr);
			FileUtils.writeToFile("unitTest.ir", irRoot); 
			
			CodeWriterSExpPrinter codeWriter = new CodeWriterSExpPrinter(new PrintWriter(System.out));
			irRoot.printSExp(codeWriter);
			codeWriter.flush();
		
			assertIRSimulatorOutput(irRoot, expected);
			
			List<Assem> assem = generateAssem(irRoot);
			runAndAssertAssem(assem, expected);
			
		} catch (FileNotFoundException | SemanticException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private void assertIRSimulatorOutput(IRNode root, String expected) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		IRSimulator simulator = new IRSimulator((IRCompUnit) root, baos);
		simulator.call("_Imain_paai", 0);

		assertEquals(expected, baos.toString());
	}
	
	private List<Assem> generateAssem(IRNode root) {
		Tiler tiler = new Tiler(new IRNodeFactory_c());
		IRNode tiled = tiler.visit(root);
		
		//System.out.println(tiled.getAssem());
		
		TrivialRegisterAllocator regAllocator = new TrivialRegisterAllocator();
		
		return regAllocator.allocate((CompUnitAssem) tiled.getAssem());
	}
	
	private void runAndAssertAssem(List<Assem> assems, String expected) {
		try {
			assems.forEach(System.out::println);
			Path pwd = Paths.get(System.getProperty("user.dir"));			
		
			String assemPathStr = "src/test/resources/runtime/release";
			
			FileUtils.writeAssemToFile("unitTest.xi", assems);
			
			// Run linkxi.sh to generate executable
			ProcessBuilder link = getProcessBuilder(assemPathStr + "/linkxi.sh", assemPathStr + "/unitTest.s"); 
			Process linkProc = link.start();
					
			linkProc.waitFor();
			
			BufferedReader stdLinkOut = new BufferedReader(new InputStreamReader(linkProc.getInputStream()));
			String s = null;
			
			if(!Files.exists(pwd.resolve("a.out"))) {
				System.out.println("linkxi.sh encounted the following error:\n");
				while ((s = stdLinkOut.readLine()) != null) {
				    System.out.println(s);
				}
				fail();
			} else {
				makeFileExecutable(pwd, "a.out");
			}
			
			// Run executable and compare output to console with expected value
			ProcessBuilder runAssem = getProcessBuilder("./a.out");
			Process runProc = runAssem.start();
							
			BufferedReader assemOut = new BufferedReader(new InputStreamReader(runProc.getInputStream()));
			
			CharBuffer cb = CharBuffer.allocate(BUFFER_SIZE);
			assemOut.read(cb);
			
			String assemOutput = cb.flip().toString();
			
			runProc.waitFor();
			
			//Files.delete(FileUtils.assemPath.resolve("unitTest.s"));
			Files.delete(pwd.resolve("a.out"));
			
			assertEquals(expected, assemOutput);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void makeFileExecutable(Path pwd, String filename) throws IOException {
		// make a.out executable
		if(OS != OSType.WINDOWS) {
			Set<PosixFilePermission> perms = Files.getPosixFilePermissions(pwd.resolve("a.out"));
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			Files.setPosixFilePermissions(pwd.resolve("a.out"), perms);
		}
	}

	private ProcessBuilder getProcessBuilder(String... commandAndArgs) {
		List<String> commandList = new ArrayList<>(Arrays.asList(commandAndArgs));		
		if(OS == OSType.WINDOWS) ArrayUtils.prepend("wsl", commandList);
		
		return new ProcessBuilder(commandList);
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
	
	private enum OSType{
		WINDOWS,
		LINUX;
		
		static OSType getOSType(String os) {
			if(os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				return LINUX;
			}
			else return WINDOWS;
		}
	}
}
