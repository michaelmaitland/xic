package mtm68.assem.pattern;

/**
 * Pattern for matching against valid index values. In x86 these include 1, 2, 4, and 8.
 * 
 * @author Scott
 */
public class IndexPattern extends ConstantPattern {
	
	public IndexPattern(String name) {
		super(name);
	}

	@Override
	protected boolean valueMatches(long value) {
		return value == 1L || value == 2L || value == 4L || value == 8L;
	}

}
