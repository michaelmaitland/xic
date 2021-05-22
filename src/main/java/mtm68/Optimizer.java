package mtm68;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import mtm68.assem.cfg.Graph;
import mtm68.ast.nodes.Program;
import mtm68.ir.cfg.CSETransformer;
import mtm68.ir.cfg.ConstantPropTransformer;
import mtm68.ir.cfg.CopyPropTransformer;
import mtm68.ir.cfg.DeadCodeTransformer;
import mtm68.ir.cfg.IRCFGBuilder;
import mtm68.ir.cfg.IRCFGBuilder.IRData;
import mtm68.util.FileUtils;
import mtm68.util.SetUtils;
import mtm68.visit.FunctionInliner;

public class Optimizer {
	private static final int ITERS = 5;
	
	private static Set<SupportedOpt> optsToPerform = new HashSet<>();
	private static IRNodeFactory nodeFactory;
	private static Set<Phase> irPhases = new HashSet<>();
	private static Set<Phase> cfgPhases = new HashSet<>();
	
	public static Program optimizeAST(Program program){
		Set<SupportedOpt> astOpts = SupportedOpt.getASTOpts();
		Set<SupportedOpt> opts = SetUtils.intersect(optsToPerform, astOpts);
		
		for(SupportedOpt opt : opts) {
			switch(opt) {
			case INL:
				FunctionInliner fl = new FunctionInliner(program.getFunctionDefns());
				program = program.accept(fl);
				break;
			default:
				break;
			}
		}
		
		return program;
	}
	
	public static IRNode optimizeIR(IRNode root) {
		Set<SupportedOpt> irOpts = SupportedOpt.getIROpts();
		Set<SupportedOpt> opts = SetUtils.intersect(optsToPerform, irOpts);
				
		writeInitial(root);
	
		IRConstantFolder constFolder = new IRConstantFolder(nodeFactory);
		if(opts.contains(SupportedOpt.CF)) {
			root = constFolder.visit(root);
		}
		
		for(int i = 0; i < ITERS; i++) {
			if(opts.contains(SupportedOpt.CSE)) {
				CSETransformer cseTransformer = new CSETransformer((IRCompUnit)root, nodeFactory);
				root = cseTransformer.doCSE();
			}
			if(opts.contains(SupportedOpt.COPY)) {
				CopyPropTransformer cpTransformer = new CopyPropTransformer((IRCompUnit)root, nodeFactory);
				root = cpTransformer.doCopyProp();
			}
			if(opts.contains(SupportedOpt.CP)) {
				ConstantPropTransformer constProp = new ConstantPropTransformer((IRCompUnit)root, nodeFactory);
				root = constProp.doConstantProp();
			}
			if(opts.contains(SupportedOpt.DCE)) {
				DeadCodeTransformer dcTransformer = new DeadCodeTransformer((IRCompUnit)root, nodeFactory);
				root = dcTransformer.doDeadCodeRemoval();
			}
		}
		
		if(opts.contains(SupportedOpt.CF)) root = constFolder.visit(root);
		
		writeFinal(root);
		
		return root;
	}
	
	private static void writeInitial(IRNode root) {
		IRCompUnit compUnit = (IRCompUnit) root;
		String rootFilename = compUnit.name().replaceFirst("\\.xi", "");
		
		if(irPhases.contains(Phase.INITIAL)) {
			String filename = rootFilename + "_initial.xi"; 
			FileUtils.writeToFile(filename, root);
		}
		if(cfgPhases.contains(Phase.INITIAL)) {
			String filename = rootFilename + "_f_initial.xi"; 
			IRCFGBuilder<String> builder = new IRCFGBuilder<>();
			Graph<IRData<String>> graph = builder.buildIRCFG(compUnit.flattenCompUnit(), () -> "");
			FileUtils.writeCFGToFile(filename, graph);
		}
	}
	
	private static void writeFinal(IRNode root) {
		IRCompUnit compUnit = (IRCompUnit) root;
		String rootFilename = compUnit.name().replaceFirst("\\.xi", "");
		
		if(irPhases.contains(Phase.FINAL)) {
			String filename = rootFilename + "_final.xi"; 
			FileUtils.writeToFile(filename, root);
		}
		if(cfgPhases.contains(Phase.FINAL)) {
			String filename = rootFilename + "_f_final.xi"; 
			IRCFGBuilder<String> builder = new IRCFGBuilder<>();
			Graph<IRData<String>> graph = builder.buildIRCFG(compUnit.flattenCompUnit(), () -> "");
			FileUtils.writeCFGToFile(filename, graph);
		}
	}
	
	public static void addCF() {
		optsToPerform.add(SupportedOpt.CF);
	}
	
	public static void addCSE() {
		optsToPerform.add(SupportedOpt.CSE);
	}
	
	public static void addINL() {
		optsToPerform.add(SupportedOpt.INL);
	}
	
	public static void addCOPY() {
		optsToPerform.add(SupportedOpt.COPY);
	}
	
	public static void addCP() {
		optsToPerform.add(SupportedOpt.CP);
	}
	
	public static void addDCE() {
		optsToPerform.add(SupportedOpt.DCE);
	}
	
	public static void addAll() {
		optsToPerform.addAll(SupportedOpt.getSupportedOpts());
	}
	
	public static void setNodeFactory(IRNodeFactory nodeFactory) {
		Optimizer.nodeFactory = nodeFactory;
	}
	
	public static void setIRPhases(Set<Phase> irPhases) {
		Optimizer.irPhases = irPhases;
	}
	
	public static void setCFGPhases(Set<Phase> cfgPhases) {
		Optimizer.cfgPhases = cfgPhases;
	}
	
	public static void printSupportedOpts() {
		for(SupportedOpt opt : SupportedOpt.getSupportedOpts()) {
			System.out.println(opt);
		}
	}
	
	public static enum SupportedOpt{
		CF,
		CSE,
		COPY,
		DCE,
		CP,
		INL;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		public static Set<SupportedOpt> getSupportedOpts(){
			return new HashSet<SupportedOpt>(Arrays.asList(SupportedOpt.values()));
		}
		
		public static Set<SupportedOpt> getIROpts(){
			return SetUtils.elems(CF, CSE, COPY);
		}
		
		public static Set<SupportedOpt> getASTOpts(){
			return SetUtils.elems(INL);
		}
		
		public static List<String> getOptsAsStringList(){
			List<String> opts = new ArrayList<>();
			for(SupportedOpt opt : getSupportedOpts())
				opts.add(opt.toString());
			return opts;
		}
	}
	
	public static enum Phase{
		INITIAL,
		FINAL;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
}
