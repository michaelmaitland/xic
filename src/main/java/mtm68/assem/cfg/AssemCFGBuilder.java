package mtm68.assem.cfg;

import java.util.List;
import java.util.function.Supplier;

import mtm68.assem.Assem;
import mtm68.assem.cfg.Graph.Node;

public class AssemCFGBuilder<T> {
	
	public AssemCFGBuilder() {
	}
	
	public Graph<AssemData<T>> buildAssemCFG(List<Assem> assems, Supplier<T> flowDataConstructor) {
		Graph<AssemData<T>> graph = new Graph<>();

		Node prev = null;

		for(Assem assem : assems) {
			AssemData<T> data = new AssemData<>(assem, flowDataConstructor.get());
			Node curr = graph.createNode(data, assem.toString());
			
			if(prev != null) {
				graph.addEdge(prev, curr);
			}
			
			prev = curr;
		}
		
		return graph;
	}

	public static class AssemData<T> {
		
		private Assem assem;
		private T flowData;
		
		public AssemData(Assem assem, T flowData) {
			this.assem = assem;
			this.flowData = flowData;
		}
		
		public Assem getAssem() {
			return assem;
		}
		
		public T getFlowData() {
			return flowData;
		}
		
		public void setFlowData(T flowData) {
			this.flowData = flowData;
		}
	}
}
