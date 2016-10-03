package io.github.wang_jingyi.ZiQian;

import java.io.FileNotFoundException;

import org.junit.Test;

public class CheckTest {
	
	@Test
	public void testCheckLearned() throws FileNotFoundException{
		String prismpf = "/Users/jingyi/ziqian/model/crowds/crowds_5000.pm";
		String prismporpf = "/Users/jingyi/ziqian/model/crowds/crowds_learn.pctl";
		int i = 1;
		
		CheckLearned cl = new CheckLearned(prismpf, prismporpf, i);
		cl.check();
	}
	
}
