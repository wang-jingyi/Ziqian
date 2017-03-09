package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.VariablesValue;

import java.io.IOException;
import java.util.List;

public class TestEnvironment {
	
	public static final TestEnvironment te = new TestEnvironment();
	private PredicateSet ps;
	private Sampler sampler;
	
	private TestEnvironment(){
		
	}
	
	public void init(PredicateSet ps, Sampler sampler){
		this.ps = ps;
		this.sampler = sampler;
	}
	
	public boolean test(List<VariablesValue> vvs, Counterexample ce) throws IOException, ClassNotFoundException{
//		List<VariablesValue> vvs = PrismPathData.extractSEData(sampler.getLastestSample(), 
//				AlgoProfile.vars,Integer.MAX_VALUE,Config.STEP_SIZE); // variables values of last simulation
		PredicateAbstraction pa = new PredicateAbstraction(ps.getPredicates());
		List<String> absExs = pa.abstractList(vvs);
		return ce.checkMembership(absExs);
	}
	
	public Sampler getSampler() {
		return sampler;
	}
	
	public PredicateSet getPredicateSet() {
		return ps;
	}

	
}
