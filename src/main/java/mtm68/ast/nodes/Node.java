package mtm68.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public abstract class Node {
	
	public abstract void prettyPrint(SExpPrinter p);
	
	/**
	 * Visit a child
	 * 
	 * @param n The child to visit
	 * @param v The visitor to visit  with
	 * @return The node returned from visiting.
	 */
	public <N extends Node> N visitChild(N n, Visitor v) {
		if(n == null) return null;
		else return v.visit(n);
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
	public <N extends Node> List<N> visitList(List<N> l, Visitor v){

		if(l == null) {
			return null;
		}
			
		List<N> result = l;
		List<N> vl = new ArrayList<>(l.size());
		
		for(N n : l) {
			N n2 = visitChild(n, v);
			if(n != n2) {
				result = vl;
			}
			// Add everything to vl in case any n != n2
			vl.add(n2);
		}

		return result;
	}

	/**
	 * Visit all children that belong to {@code this}.
	 * 
	 * @param v The visitor to visit with
	 * @return The node returned from visiting all of its children.
	 */
	public abstract Node visitChildren(Visitor v);

	public abstract Node typeCheck(TypeChecker tc);
}
