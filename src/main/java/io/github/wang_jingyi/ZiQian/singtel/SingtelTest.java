package io.github.wang_jingyi.ZiQian.singtel;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.learn.LearnPST;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingtelTest {
	
	static String data_path = "/Users/jingyi/Ziqian/resource/singtel";
	static String output_path = "/Users/jingyi/Ziqian/resource/singtel/output";
	static double epsilon = 0.0001;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		
		String model_name = "singtel";
		int data_size = 100000;
		int step_size = 1;
		String delimiter = ",";
		
		List<String> varsSet 
		= PrismPathData.extractPathVars(data_path, delimiter);

		ExtractPrismData epd = new ExtractPrismData(data_path, data_size, step_size, delimiter, true);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		
		List<String> init_labels = getDataLabels(data_path+"/label.csv", data_size, step_size, delimiter);
		
		// get the abstract system trace
		List<List<String>> traces = new ArrayList<>();
		List<String> one_trace = new ArrayList<>();
		for(int i=0; i<init_labels.size(); i++){
			one_trace.add(1+init_labels.get(i));
		}
		traces.add(one_trace);
		
		List<String> alpha = new ArrayList<>();
		alpha.add("10");
		alpha.add("11");
		
		Input training_input = new Input(alpha, traces);
		LearningDTMC learner = new LearnPST(epsilon);
		learner.learn(training_input);
		learner.PrismModelTranslation(training_input, pres, model_name);
		FormatPrismModel fpm = new FormatPrismModel("dtmc", output_path, model_name, true);
		fpm.translateToFormat(learner.getPrismModel(),training_input);
		
	}
	
	/**
	 * @param data_path the data path
	 * @return the list of label where the last column is the label of the data
	 * @throws IOException 
	 */
	private static List<String> getDataLabels(String label_path, int data_size, int step_size, String delimiter) throws IOException{
		
		List<String> labels = new ArrayList<>();
		
		FileReader reader = new FileReader(label_path);
		BufferedReader br = new BufferedReader(reader);
		String str = br.readLine(); // first line of file

		int lineCount = 0;
		int neg_count = 0;
		while((str = br.readLine()) != null){ // read each line of data from file
			lineCount++;
			if(lineCount%step_size==0){
				if(str.equals("b'normal.'")){
					labels.add("0");
				}
				else{
					labels.add("1");
					neg_count++;
				}
			}
			if(lineCount>=data_size){
				break;
			}
		}
		br.close();
		System.out.println("--- prob of entering abnormal state: " + (double)neg_count/data_size);
		return labels;
		
	}

}
