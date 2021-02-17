package mtm68;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class Main {

	@Option(name="--help", help=true, usage="print help screen")
	private boolean help = false;
	
	@Option(name="--lex", usage="outputs lexed version of source file")
	private boolean lex;
	
	@Option(name="-D", usage="specify location for generated diagnostic files")
	private Path dPath = Paths.get(System.getProperty("user.dir"));
	
	@Argument
	private List<File> sourceFiles = new ArrayList<File>();
	
	public static void main(String[] args) {
		new Main().parseCmdLine(args);
	}
	
	public void parseCmdLine(String[] args) {
		CmdLineParser cmdParser = new CmdLineParser(this, ParserProperties.defaults().withShowDefaults(false));
		
		try {
			cmdParser.parseArgument(args);
		}
		catch(CmdLineException e) {
			 System.out.println(e.getMessage());
	         printHelpScreen(cmdParser);
		}
		
		//Act on command arguments
		if(sourceFiles.isEmpty() && !help) printHelpScreen(cmdParser);
		
		if(help) printHelpScreen(cmdParser);
		
		if(lex) {
			for(File file : sourceFiles) {
				System.out.println("Lexing " + file.getName() + " into " + dPath.getFileName());
			}
		}
	}
	
	public void printHelpScreen(CmdLineParser parser) {
		System.out.println("xic [options...] arguments...");
        parser.printUsage(System.out);
        System.out.println();
	}
}
