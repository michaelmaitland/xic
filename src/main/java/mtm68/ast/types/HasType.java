package mtm68.ast.types;

import mtm68.ast.nodes.HasLocation;

public interface HasType extends HasLocation{
	Type getType();
}
