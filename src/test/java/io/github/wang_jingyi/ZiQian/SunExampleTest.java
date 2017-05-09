package io.github.wang_jingyi.ZiQian;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.wang_jingyi.ZiQian.learn.Alergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;

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
		String modelName = "sunexample";

		LearningDTMC dtmc = new Alergia(0.8);
		dtmc.learn(data);
		dtmc.PrismModelTranslation(data, pres, "sunexample"); //

		// format to .pm file
		System.out.println("--- Format to PRISM file");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", "/Users/jingyi" , modelName);
		fpm.translateToFormat(dtmc.getPrismModel(), data);
		System.out.println("====== End of the program ======");
	}

}
