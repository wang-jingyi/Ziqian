package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SunExampleTest {

	@Test
	public void testExample() throws FileNotFoundException, ClassNotFoundException, IOException{

		List<String> alpha = new ArrayList<String>();
		alpha.add("0");
		alpha.add("1");

		List<List<String>> obss = new ArrayList<List<String>>();
		for(int i=0; i<88; i++){
			List<String> obs = new ArrayList<String>();
			obs.add("0");
			obs.add("0");
			obs.add("0");
			obs.add("0");
			obss.add(obs);
		}

		for(int i=0; i<2; i++){
			List<String> obs = new ArrayList<String>();
			obs.add("0");
			obs.add("0");
			obs.add("0");
			obs.add("1");
			obss.add(obs);
		}

		for(int i=0; i<2; i++){
			List<String> obs = new ArrayList<String>();
			obs.add("0");
			obs.add("0");
			obs.add("1");
			obss.add(obs);
		}

		for(int i=0; i<8; i++){
			List<String> obs = new ArrayList<String>();
			obs.add("0");
			obs.add("0");
			obs.add("1");
			obs.add("1");
			obss.add(obs);
		}

		Input data = new Input(alpha, obss);
		Predicate pre = new SunExamplePredicate();
		List<Predicate> pres = new ArrayList<Predicate>();
		pres.add(pre);
		PredicateSet ps = new PredicateSet(pres);
		String modelName = "sunexample";

		ModelSelection gs = new AAlergia(Math.pow(2, -6), Math.pow(2, 6)); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, ps, "sunexample"); //

		// format to .pm file
		System.out.println("formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", "/Users/jingyi" , modelName);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
	}

}
