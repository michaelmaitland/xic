package mtm68.assem.tile;

import mtm68.assem.Assem;
import mtm68.assem.operand.Reg;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.PatternResults;

public abstract class Tile {
	
	private Pattern pattern;
	private float cost;

	public Tile(Pattern pattern, float cost) {
		super();
		this.pattern = pattern;
		this.cost = cost;
	}
	
	public abstract Assem getTiledAssem(Reg resultReg, PatternResults results);

	public Pattern getPattern() {
		return pattern;
	}
	
	public float getCost() {
		return cost;
	}
}
