package mtm68.ast.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.visit.FunctionCollector;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public abstract class Node implements HasLocation, Cloneable {
	
	private Location startLoc;
	
	public abstract void prettyPrint(SExpPrinter p);
	
	/**
	 * Accepts a visitor
	 * @param v the visitor to use
	 * @return the visited node
	 */
	@SuppressWarnings("unchecked")
	public <N extends Node> N accept(Visitor v) {
        Visitor v2 = v.enter(this);
        Node n = visitChildren(v2);
        return (N) v2.leave(n, this);
    }
	
	/**
	 * Visit each element of a list.
	 * 
	 * @param l The list to visit.
	 * @param v The visitor to visit with
	 * @return A new list with each element from the old list replaced by the result
	 *         of visiting that element. If {@code l} is {@code null}, {@code null}
	 *         is returned.
	 *         
	 * This function is adopted from polyglot.ast.Node_c
	 */
	public <N extends Node> List<N> acceptList(List<N> l, Visitor v){

		if(l == null) {
			return null;
		}
			
		List<N> result = l;
		List<N> vl = new ArrayList<>(l.size());
		
		for(N n : l) {
			N n2 = n.accept(v); 
			if(n != n2) {
				result = vl;
			}
			// Add everything to vl in case any n != n2
			vl.add(n2);
		}

		return result;
	}
	
	public <N extends Node> Optional<N> acceptOptional(Optional<N> opt, Visitor v) {
		if(!opt.isPresent()) return opt; 
		
		N node = opt.get();
		N newNode = node.accept(v);
		
		if(node == newNode) return opt;
		
		return Optional.of(newNode);
	}

	/**
	 * Visit all children that belong to {@code this}.
	 * 
	 * @param v The visitor to visit with
	 * @return The node returned from visiting all of its children.
	 */
	public abstract Node visitChildren(Visitor v);

	public abstract Node typeCheck(TypeChecker tc);
	
	public Node extractFunctionDecl(FunctionCollector fc){
		return this;
	}
	
	public Location getStartLoc() {
		return startLoc;
	}

	public void setStartLoc(Location startLoc) {
		this.startLoc = startLoc;
	}
	
	@Override
	public int getLine() {
		return startLoc.getLine();
	}
	
	@Override
	public int getColumn() {
		return startLoc.getColumn();
	}
	
	@SuppressWarnings("unchecked")
	public <N extends Node> N copy() {
		try {
			return (N) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
