package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultProcessor {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException{
		int[] samplesizes = new int[]{50000,100000,150000,200000};
		List<Double> idoresults = new ArrayList<Double>();
		List<Double> rsresults = new ArrayList<Double>();
		for(int ss : samplesizes){
			double[] result = computeRMCReachabilityResult("rmc16", 16, ss, 20, 0.05);
			idoresults.add(result[0]);
			rsresults.add(result[1]);
		}
		System.out.println("ido results: " + idoresults);
		System.out.println("rs results: " + rsresults);
		
		
	}
	
	@SuppressWarnings("unchecked")
	public static double[] computeRMCReachabilityResult(String model, int statesize, int samplesize, int repeat, double thres) throws FileNotFoundException, ClassNotFoundException, IOException{
		
		double[] diff = new double[2];
		
		for(int i=0; i<repeat; i++){
			String pm = PlatformDependent.MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+".pm";
			String pctl = PlatformDependent.MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+".pctl";
			System.out.println("compute actual reachability...");
			
			List<Double> actual = new ArrayList<Double>();
			String actualpath = Config.TMP_PATH+"/"+model+"_"+i+"_reach";
			File actualfile = new File(actualpath);
			if(actualfile.exists()){
				actual = (List<Double>) FileUtil.readObject(actualpath);
			}
			else{
				actual = Reachability.computeReachability(pm, pctl, statesize/2);
				FileUtil.writeObject(actualpath, actual);
				
			}
			
			String idopm = PlatformDependent.MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+"_ido_"+samplesize+"_"+i+".pm";
			String rspm = PlatformDependent.MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+"_rs_"+samplesize+"_"+i+".pm";
			System.out.println("compute ido reachability...");
			
			List<Double> ido = new ArrayList<Double>();
			String idopath = Config.TMP_PATH+"/"+model+"_"+i+"_ido_"+samplesize+"_reach";
			File idofile = new File(idopath);
			if(idofile.exists()){
				ido = (List<Double>) FileUtil.readObject(idopath);
			}
			else{
				ido = Reachability.computeReachability(idopm, pctl, statesize/2);
				FileUtil.writeObject(idopath, ido);
			}
			
			System.out.println("compute rs reachability...");
			List<Double> rs = new ArrayList<Double>();
			String rspath = Config.TMP_PATH+"/"+model+"_"+i+"rs_"+samplesize+"_reach";
			File rsfile = new File(idopath);
			if(rsfile.exists()){
				rs = (List<Double>) FileUtil.readObject(rspath);
			}
			else{
				rs = Reachability.computeReachability(rspm, pctl, statesize/2);
				FileUtil.writeObject(rspath, rs);
			}
			
			List<Double> idodiff = ListUtil.listABSPercThresDiff(actual, ido, thres);
			double idomeandiff = ListUtil.listMean(idodiff);
			
			List<Double> rsdiff = ListUtil.listABSPercThresDiff(actual, rs, thres);
			double rsmeandiff = ListUtil.listMean(rsdiff);
			
			diff[0] = diff[0] + idomeandiff;
			diff[1] = diff[1] + rsmeandiff;
		}
		
		diff[0] = diff[0]/repeat;
		diff[1] = diff[1]/repeat;
		
		return diff;
		
	}
	
}
