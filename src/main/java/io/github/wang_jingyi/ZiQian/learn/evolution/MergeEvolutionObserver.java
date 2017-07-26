package io.github.wang_jingyi.ZiQian.learn.evolution;

import java.util.ArrayList;
import java.util.List;

import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

public class MergeEvolutionObserver implements EvolutionObserver<int[]> {
	
	private List<Double> popBestCandFitness = new ArrayList<Double>();
	private double bestCandFitness = 0;
	private int[] bestCandidate;
	
	public void populationUpdate(PopulationData<? extends int[]> data) {
		popBestCandFitness.add(data.getBestCandidateFitness());
		if(data.getBestCandidateFitness()>bestCandFitness){
			bestCandFitness = data.getBestCandidateFitness();
			bestCandidate = data.getBestCandidate();
		}
		
		System.out.printf("Generation %d: %s\n",
				data.getGenerationNumber(),
				data.getBestCandidate());
	}

	public List<Double> getPopBestCandFitness() {
		return popBestCandFitness;
	}

	public void setPopBestCandFitness(List<Double> popBestCandFitness) {
		this.popBestCandFitness = popBestCandFitness;
	}

	public double getBestCandFitness() {
		return bestCandFitness;
	}

	public void setBestCandFitness(double bestCandFitness) {
		this.bestCandFitness = bestCandFitness;
	}

	public int[] getBestCandidate() {
		return bestCandidate;
	}

	public void setBestCandidate(int[] bestCandidate) {
		this.bestCandidate = bestCandidate;
	}

}
