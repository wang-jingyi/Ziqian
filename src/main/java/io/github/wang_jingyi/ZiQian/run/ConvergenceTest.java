package io.github.wang_jingyi.ZiQian.run;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConvergenceTest {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {

		Config.initConfig();
		@SuppressWarnings("unchecked")
		List<Predicate> pres = (List<Predicate>) FileUtil.readObject(Config.OUTPUT_MODEL_PATH+"/predicates");

		//		if(FileUtil.isDirEmpty(Config.DATA_PATH_LOT)){
		//			for(int i=1; i<=2000; i++){
		//				System.out.println("simulation: " + i);
		//				Simulation sim = new Simulation(Config.ORIG_MODEL_FILE, Config.DATA_PATH_LOT, "path"+i, Config.MODEL_SETTING);
		//				sim.run();
		//			}
		//		}


		List<String> varsSet = PrismPathData.extractPathVars(Config.DATA_PATH, Config.DELIMTER);
		//
		ExtractPrismData epd_lot = new ExtractPrismData(Config.DATA_PATH, Config.CONVERGE_TEST_DATA_SIZE, Config.STEP_SIZE, Config.DELIMTER);
		VariablesValueInfo vvi_lot = epd_lot.getVariablesValueInfo(varsSet);

		long start_time = System.nanoTime();
		learn(pres, vvi_lot, Config.LEARN_METHOD+ "_lot");
		long end_time = System.nanoTime();
		System.out.println("total time : " + TimeProfile.nanoToSeconds(end_time-start_time) + " s");

	}


	private static void learn(List<Predicate> pres, VariablesValueInfo vvi, String name) throws IOException, ClassNotFoundException{

		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvi.getVarsValues());

		String modelName = Config.MODEL_NAME + "_" + name;

		ModelSelection gs = new AAlergia(Math.pow(2, -6), Math.pow(2, 6)); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, pres, modelName); //

		//		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
		//		bestDTMC.learn(data);
		//		bestDTMC.PrismModelTranslation(data, ps, modelName);

		// format to .pm file
		System.out.println("formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.OUTPUT_MODEL_PATH , modelName);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);

	}

}
