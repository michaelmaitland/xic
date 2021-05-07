package mtm68.assem.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mtm68.assem.Assem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.cfg.Graph.Node;
import mtm68.assem.operand.Reg;
import mtm68.util.SetUtils;

public class Liveness {
	
	private Graph<AssemData<LiveData>> graph;
	
	public void performLiveVariableAnalysis(List<Assem> assems) {
		AssemCFGBuilder<LiveData> builder = new AssemCFGBuilder<>();

		graph = builder.buildAssemCFG(assems, LiveData::new);
		
		List<Node> nodes = graph.getNodes();
		
		boolean changes = true;
		while(changes) {
			changes = false;

			for(Node node : nodes) {
				AssemData<LiveData> data = graph.getDataForNode(node);
				Assem assem = data.getAssem();
				LiveData flowData = data.getFlowData();

				Set<String> inOld = flowData.getLiveIn();
				Set<String> outOld = flowData.getLiveOut();
				
				Set<String> use = regToStr(assem.use()); 
				Set<String> def = regToStr(assem.def()); 
				
				// in = use U (out - def)
				Set<String> in = Stream.concat(
						outOld.stream().filter(v -> !def.contains(v)),
						use.stream()).collect(Collectors.toSet());

				// out = U in 
				Set<String> out = SetUtils.empty();
				for(Node succ : node.succ()) {
					out.addAll(graph.getDataForNode(succ).getFlowData().getLiveIn());
				}
				
				flowData.setLiveIn(in);
				flowData.setLiveOut(out);
				
				changes = changes || (!inOld.equals(in) || !outOld.equals(out));
			}
		}
	}
	
	private Set<String> regToStr(Set<Reg> regs){
		return regs.stream()
				.map(Reg::getId)
				.collect(Collectors.toSet());
	}
	
	public void show(Writer writer) throws IOException {
		graph.show(writer, "Liveness", this::showLiveness);
	}
	
	private String showLiveness(AssemData<LiveData> data) {
		Set<String> liveIn = data.getFlowData().getLiveIn();
		Set<String> liveOut = data.getFlowData().getLiveOut();
		Assem assem = data.getAssem();
		
		StringBuilder sb = new StringBuilder();

		sb.append("In: ");
		sb.append(setToString(liveIn));
		sb.append("\\n");
		
		sb.append(assem.toString());
		sb.append("\\n");

		sb.append("Out: ");
		sb.append(setToString(liveOut));

		return sb.toString();
	}
	
	private <T> String setToString(Set<T> set) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		String elems = set.stream()
			.map(Object::toString)
			.collect(Collectors.joining(","));

		sb.append(elems);

		sb.append('}');
		
		return sb.toString();
	}
	
	public static class LiveData {
		private Set<String> liveIn; 
		private Set<String> liveOut; 
		
		public LiveData() {
			liveIn = new HashSet<>();
			liveOut = new HashSet<>();
		}
		
		public Set<String> getLiveIn() {
			return liveIn;
		}
		
		public void setLiveIn(Set<String> liveIn) {
			this.liveIn = liveIn;
		}
		
		public Set<String> getLiveOut() {
			return liveOut;
		}
		
		public void setLiveOut(Set<String> liveOut) {
			this.liveOut = liveOut;
		}
		
	}

}
