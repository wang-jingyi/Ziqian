package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.learn.DataPrefix;
import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class FileIOTest {
	
	@Test
	public void testReadObj() throws FileNotFoundException, ClassNotFoundException, IOException{
		DataPrefix dp = (DataPrefix)FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/dataPrefix.ser");
		System.out.println("test reading: " + dp.getPrefixesTotalNum());
	}
	
}
