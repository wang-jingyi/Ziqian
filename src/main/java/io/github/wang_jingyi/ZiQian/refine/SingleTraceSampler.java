package io.github.wang_jingyi.ZiQian.refine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;


/*
 * extract multiple paths from training data
 * the extracted path will be stored as an object
 * 
 * */
public class SingleTraceSampler implements Sampler{

	int trace_count = 0; // number of extracted paths
	String decomposed_trace_path; // the path to store the extracted paths
	String latest_sample_path; // the path of latest extracted path
	List<String> testing_data; // abstract testing data
	PrismModel learned_model; // learned model
	Counterexample ce; // counterexample
	List<String> previous_observation; // abstract previous observation
	int start_index = 0; // start index of the new path
	
	public SingleTraceSampler(String decomposed_data_path, List<String> testing_data, 
			PrismModel learned_model, Counterexample ce, List<String> previous_observation) {
		this.decomposed_trace_path = decomposed_data_path;
		FileUtil.cleanDirectory(decomposed_trace_path);
		this.testing_data = testing_data;
		this.learned_model = learned_model;
		this.ce = ce;
		this.previous_observation = previous_observation;
	}

	@Override
	public boolean isObtainingNewSample() {
		return true;
	}

	@Override
	public String getOutputFilePath() {
		return decomposed_trace_path;
	}

	@Override
	public String getLatestSample() {
		return latest_sample_path;
	}

	@Override
	public boolean isDecomposed() {
		return true;
	}

	@Override
	public void sample() {

		List<String> start_state_label = null;
		int max_path_length = ce.getLongestCounterPath();

		// find the label of the start state
		for(PrismState state : learned_model.getPrismStates()){
			if(StringUtil.isSuffix(state.getLabel(), previous_observation)){
				start_state_label = state.getLabel();
				break;
			}
		}

		// decomposition of a single trace into multiple traces for hypothesis testing
		for(int i=start_index+1; i<=testing_data.size(); i++){
			List<String> path = testing_data.subList(start_index, i);
			if(StringUtil.isSuffix(start_state_label, path)){ // start of a path
				List<String> actual_path = new ArrayList<>();
				for(int j=0; j<max_path_length; j++){
					String next_symbol = testing_data.get(i+j);
					actual_path.add(next_symbol);
					try {
						if(ce.checkMembership(actual_path)){
							start_index = i+j;
							break;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(j==max_path_length-1){
						start_index = i+j;
					}
				}
				
				try {
					FileUtil.writeObject(decomposed_trace_path+"/trace_"+trace_count, actual_path);
					latest_sample_path = decomposed_trace_path + "/trace_"+trace_count;
					trace_count ++;
					break;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
