package io.github.wang_jingyi.ZiQian.prism;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import io.github.wang_jingyi.ZiQian.main.Global;

public class ExportPrismMC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2113072541400192913L;
	String dir_path; // directory path containing the exported PRISM model files
	String file_name; // file name of the exported files
	int state_number;
	int transition_number;
	List<Integer> target_states = new ArrayList<>();
	RealVector init_dist;
	RealMatrix transition_matrix;
	boolean sparse;
	
	public ExportPrismMC(String dp, String fn) {
		this.dir_path = dp;
		this.file_name = fn;
		countStateTransitionNumber();
		if(sparse){
			transition_matrix = new OpenMapRealMatrix(state_number, state_number);
			init_dist = new OpenMapRealVector(state_number);
		}
		else{
			transition_matrix = MatrixUtils.createRealMatrix(state_number, state_number);
			init_dist = MatrixUtils.createRealVector(new double[state_number]);
		}
	}
	
	public void execute() throws IOException{
		getTransitionMatrix();
		getInitTargetStates();
	}


	private void getInitTargetStates() throws IOException {
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(dir_path+"/"+file_name+".lab"));
			line = stream.readLine();
			String[] splitted = line.split(Global.PRISM_DELIMITER);
			String init_ind = null;
			String target_ind = null;
			for(String index : splitted){
				if(index.endsWith("init\"")){
					String[] tmp_split = index.split("=");
					init_ind = tmp_split[0];
					continue;
				}
				if(index.endsWith("swat_error\"")){ // may support a set of labels later
					String[] tmp_split = index.split("=");
					target_ind = tmp_split[0];
					continue;
				}
			}
			
			List<Integer> init_states = new ArrayList<>();
			while ((line = stream.readLine()) != null) {
				if(line.endsWith(init_ind)){
					int len = line.length();
					line = line.substring(0, len-3);
					init_states.add(Integer.valueOf(line));
					continue;
				}
				if(line.endsWith(target_ind)){
					int len = line.length();
					line = line.substring(0, len-3);
					String[] ls = line.split(":");
					target_states.add(Integer.valueOf(ls[0]));
					continue;
				}
			}
			
			int init_state_num = init_states.size();
			double uniform_init_dist_prob = (double)1/init_state_num;
			for(int i=0; i<init_state_num; i++){
				init_dist.setEntry(init_states.get(i), uniform_init_dist_prob);
			}
			
		} finally {
			if (stream != null)
				stream.close();
		}
	}


	private void getTransitionMatrix() throws IOException {
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(dir_path+"/"+file_name+".tra"));
			stream.readLine();
			while ((line = stream.readLine()) != null) {
				String[] splitted = line.split(Global.PRISM_DELIMITER);
				int start_state = Integer.valueOf(splitted[0]); // first number is the start state
				int end_state = Integer.valueOf(splitted[1]); // second number is the end state
				double trans_prob = Double.valueOf(splitted[2]); // third number is the transition probability
				transition_matrix.setEntry(start_state, end_state, trans_prob);
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		
		double[] row_sums = new double[state_number];
		for(int i=0; i<state_number; i++){
			double[] row = transition_matrix.getRow(i);
			double row_sum = 0;
			for(double d : row){
				row_sum += d;
			}
			row_sums[i] = row_sum;
		}
		for(int i=0; i<state_number; i++){
			for(int j=0; j<state_number; j++){
				double orig = transition_matrix.getEntry(i, j);
				transition_matrix.setEntry(i, j, orig/row_sums[i]);
			}
		}
	}


	private void countStateTransitionNumber() {
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(dir_path+"/"+file_name+".tra"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			line = stream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] splitted = line.split(Global.PRISM_DELIMITER);
		state_number = Integer.valueOf(splitted[0]);
		transition_number = Integer.valueOf(splitted[1]);
		if((double)transition_number/(state_number*state_number)<Global.SPARSE_THRESHOLD){
			sparse = true;
		}
	}
	
	@Override
	public String toString() {
		return "ExportPrismMC [dir_path=" + dir_path + ", file_name=" + file_name + ", state_number=" + state_number
				+ ", transition_number=" + transition_number + ", target_states=" + target_states + ", init_dist="
				+ init_dist + ", transition_matrix=" + transition_matrix + ", sparse=" + sparse + "]";
	}
	
	public RealVector getInit_dist() {
		return init_dist;
	}
	
	public String getDir_path() {
		return dir_path;
	}

	public String getFile_name() {
		return file_name;
	}

	public int getState_number() {
		return state_number;
	}

	public int getTransition_number() {
		return transition_number;
	}

	public List<Integer> getTarget_states() {
		return target_states;
	}

	public RealMatrix getTransition_matrix() {
		return transition_matrix;
	}

	public boolean isSparse() {
		return sparse;
	}
	
}
