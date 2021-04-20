package mtm68.assem.visit;

import java.util.List;

import mtm68.assem.Assem;
import mtm68.ast.nodes.Node;
import mtm68.visit.Visitor;

public class TrivialRegisterAllocator extends Visitor {
	
	
	public TrivialRegisterAllocator enter(Node parent, Node n) {
		return this;
	}
	@Override
	public Node leave(Node n, Node old) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Assem> assignAbstractRegsStackLocations(List<Assem> insts) {
		//List<Assem> 
		return null;
	}
}
