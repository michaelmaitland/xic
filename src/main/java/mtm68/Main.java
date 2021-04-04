package mtm68;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Program;
import mtm68.ast.types.ContextType;
import mtm68.ast.types.TypingContext;
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
import mtm68.visit.FunctionCollector;
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
	
	@Option(name = "--irgen", usage = "generates and interprets IR code")
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
			printHelpScreen(cmdParser);
		}
		
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
				writeToFile(filename, tokens);
			}
			
			if(outputParse) writeToFile(filename, parseResult);
			
			if(!parseResult.isValidAST()) {
				if(outputTypeCheck) writeToFile(filename, Optional.of(parseResult.getFirstError()));
				continue;
			}
			
			Node root = parseResult.getNode().get();
			
			//Typecheck
			if(root instanceof Program) {
				try {
					Map<String, ContextType> mergedSymbolTable = symTableManager.mergeSymbolTables((Program) root);
					
					FunctionCollector funcCollector = new FunctionCollector(mergedSymbolTable);
					root.accept(funcCollector);
					Map<String, ContextType> startingContext = funcCollector.getContext();
					if(funcCollector.hasError()) {
						ErrorUtils.printErrors(funcCollector.getErrors(), filename);
						if(outputTypeCheck) writeToFile(filename, Optional.of(funcCollector.getFirstError()));
						continue;
					}
					
					TypeChecker typeChecker = new TypeChecker(startingContext);	
					root = typeChecker.performTypeCheck(root);
					ErrorUtils.printErrors(typeChecker.getTypeErrors(), filename);
					if(outputTypeCheck) {
						writeToFile(filename, 
							typeChecker.hasError() ? Optional.of(typeChecker.getFirstError()) : Optional.empty());
					}
				}
				catch(SemanticException e) {
					SemanticError error = new SemanticError(e.getErrorNode(), e.getMessage());
					System.out.println(error.getPrintErrorMessage(filename));
					if(outputTypeCheck) writeToFile(filename, Optional.of(error));
					continue;
				}
			}
			if(root instanceof Interface) {
				symTableManager.generateSymbolTableFromAST(filename.substring(0, filename.length()-4), (Interface) root); 
			}	
			
		}
	}
	

	/**
	 * Writes lexed results to [filename.lexed] 
	 * Requires: filename is of the form filename.xi or filename.ixi
	 * 
	 * @param filename the name of the file lexed
	 * @param tokens   the list of lexed tokens
	 */
	public void writeToFile(String filename, List<Token> tokens) {
		String outfile = filename.replaceFirst("\\.(xi|ixi)", ".lexed");
		Path outpath = dPath.resolve(outfile);
		try {
			Files.createDirectories(outpath.getParent());
			Files.write(outpath, tokens.stream()
					.map(Object::toString)
					.collect(Collectors.toList()), Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("Failed writing lexer results to " + dPath.resolve(outfile) + " for " + filename);
		}
	}
	
	/**
	 * Writes parsed AST to [filename.parsed] 
	 * Requires: filename is of the form filename.xi or filename.ixi
	 * 
	 * @param filename the name of the file parsed
	 * @param ast      the root node of the ast
	 */
	public void writeToFile(String filename, ParseResult result) {
		String outfile = filename.replaceFirst("\\.(xi|ixi)", ".parsed");
		Path outpath = dPath.resolve(outfile);
		try {
			Files.createDirectories(outpath.getParent());
			if(result.isValidAST()) {
				Node ast = result.getNode().get();
				SExpPrinter printer = new CodeWriterSExpPrinter(new PrintWriter(outpath.toFile()));
				ast.prettyPrint(printer);
				printer.close();
			}
			else {
				String error = result.getFirstError().getFileErrorMessage();
				BufferedWriter writer = new BufferedWriter(new FileWriter(outpath.toString()));
			    writer.write(error);
			    writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed writing parser results to " + dPath.resolve(outfile) + " for " + filename);
		}
	}
	
	/**
	 * Writes typecheck results to [filename.typed] 
	 * Requires: filename is of the form filename.xi or filename.ixi
	 * 
	 * @param filename the name of the file parsed
	 * @param error    the semantic error to be written
	 */
	public void writeToFile(String filename, Optional<BaseError> error) {
		String outfile = filename.replaceFirst("\\.(xi|ixi)", ".typed");
		Path outpath = dPath.resolve(outfile);
		String msg = error.isPresent() ? error.get().getFileErrorMessage() : "Valid Xi Program";
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(outpath.toString()));
		    writer.write(msg);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed writing type check results to " + dPath.resolve(outfile) + " for " + filename);
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
