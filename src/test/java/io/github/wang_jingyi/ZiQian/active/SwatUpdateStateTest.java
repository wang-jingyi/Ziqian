package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import org.junit.Test;

public class SwatUpdateStateTest {
	
	@Test
	public void testUpdateState(){
		int[][] frequencyMatrix = new int[][]{
				  { 2, 3},
				  { 3, 1}
				};
		int stateNumber = frequencyMatrix.length + GlobalVars.newStateNumber;
		int[][] newfrequencyMatrix = new int[stateNumber][stateNumber];
		System.arraycopy(frequencyMatrix, 0, newfrequencyMatrix, 0, newfrequencyMatrix.length);
		frequencyMatrix = newfrequencyMatrix;
		System.out.println(NumberUtil.twoDArrayToString(frequencyMatrix));
//		estimatedTransitionMatrix = new double[stateNumber][stateNumber];
	}

}
