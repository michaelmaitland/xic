package mtm68.ast.symbol;

import java.util.HashMap;
import java.util.Map;

public class DispatchVector {

	private Map<String, Integer> idToIndex;

	public DispatchVector() {
	}
	
	public boolean contains(String methodName) {
		return idToIndex.containsKey(methodName);
	}
	
	public void add(String methodName) {
		int index = idToIndex.size();
		idToIndex.put(methodName, index);
	}

	public DispatchVector copy() {
		DispatchVector copy = new DispatchVector();
		copy.idToIndex = new HashMap<String, Integer>(idToIndex);
		return copy;
	}
}
