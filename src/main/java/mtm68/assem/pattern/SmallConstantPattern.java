package mtm68.assem.pattern;

/**
 * Pattern for matching any 32-bit constant
 * 
 * @author Scott
 */
public class SmallConstantPattern extends ConstantPattern {

	public SmallConstantPattern(String name) {
		super(name);
	}
	
	@Override
	protected boolean valueMatches(long value) {
		return value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE;
	}
}