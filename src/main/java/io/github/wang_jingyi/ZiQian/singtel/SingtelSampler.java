package io.github.wang_jingyi.ZiQian.singtel;

import io.github.wang_jingyi.ZiQian.refine.Sampler;

public class SingtelSampler implements Sampler{
	
	int sample_count = 0;

	@Override
	public boolean isObtainingNewSample() {
		return true;
	}

	@Override
	public boolean isDecomposed() {
		return false;
	}

	@Override
	public void sample() {
		sample_count ++;
	}

	@Override
	public String getOutputFilePath() {
		return SingtelConfig.TESTING_PATH;
	}

	@Override
	public String getLatestSample() {
		return SingtelConfig.TESTING_PATH+"/ds_" + (667+sample_count) + ".csv";
	}

	@Override
	public void setOutputFilePath(String outputFilePath) {
		return;
	}

}
