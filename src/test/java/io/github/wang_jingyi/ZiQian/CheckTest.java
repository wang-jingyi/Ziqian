package io.github.wang_jingyi.ZiQian;

import java.io.IOException;

import org.junit.Test;

import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;

public class CheckTest {
	
	@Test
	public void testCheckLearned() throws IOException, PrismNoResultException{
		String prismpf = "/Users/jingyi/ziqian/model/crowds/crowds_5000.pm";
		String prismporpf = "/Users/jingyi/ziqian/model/crowds/crowds_learn.pctl";
		int i = 1;
		
		CheckLearned cl = new CheckLearned(prismpf, prismporpf, i);
		cl.check();
	}
	
}
