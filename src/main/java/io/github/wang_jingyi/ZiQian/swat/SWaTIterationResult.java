package io.github.wang_jingyi.ZiQian.swat;

public class SWaTIterationResult {
	
	int iteration;
	int number_of_state;
	double training_unsafe_prob;
	double learned_unsafe_prob;
	double test_unsafe_prob;
	double iteration_time;

	public SWaTIterationResult() {
	}
	
	public SWaTIterationResult(int iteration, int number_of_state, double training_unsafe_prob, double learned_unsafe_prob, double test_unsafe_prob, double iteration_time) {
		this.iteration = iteration;
		this.number_of_state = number_of_state;
		this.training_unsafe_prob = training_unsafe_prob;
		this.learned_unsafe_prob = learned_unsafe_prob;
		this.test_unsafe_prob = test_unsafe_prob;
		this.iteration_time = iteration_time;
	}
	
	public void setTraining_unsafe_prob(double training_unsafe_prob) {
		this.training_unsafe_prob = training_unsafe_prob;
	}

	@Override
	public String toString() {
		return "SWaTIterationResult [iteration=" + iteration + ", number_of_state=" + number_of_state
				+ ", training_unsafe_prob=" + training_unsafe_prob + ", learned_unsafe_prob=" + learned_unsafe_prob
				+ ", test_unsafe_prob=" + test_unsafe_prob + ", iteration_time=" + iteration_time + "]";
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public void setNumber_of_state(int number_of_state) {
		this.number_of_state = number_of_state;
	}

	public void setLearned_unsafe_prob(double learned_unsafe_prob) {
		this.learned_unsafe_prob = learned_unsafe_prob;
	}

	public void setTest_unsafe_prob(double test_unsafe_prob) {
		this.test_unsafe_prob = test_unsafe_prob;
	}

	public void setIteration_time(double iteration_time) {
		this.iteration_time = iteration_time;
	}
	
	
	
}
