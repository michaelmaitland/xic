package mtm68.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Node;
import mtm68.exception.BaseError;
import mtm68.lexer.Token;
import mtm68.parser.ParseResult;

public class FileUtils {
		public static Path dPath;
	
		/**
		 * Writes lexed results to [filename.lexed] 
		 * Requires: filename is of the form filename.xi or filename.ixi
		 * 
		 * @param filename the name of the file lexed
		 * @param tokens   the list of lexed tokens
		 */
		public static void writeToFile(String filename, List<Token> tokens) {
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
		public static void writeToFile(String filename, ParseResult result) {
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
		public static void writeTypeCheckToFile(String filename) {
			String outfile = filename.replaceFirst("\\.(xi|ixi)", ".typed");
			Path outpath = dPath.resolve(outfile);
			String msg = "Valid Xi Program";
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
		 * Writes IR to [filename.ir] 
		 * Requires: filename is of the form filename.xi or filename.ixi
		 * 
		 * @param filename the name of the file parsed
		 * @param ast      the root node of the ast
		 */
		public static void writeToFile(String filename, IRNode irRoot) {
			String outfile = filename.replaceFirst("\\.(xi|ixi)", ".ir");
			Path outpath = dPath.resolve(outfile);
			try {
				Files.createDirectories(outpath.getParent());			
				SExpPrinter printer = new CodeWriterSExpPrinter(new PrintWriter(outpath.toFile()));
				irRoot.printSExp(printer);
				printer.close();

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing ir to " + dPath.resolve(outfile) + " for " + filename);
			}
		}
}
