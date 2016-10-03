package io.github.wang_jingyi.ZiQian;

import java.util.List;

/*
 * implements predicate set for abstraction
 * initially, it should include two predicates from the property which represents phi and psai respectively,
 * it will take two bits in a letter of the alphabet
 * 
 * */

public class PredicateSet{
	
	private List<Predicate> predicates;
	
	public PredicateSet(List<Predicate> pres) {
		this.predicates = pres;
	}

		
	public void updatePredicateSet(Predicate newPredicate){
		predicates.add(newPredicate);
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}
	
}
