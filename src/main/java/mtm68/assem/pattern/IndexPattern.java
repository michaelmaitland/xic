package mtm68.assem.pattern;

public class IndexPattern extends ConstantPattern {
	
	public IndexPattern(String name) {
		super(name);
	}

	@Override
	protected boolean valueMatches(long value) {
		return value == 1L || value == 2L || value == 4L || value == 8L;
	}

}
