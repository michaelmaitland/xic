package mtm68.ast.types;

import mtm68.ast.nodes.HasLocation;

public interface HasResult extends HasLocation {
	
	Result getResult();

}