package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultProcessor {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException{
//		int[] samplesizes = new int[]{50000,100000,150000,200000};
//		double thres = 0.1;
//		List<Double> idoresults = new ArrayList<Double>();
//		List<Double> rsresults = new ArrayList<Double>();
//		for(int ss : samplesizes){
//			double[] result = computeRMCReachabilityResult("rmc8", 8, ss, 20, thres);
//			idoresults.add(result[0]);
//			rsresults.add(result[1]);
//		}
//		System.out.println("ido results: " + idoresults);
//		System.out.println("rs results: " + rsresults);
		computeSwatResult(64, 5001, 0.01);
		
		
	}
	
	@SuppressWarnings("unchecked")
	public static void computeSwatResult(int state_number, int new_sample_number, double thres) throws FileNotFoundException, ClassNotFoundException, IOException{
		String name_suffix = "state_" + state_number;
		String actual_result_root = PlatformDependent.CAV_MODEL_ROOT +  "/active/swat/" + "swat_"+Config.SWAT_SAMPLE_STEP+"_"+
				Config.SWAT_RECORD_STEP + "/" + name_suffix;
		String result_root = PlatformDependent.CAV_MODEL_ROOT +  "/active/swat/" + "swat_"+Config.SWAT_SAMPLE_STEP+"_"+
				Config.SWAT_RECORD_STEP + "/" + name_suffix + "/new_" + new_sample_number;
		String ido_result_root = result_root+"/ido";
		String rs_result_root = result_root+"/rs";
		
		
		List<Double> swatReachProbs = (List<Double>) FileUtil.readObject(actual_result_root + "/swat_reach");
		List<Double> targetReachProbs = new ArrayList<Double>();
		for(int i=0; i<swatReachProbs.size(); i++){
			if(swatReachProbs.get(i)>0 && swatReachProbs.get(i)<1){ // only observe target states with small reachability
				targetReachProbs.add(swatReachProbs.get(i));
			}
		}
		
		List<Double> idoReachProbs = (List<Double>) FileUtil.readObject(ido_result_root + "/ido_reach");
		List<Double> rsReachProbs = (List<Double>) FileUtil.readObject(rs_result_root + "/rs_reach");
		List<Double> ido_rrd = ListUtil.listABSPercThresDiff(targetReachProbs, idoReachProbs, thres);
		List<Double> rs_rrd = ListUtil.listABSPercThresDiff(targetReachProbs, rsReachProbs, thres);
		System.out.println("ido rrd: " + ListUtil.listMean(ido_rrd));
		System.out.println("rs rrd: " +ListUtil.listMean(rs_rrd));
	}
	
	@SuppressWarnings("unchecked")
	public static double[] computeRMCReachabilityResult(String model, int statesize, int samplesize, int repeat, double thres) throws FileNotFoundException, ClassNotFoundException, IOException{
		
		double[] diff = new double[2];
		
		for(int i=0; i<repeat; i++){
			String pm = PlatformDependent.CAV_MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+".pm";
			String pctl = PlatformDependent.CAV_MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+".pctl";
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
			
			String idopm = PlatformDependent.CAV_MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+"_ido_"+samplesize+"_"+i+".pm";
			String rspm = PlatformDependent.CAV_MODEL_ROOT +"/active/rmc/" + model +"/rmc_"+statesize+"_"+i+"_rs_"+samplesize+"_"+i+".pm";
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
			String rspath = Config.TMP_PATH+"/"+model+"_"+i+"_rs_"+samplesize+"_reach";
			File rsfile = new File(rspath);
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
