package mtm68.ast.nodes;

import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public interface INode {

	public void prettyPrint(SExpPrinter p);

	public <N extends Node> N accept(Node parent, Visitor v);
	
	public <N extends Node> N accept(Visitor v);
	
	public <N extends Node> List<N> acceptList(List<N> l, Visitor v);
	
	public <N extends Node> Optional<N> acceptOptional(Node parent, Optional<N> opt, Visitor v);
	
	public <N extends Node> Optional<N> acceptOptional(Optional<N> opt, Visitor v);
	
	public abstract Node visitChildren(Visitor v);

	public abstract Node typeCheck(TypeChecker tc);

	public abstract Node convertToIR(NodeToIRNodeConverter cv);


}
