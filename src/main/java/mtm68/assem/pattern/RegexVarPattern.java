package mtm68.assem.pattern;

import java.util.regex.Pattern;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRTemp;

/**
 * Pattern for IRTemp's with names that match the given regular expression.
 * 
 * @author Scott
 */
public class RegexVarPattern extends MatchablePattern {
	
	private String regex;

	public RegexVarPattern(String name, String regex) {
		super(name);
		
		this.regex = regex;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRTemp)) return false;
		
		IRTemp temp = (IRTemp) node;
		matched = temp;

		return Pattern.matches(regex, temp.name());
	}

}
