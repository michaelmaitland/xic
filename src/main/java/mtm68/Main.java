package mtm68;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.Node;
import mtm68.lexer.Lexer;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.SourceFileLexer;
import mtm68.lexer.Token;
import mtm68.parser.Parser;

public class Main {

	@Option(name = "--help", help = true, usage = "print help screen")
	private boolean help = false;

	@Option(name = "--lex", usage = "saves lexed tokens from source file to <filename>.lexed")
	private boolean lex;
	
	@Option(name = "--parse", usage = "saves AST generated from source file to <filename>.parsed")
	private boolean parse;
	
	@Option(name = "-sourcepath", usage = "specify path to source files")
	private Path sourcePath = Paths.get(System.getProperty("user.dir"));

	@Option(name = "-D", usage = "specify location for generated diagnostic files")
	private Path dPath = Paths.get(System.getProperty("user.dir"));

	@Argument
	private List<String> sourceFiles = new ArrayList<>();

	public static void main(String[] args) {
		try {
			new Main().parseCmdLine(args);
		} catch (Exception e) {
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
		

		// Act on command arguments
		if (help || sourceFiles.isEmpty())
			printHelpScreen(cmdParser);

		for (String filename : sourceFiles) {
			if(!filename.endsWith(".xi") && !filename.endsWith(".ixi")) {
				System.out.println("Skipping file: \'" + filename + "\' as it is not a .xi or .ixi file.");
				continue;
			}
			Lexer lexx = new FileTypeLexer(new FileReader(filename), FileType.parseFileType(filename));
			Parser parser = new Parser(lexx, new ComplexSymbolFactory());
			Node ast = (Node)(parser.parse().value);
			
			//Persist
			if (lex) {
				SourceFileLexer lexer = new SourceFileLexer(filename, sourcePath);
				List<Token> tokens = lexer.getTokens();	
				writeToFile(filename, tokens);
			}
			if(parse) writeToFile(filename, ast);
			
			SExpPrinter printer = new CodeWriterSExpPrinter(new PrintWriter(System.out));
			ast.prettyPrint(printer);
			printer.flush();
			System.out.println("Result: " + ast);


			
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
	public void writeToFile(String filename, Node ast) {
		String outfile = filename.replaceFirst("\\.(xi|ixi)", ".parsed");
		Path outpath = dPath.resolve(outfile);
		try {
			Files.createDirectories(outpath.getParent());
			SExpPrinter printer = new CodeWriterSExpPrinter(new PrintWriter(outpath.toFile()));
			ast.prettyPrint(printer);
			printer.close();
		} catch (IOException e) {
			System.out.println("Failed writing parser results to " + dPath.resolve(outfile) + " for " + filename);
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
