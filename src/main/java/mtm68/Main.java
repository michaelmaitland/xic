package mtm68;

import java.io.IOException;
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

import java_cup.runtime.Symbol;
import mtm68.lexer.SourceFileLexer;

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
			System.out.println("xic died: " + e.getMessage());
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
			SourceFileLexer lexer = new SourceFileLexer(filename, sourcePath);
			List<Symbol> tokens = lexer.getTokens();
			if (lex) {
				writeToFile(filename, tokens);
			}
			// Node ast = parse(tokens);
			// if (parse) writeToFile(filename, ast);
		}
	}

	/**
	 * Writes lexed results to [filename.lexed] 
	 * Requires: filename is of the form filename.xi or filename.ix
	 * 
	 * @param filename the name of the file lexed
	 * @param tokens   the list of lexed tokens
	 */
	public void writeToFile(String filename, List<Symbol> tokens) {
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
