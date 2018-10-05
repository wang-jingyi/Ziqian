package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConvergenceTest {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {

		String pres_path = args[0];
		String data_path = args[1];
		String output_path = args[2];
		int data_size = Integer.valueOf(args[3]);
		
		@SuppressWarnings("unchecked")
		List<Predicate> pres = (List<Predicate>) FileUtil.readObject(pres_path);
		
		// for swat
		boolean random_length = true;
		long random_seed = 379824;
		
		List<String> varsSet = PrismPathData.extractPathVars(data_path, " ");
		ExtractPrismData epd_lot = new ExtractPrismData(data_path, data_size, 1, " ", random_length, random_seed);
		VariablesValueInfo vvi_lot = epd_lot.getVariablesValueInfo(varsSet);

		long start_time = System.nanoTime();
		learn(pres, vvi_lot, output_path, "AA_lot");
		long end_time = System.nanoTime();
		System.out.println("--- Total time : " + TimeProfile.nanoToSeconds(end_time-start_time) + " s");

	}


	private static void learn(List<Predicate> pres, VariablesValueInfo vvi, String output_path, String name) throws IOException, ClassNotFoundException{

		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvi.getVarsValues());

		System.out.println("*** Learning ***");
		ModelSelection gs = new AAlergia(1, 64); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, pres, name); //

		// format to .pm file
		System.out.println("- Formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", output_path, name);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);

	}

}
