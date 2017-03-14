package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;

import java.io.IOException;
import java.util.List;

public class TestEnvironment {
	
	public static final TestEnvironment te = new TestEnvironment();
	private List<Predicate> predicates;
	private Sampler sampler;
	
	private TestEnvironment(){
		
	}
	
	public void init(List<Predicate> predicates, Sampler sampler){
		this.predicates = predicates;
		this.sampler = sampler;
	}
	
	public boolean test(List<VariablesValue> vvs, Counterexample ce) throws IOException, ClassNotFoundException{
//		List<VariablesValue> vvs = PrismPathData.extractSEData(sampler.getLastestSample(), 
//				AlgoProfile.vars,Integer.MAX_VALUE,Config.STEP_SIZE); // variables values of last simulation
		PredicateAbstraction pa = new PredicateAbstraction(predicates);
		List<String> absExs = pa.abstractList(vvs);
		return ce.checkMembership(absExs);
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}

	public Sampler getSampler() {
		return sampler;
	}
	

	
}
