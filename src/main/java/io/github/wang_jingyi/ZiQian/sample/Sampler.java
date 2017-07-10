package io.github.wang_jingyi.ZiQian.sample;

public interface Sampler {
	
	public boolean isObtainingNewSample(); // if new samples are sampled, or just use training data
	public boolean isDecomposed(); // if the trace is decomposed from single trace
	public void sample(); // make a new sample
	public String getOutputFilePath(); // return directory holding the new samples
	public String getLatestSample(); // return file path of the latest sample
	
}
