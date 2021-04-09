package mtm68;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.interpret.IRSimulator;
import edu.cornell.cs.cs4120.ir.visit.CFGVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Program;
import mtm68.exception.BaseError;
import mtm68.exception.SemanticError;
import mtm68.exception.SemanticException;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.SourceFileLexer;
import mtm68.lexer.Token;
import mtm68.lexer.TokenFactory;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.Debug;
import mtm68.util.ErrorUtils;
import mtm68.util.FileUtils;
import mtm68.visit.FunctionCollector;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;

public class Main {

	@Option(name = "--help", help = true, usage = "print help screen")
	private boolean help = false;

	@Option(name = "--lex", usage = "saves lexed tokens from source file to <filename>.lexed")
	private boolean outputLex;
	
	@Option(name = "--parse", usage = "saves AST generated from source file to <filename>.parsed")
	private boolean outputParse;
	
	@Option(name = "--typecheck", usage = "saves result of typechecking AST generated from source file to <filename>.typed")
	private boolean outputTypeCheck;
	
	@Option(name = "--irgen", usage = "saves IR representation of AST generated from source file to <filename>.ir")
	private boolean outputIR;
	
	@Option(name = "--irrun", usage = "generates and interprets IR code")
	private boolean interpretIR;

	@Option(name = "--debug", usage = "turns on debug output", hidden = true)
	private boolean debug;
	
	@Option(name = "-sourcepath", usage = "specify path to source files")
	private Path sourcePath = Paths.get(System.getProperty("user.dir"));

	@Option(name = "-D", usage = "specify location for generated diagnostic files")
	private Path dPath = Paths.get(System.getProperty("user.dir"));
	
	@Option(name = "-libpath", usage = "specify path to library interface files")
	private Path libPath = Paths.get(System.getProperty("user.dir"));
	
	@Option(name = "-O", usage = "disable optimizations")
	private boolean doNotOptimize;

	@Argument
	private List<String> sourceFiles = new ArrayList<>();

	public static void main(String[] args) {
		try {
			new Main().parseCmdLine(args);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to compile: xic exit code 1");
			System.exit(1);
		}
	}
	
	/**
	 * Parses command line options and arguments. Acts on those arguments
	 * 
	 * @param args   the array of program arguments
	 */
	public void parseCmdLine(String[] args) throws Exception {
		CmdLineParser cmdParser = new CmdLineParser(this, ParserProperties.defaults().withShowDefaults(false));

		try {
			cmdParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.out.println(e.getMessage());
		}
		
		FileUtils.dPath = dPath;
		Debug.DEBUG_ON = debug;

		if (help || sourceFiles.isEmpty())
			printHelpScreen(cmdParser);

		SymbolTableManager symTableManager = new SymbolTableManager(libPath);
		
		// Ensure ixi files are handled first
		sourceFiles.sort(new Comparator<String>() {
			public int compare(String obj1, String obj2) {
				if(FileType.parseFileType(obj1).equals(FileType.IXI)) return -1;
				if(FileType.parseFileType(obj2).equals(FileType.IXI)) return 1;
				return 0;
		    }
		});
		
		Map<String, List<FunctionDecl>> progFuncDecls = new HashMap<>();
		Map<String, Program> programs = getValidPrograms(symTableManager, progFuncDecls);
		
		IRNodeFactory nodeFactory = new IRNodeFactory_c();
		for(String programName : programs.keySet()) {
			Program program = programs.get(programName);
			

			NodeToIRNodeConverter irConverter = new NodeToIRNodeConverter(programName, nodeFactory, progFuncDecls.get(programName));
			Lowerer lowerer = new Lowerer(nodeFactory);
			IRConstantFolder constFolder = new IRConstantFolder(nodeFactory);
			CFGVisitor cfgVisitor = new CFGVisitor(nodeFactory);
			UnusedLabelVisitor unusedLabelVisitor = new UnusedLabelVisitor(nodeFactory);
			
			program = irConverter.performConvertToIR(program);

			// Add our function before lowering
			program.getIrCompUnit().appendFunc(irConverter.allocLayer());

			IRNode irRoot = lowerer.visit(program.getIrCompUnit());

			if(shouldOptimize()) irRoot = constFolder.visit(irRoot);

			irRoot = cfgVisitor.visit(irRoot);
			irRoot = unusedLabelVisitor.visit(irRoot);
			
			if(outputIR) {
				FileUtils.writeToFile(programName, irRoot);
//				CodeWriterSExpPrinter codeWriter = new CodeWriterSExpPrinter(new PrintWriter(System.out));
//				irRoot.printSExp(codeWriter);
//				codeWriter.flush();
			}
			
			if(interpretIR) {
				System.out.println("========= IR Interpreter Output =========\n");
				IRSimulator simulator = new IRSimulator((IRCompUnit) irRoot);
				simulator.call("_Imain_paai", 0);
				System.out.println("\n=========================================");
			}
		}
	}
	
	private boolean shouldOptimize() {
		return !doNotOptimize;
	}
	
	public Map<String, Program> getValidPrograms(SymbolTableManager symTableManager, 
			Map<String, List<FunctionDecl>> progFuncDecls) throws IOException {
		Map<String, Program> validPrograms = new HashMap<>();

		for (String filename : sourceFiles) {
			//Check valid file and file exists
			if(!filename.endsWith(".xi") && !filename.endsWith(".ixi")) {
				System.out.println("Skipping file: \'" + filename + "\' as it is not a .xi or .ixi file.");
				continue;
			}
			if(!Files.exists(sourcePath.resolve(filename))) {
				System.out.println("Skipping " + filename + " as it cannot be found.");
				continue;				
			}
			//Lex and parse
			TokenFactory tokenFactory = new TokenFactory();
			Lexer lexx = new FileTypeLexer(filename, sourcePath, FileType.parseFileType(filename), tokenFactory);
			Parser parser = new Parser(lexx, tokenFactory);
			
			ParseResult parseResult = new ParseResult(parser);
			ErrorUtils.printErrors(parseResult, filename);
			
			if(outputLex){
				SourceFileLexer lexer = new SourceFileLexer(filename, sourcePath);
				List<Token> tokens = lexer.getTokens();	
				FileUtils.writeToFile(filename, tokens);
			}
			
			if(outputParse) {
				FileUtils.writeToFile(filename, parseResult);
			}
			
			if(!parseResult.isValidAST()) {
				writeErrorToFile(filename, parseResult.getFirstError());
				continue;
			} 
			
			Node root = parseResult.getNode().get();
			
			//Typecheck
			if(root instanceof Program) {
				try {
					Map<String, FunctionDecl> libFuncTable = symTableManager.mergeSymbolTables((Program) root);
					
					FunctionCollector funcCollector = new FunctionCollector(libFuncTable);
					Map<String, FunctionDecl> funcTable = funcCollector.visit(root);
					if(funcCollector.hasError()) {
						ErrorUtils.printErrors(funcCollector.getErrors(), filename);
						writeErrorToFile(filename, funcCollector.getFirstError());
						continue;
					}
					
					progFuncDecls.put(filename, new ArrayList<>(funcTable.values()));
					
					TypeChecker typeChecker = new TypeChecker(funcTable);	
					root = typeChecker.performTypeCheck(root);
					ErrorUtils.printErrors(typeChecker.getTypeErrors(), filename);

					if(!typeChecker.hasError()) {
						FileUtils.writeTypeCheckToFile(filename);
						validPrograms.put(filename, (Program)root);
					} else {
						writeErrorToFile(filename, typeChecker.getFirstError());
					}
				}
				catch(SemanticException e) {
					SemanticError error = new SemanticError(e.getErrorNode(), e.getMessage());
					System.out.println(error.getPrintErrorMessage(filename));
					writeErrorToFile(filename, error);
					continue;
				}
			}
			if(root instanceof Interface) {
				symTableManager.generateSymbolTableFromAST(filename.substring(0, filename.length()-4), (Interface) root); 
			}	
			
		}
		
		return validPrograms;
	}

	public void writeErrorToFile(String filename, BaseError error) {
		List<String> outfiles = new ArrayList<>();
		if(outputTypeCheck) outfiles.add(".typed");
		if(outputIR) outfiles.add(".ir");
		
		outfiles = outfiles.stream()
			.map(ext -> filename.replaceFirst("\\.(xi|ixi)", ext))
			.collect(Collectors.toList());

		for(String outfile : outfiles) {
			Path outpath = dPath.resolve(outfile);
			String msg = error.getFileErrorMessage();
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(outpath.toString()));
				writer.write(msg);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing error to " + dPath.resolve(outfile) + " for " + filename);
			} 
		}
	}

	/**
	 * Writes xic help screen to command line
	 * 
	 * @param parser   the command line parser
	 */
	public void printHelpScreen(CmdLineParser parser) {
		System.out.println("xic [options...] arguments...");
		parser.printUsage(System.out);
		System.out.println();
	}
}
