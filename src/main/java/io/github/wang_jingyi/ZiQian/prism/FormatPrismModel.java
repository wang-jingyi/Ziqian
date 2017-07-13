package io.github.wang_jingyi.ZiQian.prism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.run.Config;


/*
 *  translate a PrismModel into PRISM .pm format
 * */

public class FormatPrismModel implements ModelTranslation{

	private String modelType; // dtmc, ctmc, mdp, etc
	private String outputFilePath; // path for output .pm file
	private String fileName; // name for the .pm file
	private boolean abstraction = true;

	public FormatPrismModel(String modelType, String outputFilePath, String fileName, boolean abs) {
		this.modelType = modelType;
		this.outputFilePath = outputFilePath;
		this.fileName = fileName;
		this.abstraction = abs;
	}
	
	public FormatPrismModel(String modelType, String outputFilePath, String fileName) {
		this.modelType = modelType;
		this.outputFilePath = outputFilePath;
		this.fileName = fileName;
	}

	@Override
	public void translateToFormat(PrismModel pm, Input data) {
		File file = new File(outputFilePath+"/"+fileName+".pm");
		File labelFile = new File(outputFilePath+"/"+fileName+"_label.txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
			labelFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileWriter fw = new FileWriter(file, false);
			FileWriter fw1 = new FileWriter(labelFile,false);
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			
			if(abstraction){ // abstract case
				bw.write(ToDotPM(pm,data));
			}
			
			else{ // non-abstract case
				bw.write(ToDotPM(pm,data,AlgoProfile.vars,AlgoProfile.varsLength));
			}
			
			bw1.write(labelToFile(pm));
			bw.flush();
			bw1.flush();
			bw.close();
			bw1.close();
			fw.close();
			fw1.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String labelToFile(PrismModel pm){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<pm.getNumOfPrismStates(); i++){
			sb.append("state id: " + pm.getPrismStates().get(i).getId() + ", label: " + pm.getPrismStates().get(i).getLabel() + "\n");
		}
		return sb.toString();
	}
	
	private String ToDotPM(PrismModel pm, Input data, List<String> vars, List<Integer> varsLength){
		StringBuilder sb = new StringBuilder();
		sb.append(modelType + " \n \n");

		// module
		sb.append("module " + fileName + "\n");
		assert pm.getInitialStates().size()<=1 : "more than one initial state.";
		sb.append("s:[1.." + pm.getNumOfPrismStates() + "] init 1" + "; \n"); //

		// state transitions
		for(int i=0; i<pm.getNumOfPrismStates(); i++){
			PrismState currentPS = pm.getPrismStates().get(i); // get a state 
			sb.append("[]s=" + currentPS.getId() + " -> ");
			if(currentPS.getNextStates().size()!=0){ // next states are nonempty
				for(int j=0; j<currentPS.getNextStates().size(); j++){
					if(j==0){
						sb.append(currentPS.getTransitionProb().get(j) + " :(s'=" + currentPS.getNextStates().get(j).getId() + ")");
						continue;
					}
					sb.append(" + " + currentPS.getTransitionProb().get(j) + " :(s'=" + currentPS.getNextStates().get(j).getId() + ")");
				}
				sb.append(";\n");
			}
			else{
				sb.append("1:(s'=" + currentPS.getId() + ")");
				sb.append(";\n");
			}
		}
		sb.append("endmodule \n\n");

		// non-abstract predicates labels
		for(int i=0; i<pm.getPredicates().size(); i++){
			Predicate pre = pm.getPredicates().get(i);
			List<Integer> validStateID = new ArrayList<Integer>();
			for(PrismState ps : pm.getPrismStates()){ // find all valid states for current atomic proposition
				String curState = ps.getCurrentState();
				if(curState.equals("empty") || curState.equals("")){ // take care of initial state: empty
					if(pre.getPredicateName().equals("hold")){
						validStateID.add(ps.getId());
					}
					continue;
				}
				if(pre.check(VariablesValue.stateToVariableValue(curState, vars, varsLength))){
					validStateID.add(ps.getId());
				}
			}
			if(validStateID.size()!=0){
				sb.append("label \"" + pre.getPredicateName()+"\" = s=" + validStateID.get(0) );
				if(validStateID.size()>1){
					for(int k=1; k<validStateID.size(); k++){
						sb.append("|s=" + validStateID.get(k));
					}
				}
				sb.append(";\n");
			}
			else{
				sb.append("label \"" + pre.getPredicateName()+"\" = s=0;\n" ); // non-reachable
			}
		}

		return sb.toString();
		
		
	}

	private String ToDotPM(PrismModel pm, Input data) {
		StringBuilder sb = new StringBuilder();
		sb.append(modelType + " \n \n");

		// module
		sb.append("module " + fileName + "\n");
		
		sb.append("s:[1.." + pm.getNumOfPrismStates() + "] init " + pm.getInitialStates().get(0).getId() + "; \n"); //

		// state transitions
		for(int i=0; i<pm.getNumOfPrismStates(); i++){
			PrismState currentPS = pm.getPrismStates().get(i); // get a state 
			sb.append("[]s=" + currentPS.getId() + " -> ");
			if(currentPS.getNextStates().size()!=0){ // next states are nonempty
				for(int j=0; j<currentPS.getNextStates().size(); j++){
					if(j==0){
						sb.append(currentPS.getTransitionProb().get(j) + " :(s'=" + currentPS.getNextStates().get(j).getId() + ")");
						continue;
					}
					sb.append(" + " + currentPS.getTransitionProb().get(j) + " :(s'=" + currentPS.getNextStates().get(j).getId() + ")");
				}
				sb.append(";\n");
			}
			else{
				sb.append("1:(s'=" + currentPS.getId() + ")");
				sb.append(";\n");
			}
		}
		sb.append("endmodule \n\n");

		// abstract predicates labels
		if(!Config.EGL){
			for(int i=0; i<pm.getPredicates().size(); i++){
				Predicate pre = pm.getPredicates().get(i);
				List<Integer> validStateID = new ArrayList<Integer>();
				for(PrismState ps : pm.getPrismStates()){ // find all valid states for current atomic proposition
					String curState = ps.getCurrentState();
					if(curState.equals("empty") || curState.equals("")){ // take care of initial state: empty
						if(pre.getPredicateName().equals("hold")){ // if true predicate, then holds, others not
							validStateID.add(ps.getId());
						}
						continue;
					}
					if(curState.charAt(i)=='1'){
						validStateID.add(ps.getId());
					}
				}
				
				if(validStateID.size()!=0){
					sb.append("label \"" + pre.getPredicateName()+"\" = s=" + validStateID.get(0) );
					if(validStateID.size()>1){
						for(int k=1; k<validStateID.size(); k++){
							sb.append("|s=" + validStateID.get(k));
						}
					}
					sb.append(";\n");
				}
				else{
					sb.append("label \"" + pre.getPredicateName()+"\" = s=0;\n" ); // non-reachable
				}
			}
		}
		else{ // take care of egl
			List<Integer> validUnfairAIndex = new ArrayList<>();
			List<Integer> validUnfairBIndex = new ArrayList<>();
			for(PrismState ps : pm.getPrismStates()){
				String curState = ps.getCurrentState();
				if(curState.equals("empty") || curState.equals("")){
					continue;
				}
				if(curState.substring(1, 2).equals("1") && curState.substring(2, 3).equals("0")){
					validUnfairBIndex.add(ps.getId());
				}
				else if(curState.substring(1, 2).equals("0") && curState.substring(2, 3).equals("1")){
					validUnfairAIndex.add(ps.getId());
				}
			}
			
			if(validUnfairAIndex.size()!=0){
				sb.append("label \" unfairA \" = s=" + validUnfairAIndex.get(0));
				if(validUnfairAIndex.size()>1){
					for(int k=1; k<validUnfairAIndex.size(); k++){
						sb.append("|s=" + validUnfairAIndex.get(k));
					}
				}
				sb.append(";\n");
			}
			else{
				sb.append("label \" unfairA \" = s=0;\n" ); // non-reachable
			}
			
			if(validUnfairBIndex.size()!=0){
				sb.append("label \" unfairB \" = s=" + validUnfairBIndex.get(0));
				if(validUnfairBIndex.size()>1){
					for(int k=1; k<validUnfairBIndex.size(); k++){
						sb.append("|s=" + validUnfairBIndex.get(k));
					}
				}
				sb.append(";\n");
			}
			else{
				sb.append("label \" unfairB \" = s=0;\n" ); // non-reachable
			}
		}

		return sb.toString();
	}


}
