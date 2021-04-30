package mtm68.assem.pattern;

/**
 * Pattern for matching against any constant
 * 
 * @author Scott
 */
public class AnyConstantPattern extends ConstantPattern {

	public AnyConstantPattern(String name) {
		super(name);
	}

	@Override
	protected boolean valueMatches(long value) {
		return true;
	}

}
