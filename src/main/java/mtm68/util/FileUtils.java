package mtm68.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.Assem;
import mtm68.assem.cfg.Graph;
import mtm68.assem.visit.AssemToFileBuilder;
import mtm68.ast.nodes.Node;
import mtm68.ir.cfg.IRCFGBuilder;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.lexer.Token;
import mtm68.parser.ParseResult;

public class FileUtils {
		public static Path diagPath;
		public static Path assemPath;
	
		/**
		 * Writes lexed results to [filename.lexed] 
		 * Requires: filename is of the form filename.xi or filename.ixi
		 * 
		 * @param filename the name of the file lexed
		 * @param tokens   the list of lexed tokens
		 */
		public static void writeToFile(String filename, List<Token> tokens) {
			String outfile = filename.replaceFirst("\\.(xi|ixi)", ".lexed");
			Path outpath = diagPath.resolve(outfile);
			try {
				Files.createDirectories(outpath.getParent());
				Files.write(outpath, tokens.stream()
						.map(Object::toString)
						.collect(Collectors.toList()), Charset.defaultCharset());
			} catch (IOException e) {
				System.out.println("Failed writing lexer results to " + diagPath.resolve(outfile) + " for " + filename);
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
			Path outpath = diagPath.resolve(outfile);
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
				System.out.println("Failed writing parser results to " + diagPath.resolve(outfile) + " for " + filename);
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
			Path outpath = diagPath.resolve(outfile);
			String msg = "Valid Xi Program";
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(outpath.toString()));
			    writer.write(msg);
			    writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing type check results to " + diagPath.resolve(outfile) + " for " + filename);
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
			Path outpath = diagPath.resolve(outfile);
			try {
				Files.createDirectories(outpath.getParent());			
				SExpPrinter printer = new CodeWriterSExpPrinter(new PrintWriter(outpath.toFile()));
				irRoot.printSExp(printer);
				printer.close();

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing ir to " + diagPath.resolve(outfile) + " for " + filename);
			}
		}
		
		/**
		 * Writes Assem to [filename.s] 
		 * Requires: filename is of the form filename.xi or filename.ixi
		 * 
		 * @param filename the name of the file parsed
		 * @param assem    
		 */
		public static void writeAssemToFile(String filename, List<Assem> assem) {
			String outfile = filename.replaceFirst("\\.(xi|ixi)", ".s");
			Path outpath = assemPath.resolve(outfile);
			BufferedWriter writer;
			try {
				Files.createDirectories(outpath.getParent());			
				writer = new BufferedWriter(new FileWriter(outpath.toString()));
			    writer.write(AssemToFileBuilder.assemToFileString(filename, assem));
			    writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing assem results to " + outpath + " for " + filename);
			}
		}
		
		/**
		 * Writes Graph to [filename.dot] 
		 * Requires: filename is of the form filename.xi or filename.ixi
		 * 
		 * @param filename the name of the file parsed
		 * @param assem    
		 */
		public static void writeCFGToFile(String filename, Collection<IRFuncDefn> fDefns) {
			String outfile = filename.replaceFirst("\\.(xi|ixi)", ".dot");
			Path outpath = Paths.get(outfile);
			BufferedWriter writer;
			try {
				Files.createDirectories(outpath.getParent());			
				writer = new BufferedWriter(new FileWriter(outpath.toString()));
				for(IRFuncDefn fDefn : fDefns) {
					IRCFGBuilder<String> builder = new IRCFGBuilder<>();
					Graph<IRData<String>> graph = builder.buildIRCFG(((IRSeq)fDefn.getBody()), () -> "");
					graph.show(writer, fDefn.name(), true, o -> o.getIR().toString());
					writer.append('\n');
				}
			    writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed writing CFG results to " + outpath + " for " + filename);
			}
		}
}
