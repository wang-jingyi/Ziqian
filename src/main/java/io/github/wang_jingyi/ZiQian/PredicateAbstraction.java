package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PredicateAbstraction implements Abstraction{

	List<Predicate> predicates;

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public PredicateAbstraction(List<Predicate> pres) {
		this.predicates = pres;
	}
	
	public void addNewPredicate(Predicate newPres){
		predicates.add(newPres);
	}
	
	public Input abstractInput(List<List<VariablesValue>> vvll){
		List<List<String>> obss = new ArrayList<>();
		for(List<VariablesValue> vvl : vvll){
			List<String> abs_l = abstractList(vvl);
			obss.add(abs_l);
		}
		
		List<String> alpha = new ArrayList<>();
		for(List<String> obs : obss){
			for(String s : obs){
				if(StringUtil.getStringIndex(s, alpha)==-1){
					alpha.add(s);
				}
			}
		}
		System.out.println("------Data information------");
		System.out.println("- alphabet: " + alpha);
		System.out.println("- alphabet size: " + alpha.size());
		return new Input(alpha, obss);
	}

	@Override
	public List<String> abstractList(List<VariablesValue> vvl) {
		List<String> obs = new ArrayList<>();
		for(VariablesValue vv : vvl){
			String abs_s = abstractVariablesValue(vv, predicates);
			obs.add(abs_s);
		}

		return obs;
	}

	private String abstractVariablesValue(VariablesValue vv, List<Predicate> predicates){
		String abs_s = "";
		for(Predicate pre : predicates){
			if(pre.check(vv)){
				abs_s += "1";
			}
			else{
				abs_s += "0";
			}
		}
		return abs_s;
	}

}
