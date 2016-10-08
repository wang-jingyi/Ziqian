package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SwatTraceTest {
	
	@Test
	public void testSwatTrace() throws FileNotFoundException{
		
		SwatSensorAbstraction ssa = new SwatSensorAbstraction();
		ssa.addSensor("LIT101", new Interval(200, 1100), 500);
		ssa.addSensor("LIT301", new Interval(300, 1000), 500);
		ssa.addSensor("LIT401", new Interval(300, 1100), 500);
		ssa.addSensor("LS601", new Interval(400, 3300), 500);
		ssa.addSensor("LS602", new Interval(400, 600), 100);
		
		List<SwatTrace> sts = new ArrayList<SwatTrace>();
		for(String s : FileUtil.filesInDir("/Users/jingyi/ziqian/lar/swat/10,5/paths")){
			SwatTrace st = new SwatTrace(s);
			st.collectTraceFromPath(ssa);
			sts.add(st);
		}
		
		SwatStatePool ssp = new SwatStatePool(sts);
		ssp.buildPool();
		System.out.println(ssp);
		System.out.println("state number: " + ssp.getStateNumber());
	}

}
