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

import mtm68.lexer.Lexer.Token;
import mtm68.lexer.SourceFileLexer;

public class Main {

	@Option(name = "--help", help = true, usage = "print help screen")
	private boolean help = false;

	@Option(name = "--lex", usage = "outputs lexed version of source file")
	private boolean lex;

	@Option(name = "-D", usage = "specify location for generated diagnostic files")
	private Path dPath = Paths.get(System.getProperty("user.dir"));

	@Argument
	private List<String> sourceFiles = new ArrayList<>();

	public static void main(String[] args) {
		new Main().parseCmdLine(args);
	}

	public void parseCmdLine(String[] args) {
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

		// TODO: Ignore non *.xi files
		// TODO: Figure out if it should throw an error or not
		// TODO: Output tokens into lexed file
		//System.out.println("dpath: " + dPath);
		for (String filename : sourceFiles) {
			//System.out.println("Lexing " + filename + " into " + dPath.getFileName());
			try {
				SourceFileLexer lexer = new SourceFileLexer(filename);
				List<Token> tokens = lexer.getTokens();
				if (lex) {
					writeToFile(filename, tokens);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes lexed results to [filename.lexed] 
	 * Requires: filename is of the form filename.xi or filename.ixi
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

	public void printHelpScreen(CmdLineParser parser) {
		System.out.println("xic [options...] arguments...");
		parser.printUsage(System.out);
		System.out.println();
	}
}
